package com.moesee.moeseedemo.consumer;

import com.moesee.moeseedemo.mapper.SeckillMapper;
import com.moesee.moeseedemo.pojo.VoucherOrder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RabbitOrderConsumer {

    @Autowired
    private SeckillMapper seckillMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RabbitListener(queues = "order.delay.queue") //超时消费者
    public void handleDelayedOrder(Map<String, String> message) {
        System.out.println("接收到延时订单消息：" + message);

        Long orderId = Long.valueOf(message.get("Id"));
        Long voucherId = Long.valueOf(message.get("voucherId"));
        String  userUid = message.get("userUid");

        // 检查订单是否已支付
        String redisKey = "order:voucher:" + voucherId + ":" + userUid;
        String status = (String) stringRedisTemplate.opsForHash().get(redisKey, "status");
        if ("未支付".equals(status)) {
            // 超时未支付，释放库存并移除用户信息
            stringRedisTemplate.delete(redisKey);
            System.out.println("删除了缓存key");
            stringRedisTemplate.opsForHash().increment("seckill:voucher:" + voucherId, "stock", 1);
            System.out.println("释放了库存");
            stringRedisTemplate.opsForSet().remove("order:voucher:" + voucherId, userUid);
            System.out.println("订单超时未支付，已释放库存，订单 ID：" + orderId);
        }
    }

    @RabbitListener(queues="order.success.queue") //"成功消费者"
    public void handleSuccessOrder(Map<String,String> message){
        try{

            System.out.println("监听到订单消息(支付成功):"+ message);
            //从订单中提取信息
            Long orderId=Long.valueOf(message.get("Id"));
            Long voucherId = Long.valueOf((String) message.get("voucherId"));
            int userUid = Integer.parseInt((String) message.get("userUid"));
            VoucherOrder order = new VoucherOrder(orderId,voucherId,userUid);
            String redisVoucherSeckilledKey = "order:voucher:" + voucherId + ":" + userUid;
            System.out.println("获取消息成功,准备写入数据库,同时删除redis中订单缓存...");
            stringRedisTemplate.delete(redisVoucherSeckilledKey);
            seckillMapper.createOrder(order);
            System.out.println("订单处理完成，订单ID：" + orderId);

        }catch (Exception e){
            System.out.println("订单处理失败：" + message);
            e.printStackTrace();
        }
    }

    @RabbitListener(queues="order.fail.queue") //"失败消费者"
    public void handleFailOrder(Map<String,String> message){
        try{
            System.out.println("监听到订单消息(支付失败):"+ message);
            Long orderId=Long.valueOf(message.get("Id"));
            Long voucherId = Long.valueOf((String) message.get("voucherId"));
            int userUid = Integer.parseInt((String) message.get("userUid"));
            String redisVoucherSeckilledKey = "order:voucher:" + voucherId + ":" + userUid;
            System.out.println("获取消息成功,准备释放库存,同时删除redis中订单缓存...");
            stringRedisTemplate.delete(redisVoucherSeckilledKey);
            stringRedisTemplate.opsForHash().increment("seckill:voucher:" + voucherId, "stock", 1);
            stringRedisTemplate.opsForSet().remove("order:voucher:" + voucherId, userUid);
            System.out.println("用户取消了订单，已释放库存，订单 ID：" + orderId);
        } catch (Exception e) {
            System.out.println("订单处理失败：" + message);
            e.printStackTrace();
        }
    }
}

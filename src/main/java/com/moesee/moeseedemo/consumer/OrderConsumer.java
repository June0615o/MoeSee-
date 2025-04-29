/*
package com.moesee.moeseedemo.consumer;

import com.moesee.moeseedemo.mapper.SeckillMapper;
import com.moesee.moeseedemo.pojo.VoucherOrder;
import com.moesee.moeseedemo.utils.RedisIdWorker;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class OrderConsumer {

    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String streamKey = "stream.orders";
    private String consumerGroup = "group.orders";
    private String consumerName = "consumer-1";

    @PostConstruct
    public void init() {
        // 确保消费者组存在
        try {
            stringRedisTemplate.opsForStream().createGroup(streamKey, consumerGroup);
        } catch (Exception e) {
            System.out.println("消费者组已存在，无需创建");
        }

        // 启动消费逻辑
        new Thread(() -> {
            while (true) {
                consumeMessages();
            }
        }).start();
    }

    public void consumeMessages() {
        try {
            // 拉取消息
            System.out.println("开始拉取消息...");
            List<MapRecord<String, Object, Object>> messages = stringRedisTemplate.opsForStream().read(
                    Consumer.from(consumerGroup, consumerName),
                    StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                    StreamOffset.create(streamKey, ReadOffset.lastConsumed())
            );
            System.out.println("拉取消息完成，消息数量：" + (messages != null ? messages.size() : 0));

            if (messages != null && !messages.isEmpty()) {
                for (MapRecord<String, Object, Object> message : messages) {
                    System.out.println("处理消息：" + message.getValue());
                    handleOrder(message.getValue());
                    stringRedisTemplate.opsForStream().acknowledge(consumerGroup, message); // 确认消息
                }
            }
        } catch (Exception e) {
            System.err.println("消费消息出现异常");
            e.printStackTrace();
        }
    }

    private void handleOrder(Map<Object, Object> message) {
        try {
            System.out.println("消息内容：" + message);
            Long orderId = Long.valueOf((String) message.get("Id"));
            Long voucherId = Long.valueOf((String) message.get("voucherId"));
            int userUid = Integer.parseInt((String) message.get("userUid"));

            // 订单入库逻辑
            VoucherOrder order = new VoucherOrder(orderId, voucherId, userUid);
            System.out.println("准备写入数据库...");
            seckillMapper.createOrder(order);
            seckillMapper.decrementStock(voucherId);
            System.out.println("订单处理完成，订单ID：" + orderId);
        } catch (Exception e) {
            System.err.println("订单处理失败：" + message);
            e.printStackTrace();
        }
    }
}

 */


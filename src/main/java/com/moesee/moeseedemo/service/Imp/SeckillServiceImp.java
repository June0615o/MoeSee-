package com.moesee.moeseedemo.service.Imp;

import com.moesee.moeseedemo.dto.SeckillVoucherDTO;
import com.moesee.moeseedemo.mapper.SeckillMapper;
import com.moesee.moeseedemo.mapper.UserMapper;
import com.moesee.moeseedemo.service.SeckillService;
import com.moesee.moeseedemo.utils.RedisIdWorker;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImp implements SeckillService {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    SeckillMapper seckillMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisIdWorker redisIdWorker;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Override
    public boolean addSeckillVoucher(SeckillVoucherDTO seckillVoucherDTO) {
        // 秒杀券是热点key. 将其信息保存到数据库的同时,缓存至redis.
        int rows= seckillMapper.insertSeckillVoucher(seckillVoucherDTO);
        if(rows<=0){
            return false;
        }else {
            String redisKey = "seckill:voucher:" + seckillVoucherDTO.getVoucherId();
            Map<String, Object> data = new HashMap<>();
            data.put("stock", String.valueOf(seckillVoucherDTO.getStock()));
            long beginTimestamp = LocalDateTime.parse(seckillVoucherDTO.getBeginTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    .toEpochSecond(ZoneOffset.UTC);
            long endTimestamp = LocalDateTime.parse(seckillVoucherDTO.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    .toEpochSecond(ZoneOffset.UTC);
            data.put("beginTime", String.valueOf(beginTimestamp));
            data.put("endTime", String.valueOf(endTimestamp));
            stringRedisTemplate.opsForHash().putAll(redisKey, data);
            return true;
        }
    }

    @Override
    public Long trySeckill(Long voucherId, int userUid) {
        String luaScript = loadLuaScript("lua/seckill.lua");
        //直接查询秒杀券在redis中的热点key
        String voucherKey = "seckill:voucher:" + voucherId;
        String orderKey = "order:voucher:" + voucherId;
        long now = System.currentTimeMillis() / 1000;
        Long orderId = redisIdWorker.nextId("order");
        String redisVoucherSeckilledKey = "order:voucher:" + voucherId + ":" + userUid;
        Long ttlSeconds = 15 * 60 + 2L; // Redis TTL 设置为 15 分钟 2 秒
        /*
              资格OK.先在Redis中存放订单号与状态(未支付)的 Hash.
              键名为: order:voucher:{voucherId}:{userUid}
              同时扣减库存并将userUid加入到Set内（必须保证原子性）;
              同时发送TTL订单消息，并对接支付业务
        */
        //执行lua脚本
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript); // luaScript 是 Lua 脚本内容
        redisScript.setResultType(Long.class);
        Long result = stringRedisTemplate.execute(redisScript,
                List.of(voucherKey, orderKey, redisVoucherSeckilledKey),
                String.valueOf(userUid),
                String.valueOf(now),
                String.valueOf(orderId),
                String.valueOf(ttlSeconds));

        if(result !=null && result.equals(orderId)){
            /*
              一旦订单确定成功建立，发送TTL=15min 的延时消息.(redis缓存TTL=15min02s)
              发送TTL订单消息，并对接支付业务
             */
            stringRedisTemplate.opsForHash().put(redisVoucherSeckilledKey, "status", "未支付");

            sendOrderToDelayQueue(orderId,voucherId,userUid);
            System.out.println("[写入延时消息队列成功]");
            return result;
        }
        return result != null ? result : -99;
    }
    private String loadLuaScript(String path){
        try{
            ClassPathResource resource = new ClassPathResource(path);
            return Files.readString(Paths.get(resource.getURI()));
        }catch(IOException e){
            throw new RuntimeException("加载Lua脚本失败:"+ path , e);
        }
    }
    public void sendOrderToDelayQueue(Long orderId,Long voucherId, int userUid) {
        try {// 准备消息内容
            System.out.println("写入延时消息队列中");
            Map<String, String> message = new HashMap<>();
            message.put("Id", orderId.toString());
            message.put("voucherId", voucherId.toString());
            message.put("userUid", String.valueOf(userUid));

            rabbitTemplate.convertAndSend("order.delay","order.delay.key",message,msg->{
                msg.getMessageProperties().setHeader("x-delay", 15  * 60 * 1000);
                return msg;
            });
            System.out.println("写入延时消息队列成功"+message);
        } catch (Exception e) {
            System.err.println("写入延时消息队列失败：" + e.getMessage());
        }
    }
        /*String userLockKey="lock:seckill:user:"+voucherId+":"+userUid;
        RLock lock = redissonClient.getLock(userLockKey);
        try{
            userLock.lock(10, TimeUnit.SECONDS);
            //首先检查用户是否已经下单过
            String userOrderKey="seckill:user:"+voucherId+":"+userUid;
            Boolean hasOrdered = redisTemplate.opsForValue().get(userOrderKey) != null;
            if(hasOrdered){
                return false; //用户已经下单过
            }
            //检查库存
            Integer stock = (Integer)stringRedisTemplate.opsForHash().get(redisKey, "stock");
            if(stock==null||stock<=0){
                return false; //库存不足
            }
            //进入到此逻辑说明秒杀成功.应当扣减库存
            redisTemplate.opsForHash().increment(redisKey,"stock",-1);
            //记录用户已经下单
            stringRedisTemplate.opsForValue().set(userOrderKey,"1", Duration.ofHours(1));
            //创建订单
            int userId=userMapper.getUserIdByUid(userUid);
            seckillMapper.createOrder(voucherId,userId);

            return true;
        }finally{
            userLock.unlock();
        }
    }*/
}

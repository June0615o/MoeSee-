local voucherKey = KEYS[1] -- 秒杀券 Redis key
local orderKey = KEYS[2]   -- 已购买用户 Redis Set key
local redisOrderKey = KEYS[3] -- 订单 Redis Hash key
local userId = ARGV[1]     -- 用户 ID
local now = tonumber(ARGV[2]) -- 当前时间戳
local orderId = ARGV[3]    -- 订单号
local ttl = tonumber(ARGV[4]) -- TTL 秒数

-- 检查秒杀时间
local beginTime = tonumber(redis.call('hget', voucherKey, 'beginTime'))
local endTime = tonumber(redis.call('hget', voucherKey, 'endTime'))
if now < beginTime or now > endTime then
    return -1 -- 秒杀时间不合法
end

-- 检查库存
local stock = tonumber(redis.call('hget', voucherKey, 'stock'))
if stock <= 0 then
    return -2 -- 库存不足
end

-- 检查用户是否已下单
if redis.call('sismember', orderKey, userId) == 1 then
    return -3 -- 用户已下单过
end

-- 扣减库存并记录用户已下单（原子操作）
redis.call('hIncrBy', voucherKey, 'stock', -1)
redis.call('sadd', orderKey, userId)

-- 创建订单 Hash 并设置订单状态为 "未支付"
redis.call('hset', redisOrderKey, 'status', '未支付')

-- 设置订单的 TTL（自动超时过期处理）
redis.call('expire', redisOrderKey, ttl)

-- 返回订单号
return orderId

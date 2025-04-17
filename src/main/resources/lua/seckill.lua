local voucherKey = KEYS[1]
local orderKey= KEYS[2]
local userId = ARGV[1]
local now = tonumber(ARGV[2])
local orderId = ARGV[3]

--检查秒杀时间
local beginTime = tonumber(redis.call('hget',voucherKey, 'beginTime'))
local endTime = tonumber(redis.call('hget',voucherKey,'endTime'))
if now < beginTime or now > endTime then
    return -1 --秒杀时间不合法
end

--检查库存
local stock = tonumber(redis.call('hget',voucherKey,'stock'))
if stock <= 0 then
    return -2 --库存不足
end

--检查用户是否已下单
if redis.call('sismember',orderKey,userId)==1 then
    return -3 --用户已下单过
end

--扣减库存并记录用户已下单
redis.call('hIncrBy',voucherKey,'stock',-1)
redis.call('sadd',orderKey,userId)

--返回成功
return orderId


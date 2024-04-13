package com.redisson;

import org.redisson.Redisson;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class IndexController {

    @Autowired
    private Redisson redisson;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/deduct_stock")
    public String deductStock() {
        String lockKey = "lock:product_101";
        //Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "zhuge");
        //stringRedisTemplate.expire(lockKey, 10, TimeUnit.SECONDS);
        /*String clientId = UUID.randomUUID().toString();
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, clientId, 30, TimeUnit.SECONDS); //jedis.setnx(k,v)
        if (!result) {
            return "error_code";
        }*/
        //獲取鎖對象
        RLock redissonLock = redisson.getLock(lockKey);
        //加分佈式鎖
        redissonLock.lock();  //  .setIfAbsent(lockKey, clientId, 30, TimeUnit.SECONDS);
        try {
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock")); // jedis.get("stock")
            if (stock > 0) {
                int realStock = stock - 1;
                stringRedisTemplate.opsForValue().set("stock", realStock + ""); // jedis.set(key,value)
                System.out.println("扣減成功，剩餘庫存:" + realStock);
            } else {
                System.out.println("扣減失敗，庫存不足");
            }
        } finally {
            /*if (clientId.equals(stringRedisTemplate.opsForValue().get(lockKey))) {
                stringRedisTemplate.delete(lockKey);
            }*/
            //解鎖
            redissonLock.unlock();
        }


        return "end";
    }


    @RequestMapping("/redlock")
    public String redlock() {
        String lockKey = "product_001";
        //這裏需要自己實例化不同redis實例的redisson客戶端連接，這裏只是偽代碼用一個redisson客戶端簡化了
        RLock lock1 = redisson.getLock(lockKey);
        RLock lock2 = redisson.getLock(lockKey);
        RLock lock3 = redisson.getLock(lockKey);

        /**
         * 根據多個 RLock 對象構建 RedissonRedLock （最核心的差別就在這裏）
         */
        RedissonRedLock redLock = new RedissonRedLock(lock1, lock2, lock3);
        try {
            /**
             * waitTimeout 嘗試獲取鎖的最大等待時間，超過這個值，則認為獲取鎖失敗
             * leaseTime   鎖的持有時間,超過這個時間鎖會自動失效（值應設置為大於業務處理的時間，確保在鎖有效期內業務能處理完）
             */
            boolean res = redLock.tryLock(10, 30, TimeUnit.SECONDS);
            if (res) {
                //成功獲得鎖，在這裏處理業務
            }
        } catch (Exception e) {
            throw new RuntimeException("lock fail");
        } finally {
            //無論如何, 最後都要解鎖
            redLock.unlock();
        }

        return "end";
    }

}
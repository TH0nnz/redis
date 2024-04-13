package com.tuling.jedis.bf;

import com.google.common.hash.Funnels;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 訪問redis單機
 *
 */
public class JedisBloomFilterTest {
    public static void main(String[] args) throws IOException {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMaxIdle(5);
        jedisPoolConfig.setMinIdle(2);

        // timeout，這裏既是連接超時又是讀寫超時，從Jedis 2.8開始有區分connectionTimeout和soTimeout的構造函數
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "192.168.0.60", 6380, 3000, null);

        Jedis jedis = null;
        try {
            //從redis連接池裡拿出一個連接執行命令
            jedis = jedisPool.getResource();

            //******* Redis測試布隆方法 ********
            BloomFilterHelper<CharSequence> bloomFilterHelper = new BloomFilterHelper<>(Funnels.stringFunnel(Charset.defaultCharset()), 1000, 0.1);
            RedisBloomFilter<Object> redisBloomFilter = new RedisBloomFilter<>(jedis);
            int j = 0;
            for (int i = 0; i < 100; i++) {
                redisBloomFilter.addByBloomFilter(bloomFilterHelper, "bloom", i+"");
            }
            for (int i = 0; i < 1000; i++) {
                boolean result = redisBloomFilter.includeByBloomFilter(bloomFilterHelper, "bloom", i+"");
                if (!result) {
                    j++;
                }
            }
            System.out.println("漏掉了" + j + "個");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //注意這裏不是關閉連接，在JedisPool模式下，Jedis會被歸還給資源池。
            if (jedis != null)
                jedis.close();
        }
    }
}

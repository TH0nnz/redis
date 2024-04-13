package com.tuling.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 訪問redis單機
 */
public class JedisSingleTest {
    public static void main(String[] args) throws IOException {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMinIdle(5);

        // timeout，這裏既是連接超時又是讀寫超時，從Jedis 2.8開始有區分connectionTimeout和soTimeout的構造函數
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "10.211.55.5", 6379, 3000, "123456");

        Jedis jedis = null;
        try {
            //從redis連接池裡拿出一個連接執行命令
            jedis = jedisPool.getResource();

            //******* jedis普通操作示例 ********
            System.out.println(jedis.set("simple", "tom"));
            System.out.println(jedis.get("simple"));

            //******* 管道示例 ********
            //管道的命令執行方式：cat redis.txt | redis-cli -h 127.0.0.1 -a password - p 6379 --pipe
            Pipeline pl = jedis.pipelined();
            for (int i = 0; i < 10; i++) {
                pl.incr("pipelineKey");
                pl.set("pipe" + i, "tom");
                //模擬管道報錯 '操作bitmap 偏移量-1 會報錯'
                pl.setbit("pipe", -1, true);
            }//原子性示範 pl.incr("pipelineKey")跟pl.set("pipe" + i, "tom")都正常但是pl.setbit("pipe", -1, true)抱錯，還是能執行
            List<Object> results = pl.syncAndReturnAll();
            System.out.println(results);

            //******* lua腳本示例 ********`
            //模擬一個商品減庫存的原子操作
            //lua腳本命令執行方式：redis-cli --eval /tmp/test.lua , 10
            jedis.set("product_stock_lua101", "16");  //初始化商品lua101的庫存
            String script = " local count = redis.call('get', KEYS[1]) " +
                    " local a = tonumber(count) " +
                    " local b = tonumber(ARGV[1]) " +
                    " if a >= b then " +
                    "   redis.call('set', KEYS[1], a-b) " +
                    "   return 1 " +
                    " end " +
                    " return 0 ";
            ;
            Object obj = jedis.eval(script, Arrays.asList("product_stock_lua101"), Arrays.asList("6"));
            System.out.println(obj);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //注意這裏不是關閉連接，在JedisPool模式下，Jedis會被歸還給資源池。
            if (jedis != null)
                jedis.close();
        }
    }
}

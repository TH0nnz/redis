package com.tuling.jedis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 訪問redis哨兵集群
 *
 * @author 圖靈-諸葛老師
 */
public class JedisSentinelTest {
    public static void main(String[] args) throws IOException {

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(10);
        config.setMinIdle(5);


        String masterName = "mymaster";
        Set<String> sentinels = new HashSet<String>();
        sentinels.add(new HostAndPort("10.211.55.5",26379).toString());
        sentinels.add(new HostAndPort("10.211.55.5",26380).toString());
        sentinels.add(new HostAndPort("10.211.55.5",26381).toString());
        //JedisSentinelPool其實本質跟JedisPool類似，都是與redis主節點建立的連接池
        //JedisSentinelPool並不是說與sentinel建立的連接池，而是通過sentinel發現redis主節點並與其建立連接
        //JedisSentinelPool(String masterName, Set<String> sentinels, GenericObjectPoolConfig<Jedis> poolConfig, int timeout, String password)
        JedisSentinelPool jedisSentinelPool = new JedisSentinelPool(masterName, sentinels, config, 3000, "123456");

        Jedis jedis = null;
        try {
            jedis = jedisSentinelPool.getResource();
            System.out.println(jedis.set("sentinel_test", "tom_01"));
            System.out.println(jedis.get("sentinel_test"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //注意這裏不是關閉連接，在JedisPool模式下，Jedis會被歸還給資源池。
            if (jedis != null)
                jedis.close();
        }
    }
}

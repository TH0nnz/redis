package com.tuling.jedis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 訪問redis集群
 *
 */
public class JedisClusterTest {
    public static void main(String[] args) throws IOException {

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(10);
        config.setMinIdle(5);

        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort("10.211.55.21", 8001));
        jedisClusterNode.add(new HostAndPort("10.211.55.22", 8002));
        jedisClusterNode.add(new HostAndPort("10.211.55.23", 8003));
        jedisClusterNode.add(new HostAndPort("10.211.55.24", 8004));
        jedisClusterNode.add(new HostAndPort("10.211.55.25", 8005));
        jedisClusterNode.add(new HostAndPort("10.211.55.26", 8006));

        JedisCluster jedisCluster = null;
        try {
            //connectionTimeout：指的是連接一個url的連接等待時間
            //soTimeout：指的是連接上一個url，獲取response的返回等待時間
            jedisCluster = new JedisCluster(jedisClusterNode, 6000, 5000, 10, "123456", config);
            System.out.println(jedisCluster.set("cluster", "Tom_cluster_test"));
            System.out.println(jedisCluster.get("cluster"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedisCluster != null)
                jedisCluster.close();
        }
    }
}

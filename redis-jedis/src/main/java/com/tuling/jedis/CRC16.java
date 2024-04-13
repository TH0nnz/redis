package com.tuling.jedis;

//import redis.clients.jedis.util.JedisClusterCRC16;
import redis.clients.util.JedisClusterCRC16;

/**
 * redis分片算法
 *
 */
public class CRC16 {

    public static void main(String[] args){
        String str="name1";
        System.out.println(JedisClusterCRC16.getCRC16(str)%16384);
        System.out.println(JedisClusterCRC16.getCRC16(str)& (16384 - 1));
    }
    
}

package com.tuling.jedis.bf;

import redis.clients.jedis.Jedis;

public class RedisBloomFilter<T> {
    private Jedis jedis;

    public RedisBloomFilter(Jedis jedis) {
        this.jedis = jedis;
    }

    /**
     * 根據給定的布隆過濾器添加值
     */
    public <T> void addByBloomFilter(BloomFilterHelper<T> bloomFilterHelper, String key, T value) {
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        for (int i : offset) {
            //redisTemplate.opsForValue().setBit(key, i, true);
            jedis.setbit(key, i, true);
        }
    }

    /**
     * 根據給定的布隆過濾器判斷值是否存在
     */
    public <T> boolean includeByBloomFilter(BloomFilterHelper<T> bloomFilterHelper, String key, T value) {
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        for (int i : offset) {
            //if (!redisTemplate.opsForValue().getBit(key, i)) {
            if (!jedis.getbit(key, i)) {
                return false;
            }
        }
        return true;
    }
}
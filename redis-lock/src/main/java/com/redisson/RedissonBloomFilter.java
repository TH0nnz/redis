package com.redisson;

import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonBloomFilter {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        //構造Redisson
        RedissonClient redisson = Redisson.create(config);

        RBloomFilter<String> bloomFilter = redisson.getBloomFilter("nameList");
        //初始化布隆過濾器：預計元素為100000000L,誤差率為3%,根據這兩個參數會計算出底層的bit數組大小
        bloomFilter.tryInit(100000L,0.03);
        //將zhuge插入到布隆過濾器中
        bloomFilter.add("zhuge");
        bloomFilter.add("tuling");

        //判斷下面號碼是否在布隆過濾器中
        System.out.println(bloomFilter.contains("guojia"));//false
        System.out.println(bloomFilter.contains("baiqi"));//false
        System.out.println(bloomFilter.contains("zhuge"));//true
    }
}
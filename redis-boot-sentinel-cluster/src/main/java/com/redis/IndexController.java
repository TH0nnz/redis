package com.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 測試節點掛了哨兵重新選舉新的master節點，客戶端是否能動態感知到
     *
     * @throws InterruptedException
     */
//    @RequestMapping("/test_sentinel")
//    public void testSentinel() throws InterruptedException {
//        int i = 1;
//        while (true) {
//            try {
//                stringRedisTemplate.opsForValue().set("tom" + i, i + ""); //jedis.set(key,value);
//                System.out.println("設置key：" + "tom" + i);
//                i++;
//                Thread.sleep(1000);
//            } catch (Exception e) {
//                logger.error("錯誤：", e);
//            }
//        }
//    }
    @RequestMapping("/test_cluster/{petID}/{petValue}")
    public void testCluster(@PathVariable String petID, @PathVariable String petValue) {
        try {
            stringRedisTemplate.opsForValue().set(petID, petValue);
            System.out.println(stringRedisTemplate.opsForValue().get(petID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


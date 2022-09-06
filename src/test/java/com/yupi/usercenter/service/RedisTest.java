package com.yupi.usercenter.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * @author author
 * @date 2022-08-23
 * @description
 */
@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test() {
        // 得到操作Redis字符串类型操作合集
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("key1", "1");
        valueOperations.set("key2", "2");
        valueOperations.set("key3", "3");
        valueOperations.set("age", 18);


        System.out.println(redisTemplate.hasKey("key1"));

        Object key = valueOperations.get("key1");

        Assertions.assertTrue("1".equals((String) key));

        Object age = valueOperations.get("age");

        Assertions.assertTrue(18 == (Integer) age);
    }
}
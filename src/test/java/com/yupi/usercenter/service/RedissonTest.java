package com.yupi.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.usercenter.model.domain.User;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author author
 * @date 2022-08-25
 * @description
 */
@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void test() {
        // list 数据存储在 redis 的内存中
        RList<Object> rList = redissonClient.getList("test-list");
        rList.add("yupi");
        System.out.println(rList.get(0));
        // rList.remove(0);

        RMap<Object, Object> rMap = redissonClient.getMap("test-map");
        rMap.put("a", "1");
        rMap.put("b", "2");
        rMap.put("c", "1");

        System.out.println(rMap.get("a"));
        System.out.println(rMap.get("f"));


        // map

    }

    @Test
    void testWatchDog() {
        // 获取锁对象
        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");
        try {
            // 只有一个线程能够获取到锁，设置：等待时间为0s（拿不到锁就结束不进行等待），锁过期时间设置 30s
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock:" + Thread.currentThread().getId());
                Thread.sleep(1000000);

            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            // 只能释放自己到锁 - 释放锁操作一定得放在finally中，避免异常导致释放锁操作无法继续执行
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock:" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

}
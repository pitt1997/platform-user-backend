package com.yupi.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author author
 * @date 2022-08-24
 * @description 缓存预热任务
 */
@Slf4j
@Component
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 重点用户
     */
    private List<Long> mainUserList = Arrays.asList(1L);

    /**
     * 每天执行缓存预加载，预热推荐用户
     */
    @Scheduled(cron = "0 59 23 * * *")
    public void doCacheRecommendUser() {
        // 获取锁对象
        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");
        try {
            // 只有一个线程能够获取到锁，设置：等待时间为0s（拿不到锁就结束不进行等待），锁过期时间设置 30s
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock:" + Thread.currentThread().getId());
                for (Long userId : mainUserList) {
                    // 缓存中不存在则查询数据库
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("yupao:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    // 写缓存
                    try {
                        // 设置过期时间 10s过期
                        valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }

                    System.out.println("done.");
                }
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        } finally {
            // 只能释放自己到锁 - 释放锁操作一定得放在finally中，避免异常导致释放锁操作无法继续执行
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock:" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
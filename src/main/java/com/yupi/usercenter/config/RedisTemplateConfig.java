package com.yupi.usercenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author author
 * @date 2022-08-24
 * @description
 */
@Configuration
public class RedisTemplateConfig {

    /**
     * 需要指定一个连接工厂
     * @param connectionFactory 连接工厂
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        // 设置字符串编码格式
        redisTemplate.setKeySerializer(RedisSerializer.string());
        return redisTemplate;
    }
}
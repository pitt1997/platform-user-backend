package com.yupi.usercenter.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author author
 * @date 2022-08-25
 * @description Redisson 配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis") // 配置文件中获取spring.redis等配置项值
@Data
public class RedissonConfig {

    private String host;

    private String port;

    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建配置
        Config config = new Config();
        // 使用集群
        // config.useClusterServers()
        String redisAddress = String.format("redis://%s:%s", host, port);
        // 使用单机
        config.useSingleServer().setAddress(redisAddress).setDatabase(3);
        // config = Config.fromYAML(new File("config-file.yaml"));

        // 2. 创建 Redisson 实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
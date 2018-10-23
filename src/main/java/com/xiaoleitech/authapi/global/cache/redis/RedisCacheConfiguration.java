package com.xiaoleitech.authapi.global.cache.redis;

import com.xiaoleitech.authapi.global.systemparams.SystemGlobalParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 类名：RedisCacheConfiguration<br>
 * 描述：<br>
 * 创建人：<br>
 * 创建时间：2016/9/6 17:33<br>
 *
 * @version v1.0
 */

@Configuration
@EnableCaching
public class RedisCacheConfiguration extends CachingConfigurerSupport {
    private Logger logger = LoggerFactory.getLogger(RedisCacheConfiguration.class);
    private final SystemGlobalParams systemGlobalParams;

//    @Value("${spring.redis.host}")
//    private String host;
//
//    @Value("${spring.redis.port}")
//    private int port;

    @Value("${spring.redis.timeout}")
    private String timeout;

    //    @Value("${spring.redis.pool.max-idle}")
//    private int maxIdle;
    @Value("${spring.redis.jedis.pool.max-wait}")
    private String maxWaitMillis;

//    @Value("${spring.redis.password}")
//    private String password;

    @Autowired
    public RedisCacheConfiguration(SystemGlobalParams systemGlobalParams) {
        this.systemGlobalParams = systemGlobalParams;
    }

    @Bean
    public JedisPool redisPoolFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxIdle(maxIdle);
//        long maxWaitMS = UtilsHelper.extractInt(maxWaitMillis);
//        jedisPoolConfig.setMaxWaitMillis(maxWaitMS);
//        int timeoutSeconds = UtilsHelper.extractInt(timeout) / 1000;

        int timeoutSeconds = 0;
        String redisHost = systemGlobalParams.getRedisHost();
        int redisPort = systemGlobalParams.getRedisPort();
        String redisPassword = systemGlobalParams.getRedisPassword();

        logger.info("JedisPool注入成功！！");
        logger.info("redis地址：" + redisHost + ":" + redisPort);
        logger.info("redis password：" + redisPassword);

        JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisHost, redisPort, timeoutSeconds, redisPassword);

        return jedisPool;
    }

}

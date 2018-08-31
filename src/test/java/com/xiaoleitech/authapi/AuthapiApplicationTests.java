package com.xiaoleitech.authapi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthapiApplicationTests {

    private Jedis jedis;

    @Before
    public void setJedis() {
        jedis = new Jedis("115.28.34.226", 6379);
        jedis.auth("Cloud@629");
        System.out.println("Redis连接成功 ... ...");
    }

    @Autowired
    private JedisPool jedisPool;

    @Test
    public void testRedis() {
//        setJedis();
        jedis = jedisPool.getResource();
        // 添加数据
        jedis.set("name", "redis-test-instance");
        System.out.println("初始值：" + jedis.get("name"));

        // 尾部增加字符串
        jedis.append("name", " ---- is appended");
        System.out.println("Appended: " + jedis.get("name"));

        // 删除键值
        jedis.del("name");
        System.out.println("删除后：" + jedis.get("name"));
    }

    @Test
    public void contextLoads() {
        System.out.println("contextLoads......");
    }

}

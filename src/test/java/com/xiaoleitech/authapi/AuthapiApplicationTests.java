package com.xiaoleitech.authapi;

import com.xiaoleitech.authapi.helper.authenticate.AuthenticationHelper;
import com.xiaoleitech.authapi.helper.cache.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
@MapperScan(basePackages = {"com.xiaoleitech.authapi.mapper", "com.xiaoleitech.authapi.Test.mapper", "com.xiaoleitech.authapi.global.dictionary"})
public class AuthapiApplicationTests {

    @Autowired
    private RedisService redisService;

//    @Test
    public void testStringBytes(){
        String hexData = "CFA783EA";
//        Arrays.
        byte[] bytes = hexData.getBytes();
        System.out.println(bytes);
    }

//    @Test
    public void testRedisStep1() {
//        RedisService redisService = new RedisService();
//        redisService.select(9);
//        redisService.flush();

//        System.out.println("radis 空间 9 已清空 ... ...");

        System.out.println("设置键值 ... ...");
        System.out.println(redisService.setValue("wang", "60"));
        System.out.println(redisService.setValue("zhang", "30"));
        System.out.println(redisService.setValue("li", "10"));

        System.out.println("取出键值 ... ...");
        System.out.println("wang = " + redisService.getValue("wang"));
        System.out.println("zhang = " + redisService.getValue("zhang"));
        System.out.println("li = " + redisService.getValue("li"));

        System.out.println("设置键值 again... ...");
        System.out.println(redisService.setValue("wang", "55"));
        System.out.println(redisService.setValue("zhang", "66"));
        System.out.println(redisService.setValue("li", "77"));

        System.out.println("取出键值 again ... ...");
        System.out.println("wang = " + redisService.getValue("wang"));
        System.out.println("zhang = " + redisService.getValue("zhang"));
        System.out.println("li = " + redisService.getValue("li"));
    }

//    @Test
    public void testRedisStep2() {
//        RedisService redisService = new RedisService();
//        redisService.select(9);

        System.out.println("再次取出键值 ... ...");
        System.out.println("wang = " + redisService.getValue("wang"));
        System.out.println("zhang = " + redisService.getValue("zhang"));
        System.out.println("li = " + redisService.getValue("li"));
    }

    private Jedis jedis;

//    @Before
    public void setJedis() {
        jedis = new Jedis("115.28.34.226", 6379);
        jedis.auth("Cloud@629");
        System.out.println("Redis连接成功 ... ...");
    }

    @Autowired
    private JedisPool jedisPool;

//    @Test
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

package com.xiaoleitech.authapi.helper.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisService {
    private Jedis jedis;

    @Autowired
    private JedisPool jedisPool;

    public String getValue(String key) {
        jedis = jedisPool.getResource();
        return jedis.get(key);
    }

    public String setValue(String key, String value) {
        jedis = jedisPool.getResource();
        return jedis.set(key,value);
    }

    /**
     * jedis.set(key, value, nxxx, expx, time)
     *      nxxx: "NX" -- Only set the key if it does not already exist. XX -- Only set the key .
     *      expx: "EX" -- time的单位是秒；"PX" -- time的单位是毫秒；
     *
     * @param key 键
     * @param value 键对应的值
     * @param seconds 生存秒数
     * @return
     */
    public String setValueForSeconds(String key, String value, int seconds) {
        jedis = jedisPool.getResource();
        return jedis.set(key, value, "NX", "EX", seconds);
    }

}

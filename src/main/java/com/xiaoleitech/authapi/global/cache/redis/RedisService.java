package com.xiaoleitech.authapi.global.cache.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class RedisService {
    private Jedis jedis;

    private final JedisPool jedisPool;

    @Autowired
    public RedisService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.jedis = this.jedisPool.getResource();
    }

    public String getValue(String key) {
        return jedis.get(key);
    }

    public String setValue(String key, String value) {
        return jedis.set(key, value);
    }

    public void clearKey(String key) {
        jedis.del(key);
    }

    /**
     * jedis.set(key, value, nxxx, expx, time)
     * nxxx: "NX" -- Only set the key if it does not already exist. XX -- Only set the key if it already exist.
     * expx: "EX" -- time的单位是秒；"PX" -- time的单位是毫秒；
     *
     * @param key     键
     * @param value   键对应的值
     * @param seconds 生存秒数
     * @return set 的结果： "OK" -- 成功
     */
    public String setValueForSeconds(String key, String value, int seconds) {
        if (!jedis.exists(key))
            return jedis.set(key, value, "NX", "EX", seconds);
        else
            return jedis.set(key, value, "XX", "EX", seconds);
    }

//    public String select(int index) {
//        jedis = jedisPool.getResource();
//        return jedis.select(index);
//    }
//
//    public String flush() {
//        jedis = jedisPool.getResource();
//        return jedis.flushDB();
//    }

//    public String setHashValueForSeconds(String key, String field, String value, int seconds) {
//        jedis = jedisPool.getResource();
//        jedis.hset(key, field, value);
//        jedis.expire(key, seconds);
//    }

}

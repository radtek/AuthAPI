package com.xiaoleitech.authapi.auxiliary.authentication;

import com.xiaoleitech.authapi.global.cache.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ChallengeHelper {
    private final RedisService redisService;
    private int challengeValidSeconds;
    private int smsCodeValidSeconds;

    @Autowired
    public ChallengeHelper(RedisService redisService) {
        this.challengeValidSeconds = getChallengeValidSeconds();
        this.smsCodeValidSeconds = getSmsCodeValidSeconds();
        this.redisService = redisService;
    }

    /** 获取纯数字（0--9）的挑战码，位数由 digitsCount 指定
     *
     * @param digitsCount  挑战码位数，取值范围1至16，大于16的按16处理，小于1的按1来处理
     * @return
     *      纯数字（0--9）的挑战码
     */
    private String generateDigitsChallenge(int digitsCount) {
        if (digitsCount < 1)  digitsCount = 1;
        else if (digitsCount > 16) digitsCount = 16;

        Random rand = new Random();
        String randString = "";
        for (int i=0;i<digitsCount;i++)
            randString = randString.concat( String.valueOf( rand.nextInt(10) ) );

        return randString;
    }

    private String getCachedSmsKey(String phoneNo) { return "sms_code_" + phoneNo; }

    /**
     * 产生一个短信验证码，并在缓存中保存一段有效时间
     * @param phoneNo 需要关联的电话号码
     * @param smsCode 指定 smsCode，不随机生成验证码
     * @return 短信验证码，如果产生失败，则返回空字符串
     */
    public String generateSmsCode(String phoneNo, String smsCode) {
        int digits = 6; // getDefaultChallengeDigitsCount
        String challenge;

        if (smsCode.isEmpty()) {
            // 指定验证码smsCode为空，产生6位随机数
            challenge = generateDigitsChallenge(digits);
        } else {
            // 不为空，则使用指定的 smsCode
            challenge = smsCode;
        }

        // 保存到缓存，并设定300秒有效期
        String key = getCachedSmsKey(phoneNo);
        String result = redisService.setValueForSeconds(key, challenge, smsCodeValidSeconds);
        if ( !result.equals("OK") )
            return "";
        return challenge;
    }

    /**
     * 获取缓存的短信验证码
     * @param phoneNo 关联的电话号码
     * @return 短信验证码，如果已经失效，则返回空字符串
     */
    public String getSmsCode(String phoneNo) {
        String key = getCachedSmsKey(phoneNo);
        String smsCode = redisService.getValue(key);
        if (smsCode == null)
            return "";
        else
            return smsCode;
    }

    /** 返回一个固定长度的数字型挑战码
     * 本版本长度默认为6位，可以在字典表里保存挑战码长度，替换硬编码
     *
     * @param userUuid 用户UUID，在cache中保存：键（用户UUID）值（挑战码）对
     * @return
     *      纯数字（0--9）的挑战码
     */
    public String generateUserChallenge(String userUuid) {
        int digits = 6; // getDefaultChallengeDigitsCount
        // 产生6位随机数
        String challenge = generateDigitsChallenge(digits);

        // 保存到缓存，并设定60秒有效期
        String result = redisService.setValueForSeconds(userUuid, challenge, challengeValidSeconds);
        if ( !result.equals("OK") )
            return "";

        return challenge;
    }

    /** 获取挑战码的有效时间（单位：秒）
     *
     * @return  挑战码有效秒数
     */
    private int getChallengeValidSeconds() {
        return 60;
    }

    /** 获取短信验证码的有效时间（单位：秒）
     *
     * @return  短信验证码有效秒数
     */
    private int getSmsCodeValidSeconds() {
        return 300;
    }

    public String getChallenge(String key) {
        return redisService.getValue(key);
    }
}

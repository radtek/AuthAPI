package com.xiaoleitech.authapi.auxiliary.authentication;

import com.xiaoleitech.authapi.global.systemparams.SystemGlobalParams;
import com.xiaoleitech.authapi.global.utils.UtilsHelper;
import com.xiaoleitech.authapi.global.cache.redis.RedisService;
import com.xiaoleitech.authapi.global.cipher.base64.Base64Coding;
import com.xiaoleitech.authapi.global.cipher.hash.HashAlgorithm;
import com.xiaoleitech.authapi.global.cipher.symmetric.SymmetricAlgorithm;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.enumeration.PasswordModeEnum;
import com.xiaoleitech.authapi.global.enumeration.ProtectMethodEnum;
import com.xiaoleitech.authapi.dao.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;

@Component
public class AuthenticationHelper {
    private final SystemGlobalParams systemGlobalParams;
    private final RedisService redisService;
    private final SymmetricAlgorithm symmetricAlgorithm;

    private final int authRetryMax = 10;    // 认证最大尝试次数

    @Autowired
    public AuthenticationHelper(SystemGlobalParams systemGlobalParams, RedisService redisService, SymmetricAlgorithm symmetricAlgorithm) {
        this.systemGlobalParams = systemGlobalParams;
        this.redisService = redisService;
        this.symmetricAlgorithm = symmetricAlgorithm;
    }

//    public enum PasswordEncryptMethodEnum {
//        ENCRYPT_METHOD_PLAIN_TEXT(1),       // Plain Password
//        ENCRYPT_METHOD_SHA256(2),           // SHA256(pwd)
//        ENCRYPT_METHOD_SALT_SHA256(3),      // SHA256(pwd+salt)
//        ;
//        private int method;
//
//        PasswordEncryptMethodEnum(int method) { this.method = method; }
//
//        public int getMethod() {
//            return method;
//        }
//
//        public void setMethod(int method) {
//            this.method = method;
//        }
//    }

    /** 密码明文使用固定的加密方式，返回密码的密文
     *
     * @param passwordPlainText: 密码明文
     * @return 密码加密后的16进制字串
     */
    public String getEncryptedPassword(String passwordPlainText) {
        return getEncryptedPassword(passwordPlainText, "");
    }

    /** 模块内部统一的密码加密算法
     *
     * @return 密码加密算法的枚举值
     *  目前统一且固定采用SHA256
     */
    private int getPasswordEncryptMethod() {
        return systemGlobalParams.getPasswordMode();
//        return PasswordEncryptMethodEnum.ENCRYPT_METHOD_SHA256;
    }

    /** 按指定的加密方式和盐，返回密码的密文
     * e.g., 密文 = SHA256( SHA256(明文) + 盐 )
     *
     * @param passwordPlainText: 密码明文
     * @param salt: 盐
     * @return 密码密文，16进制的字符串
     */
    public String getEncryptedPassword(
            String passwordPlainText,
            String salt) {
        // 未给定密码明文的，返回空字符串
        if ( (passwordPlainText == null) || (passwordPlainText.isEmpty()) )
            return "";

        int passwordMode = getPasswordEncryptMethod();
        if (passwordMode == PasswordModeEnum.PASSWORD_MODE_PLAIN.getId()) {
            // 明文模式
            return passwordPlainText;
        } else if (passwordMode == PasswordModeEnum.PASSWORD_MODE_SHA256.getId()) {
            // 对密码做HASH
            String hexPassword = UtilsHelper.bytesToHexString(passwordPlainText.getBytes());
            return HashAlgorithm.getSHA256(hexPassword);
        } else if (passwordMode == PasswordModeEnum.PASSWORD_MODE_SALT_SHA256.getId()) {
            // 先做一次HASH，在HASH结果尾部添加盐，再做一次HASH
            String hexPassword = UtilsHelper.bytesToHexString(passwordPlainText.getBytes());
            String hash = HashAlgorithm.getSHA256(hexPassword);
            hash += salt;
            String hexHash = UtilsHelper.bytesToHexString(hash.getBytes());
            return HashAlgorithm.getSHA256(hexHash);
        } else {
            // 不能识别的模式，返回明文
            return passwordPlainText;
        }
    }

    /** 将密码的明文和密文做一致性匹配
     *
     * @param passwordPlainText: 密码明文
     * @param passwordCipherText: 密码密文
     * @param salt: 加密采用的盐
     * @return
     *      true: 密码正确；false: 密码错误
     */
    public boolean isValidPassword(
            String passwordPlainText,
            String passwordCipherText,
            String salt) {
        String cipher = getEncryptedPassword(passwordPlainText, salt);
        return cipher.equals(passwordCipherText);
//        return getEncryptedPassword(passwordPlainText, salt).equals(passwordCipherText);
    }

    /** 检查指定令牌是否已校验或有效
     *
     * @Deprecated
     * @param verifyToken 需验证的token
     * @return
     *      true: 令牌验证已通过或有效；false: 令牌无效或验证失败
     */
    public boolean isTokenVerified(String verifyToken) {
        // TODO: 检查令牌 verifyToken
//        String value =
        return true;
    }

    /** 检查指定令牌是否已校验或有效
     *
     * @param verifyToken 需验证的token
     * @return
     *      true: 令牌验证已通过或有效；false: 令牌无效或验证失败
     */
    public boolean isValidVerifyToken(String verifyToken) {
        // TODO: 检查令牌 verifyToken
//        String value =
        return true;
    }

    /**
     * 用user的 authkey对appuuid进行分散，得到分散密钥（base64编码）
     * @param appUuid APP（或RP）的UUID
     * @param userAuthKey 用户的authkey
     * @return 分散密钥的base64编码
     */
    public String getSdkAuthKey(String appUuid, String userAuthKey) {
        String sdkAuthKey = symmetricAlgorithm.doAes(appUuid, userAuthKey, 256, Cipher.ENCRYPT_MODE);

        return Base64Coding.encodeFromHexString(sdkAuthKey);
    }

    /**
     * 随机产生系统定义的用户authkey
     * 只产生可打印字符，保存在数据库中以源字符串形式，不转成HexString
     *
     * @return 返回16进制字符串格式的 authkey
     */
    public String generateAuthKey() {
        final int bytesCount = 32;
        return UtilsHelper.generatePrintableRandom(bytesCount);
    }


    /**
     * 获取验证许可，如果验证尝试次数已达到最大值，则检查锁定时间，锁定时间已过，需对user的尝试次数清零
     * @param user 用户记录
     * @param protectMethod 保护方式，密码和非密码两种
     * @return 错误代码，允许验证时返回 ERROR_OK，否则返回对应的错误码
     */
    public ErrorCodeEnum getAuthticateRight(Users user, int protectMethod) {
        // 如果是密码校验方式，检查用户密码的状态（是否锁定，剩余次数）
        if (ProtectMethodEnum.VERIFY_PASSWORD.isMatched(protectMethod)) {
            // 密码失败次数是否达到锁定的阈值
            if (user.getPassword_attempt_fail_count() >= authRetryMax) {
                // 检查密码锁定时间
                if (user.getPassword_lock_to().before(UtilsHelper.getCurrentSystemTimestamp())){
                    // 清除密码尝试次数
                    user.setPassword_attempt_fail_count(0);
                } else {
                    return ErrorCodeEnum.ERROR_USER_PASSWORD_LOCKED;
                }
            }
        } else {
            // 非密码，检查第二因子的验证状态
            if (user.getSecond_factor_attempt_fail_count() >= authRetryMax) {
                // 检查第二因子锁定时间
                if (user.getSecond_factor_lock_to().before(UtilsHelper.getCurrentSystemTimestamp())) {
                    // 清除第二因子尝试次数
                    user.setSecond_factor_attempt_fail_count(0);
                } else {
                    return ErrorCodeEnum.ERROR_USER_2FA_LOCKED;
                }
            }
        }

        return ErrorCodeEnum.ERROR_OK;

    }

    /**
     * 获取最大尝试次数
     * @return 最大尝试次数
     */
    public int getAuthRetryMax() {
        return authRetryMax;
    }

    /**
     * 获取锁定时间段
     * @return 毫秒单位的整数
     */
    public long getLockPeriod() {
        // 10分钟
        return (10 * 60 * 1000);
    }

    /**
     * 获取锁定时间
     * @return 锁定时间
     */
    public java.sql.Timestamp getLockTime() {
        java.sql.Timestamp currentTime = UtilsHelper.getCurrentSystemTimestamp();
        currentTime.setTime(currentTime.getTime() + getLockPeriod());
        return currentTime;
    }

    /**
     * 获取token的有效时间
     * @param token 系统产生的验证令牌
     * @return token有效时间
     */
    public java.sql.Timestamp getTokenExpireTime(String token) {
        // TODO: 需替换成从缓存中取token的有效时间
        java.sql.Timestamp currentTime = UtilsHelper.getCurrentSystemTimestamp();
        currentTime.setTime(currentTime.getTime() + getVerifyTokenValidPeriod());
        return currentTime;
    }

    /**
     * 维护用户验证的有效时间跨度
     * @return 暂定10分钟，可以由应用方指定，在数据库中维护
     */
    private int getUserAuthValidPeriod() {
        return 10 * 60 * 1000;
    }

    /**
     * 获取用户过期时间
     * @param user users 记录
     * @return 用户验证的过期时间
     */
    public java.sql.Timestamp getUserAuthExpireTime(Users user) {
        java.sql.Timestamp authTime = user.getAuth_at();
        authTime.setTime(authTime.getTime() + getUserAuthValidPeriod());
        return authTime;
    }

    /**
     * 维护验证令牌的有效期跨度
     * @return 暂定60秒，可以由应用方指定，在数据库中维护
     */
    private int getVerifyTokenValidPeriod() {
        return 60 * 1000;
    }

    /**
     * 产生一个验证令牌
     * @return 验证令牌字符串格式
     */
    public String generateTimingToken() {
        // TODO: 需补充：产生令牌后，向缓存中保存一段有效时间
        String token = UtilsHelper.generateUuid();
        redisService.setValueForSeconds(token, "valid", getVerifyTokenValidPeriod());
        return token;
    }

    public void clearToken(String token) {
        redisService.clearKey(token);
    }

}

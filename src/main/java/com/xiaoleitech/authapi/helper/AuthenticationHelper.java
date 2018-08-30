package com.xiaoleitech.authapi.helper;

import com.xiaoleitech.authapi.helper.cipher.HashAlgorithm;

public class AuthenticationHelper {

    public enum PasswordEncryptMethodEnum {
        ENCRYPT_METHOD_PLAIN_TEXT(0),
        ENCRYPT_METHOD_SHA256(1),
        ENCRYPT_METHOD_SALT_SHA256(2),
        ;
        private int method;

        PasswordEncryptMethodEnum(int method) { this.method = method; }

        public int getMethod() {
            return method;
        }

        public void setMethod(int method) {
            this.method = method;
        }
    }

    /** 密码明文使用固定的加密方式，返回密码的密文
     *
     * @param passwordPlainText: 密码明文
     * @return 密码加密后的16进制字串
     */
    static public String getEncryptedPassword(String passwordPlainText) {
        return getEncryptedPassword(passwordPlainText, "");
    }

    /** 模块内部统一的密码加密算法
     *
     * @return 密码加密算法的枚举值
     *  目前统一且固定采用SHA256
     */
    static public PasswordEncryptMethodEnum getPasswordEncryptMethod() {
        return PasswordEncryptMethodEnum.ENCRYPT_METHOD_SHA256;
    }

    /** 按指定的加密方式和盐，返回密码的密文
     * e.g., 密文 = SHA256( SHA256(明文) + 盐 )
     *
     * @param passwordPlainText: 密码明文
     * @param salt: 盐
     * @return 密码密文，16进制的字符串
     */
    static public String getEncryptedPassword(
            String passwordPlainText,
            String salt) {
        PasswordEncryptMethodEnum encryptMethod = AuthenticationHelper.getPasswordEncryptMethod();

        // 未给定密码明文的，返回空字符串
        if ( (passwordPlainText == null) || (passwordPlainText.isEmpty()) )
            return "";

        if (encryptMethod == PasswordEncryptMethodEnum.ENCRYPT_METHOD_SHA256)
            return HashAlgorithm.getSHA256(passwordPlainText);
        else if (encryptMethod == PasswordEncryptMethodEnum.ENCRYPT_METHOD_SALT_SHA256) {
            // 先做一次HASH，在HASH结果尾部添加盐，再做一次HASH
            String hash = HashAlgorithm.getSHA256(passwordPlainText);
            hash += salt;
            return HashAlgorithm.getSHA256(hash);
        } else
            return "";
    }

    /** 将密码的明文和密文做一致性匹配
     *
     * @param passwordPlainText: 密码明文
     * @param passwordCipherText: 密码密文
     * @param salt: 加密采用的盐
     * @return
     *      true: 密码正确；false: 密码错误
     */
    static public boolean isValidPassword(
            String passwordPlainText,
            String passwordCipherText,
            String salt) {
        String cipher = getEncryptedPassword(passwordPlainText, salt);
        return passwordCipherText.equals(cipher);
    }
}

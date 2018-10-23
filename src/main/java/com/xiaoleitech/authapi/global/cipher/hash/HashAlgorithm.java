package com.xiaoleitech.authapi.global.cipher.hash;


import com.xiaoleitech.authapi.global.utils.UtilsHelper;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashAlgorithm {

    /**
     * 对输入的字符串，按照指定算法进行散列运算，并返回字符串形式的16进制结果
     *
     * @param message:   输入明文(纯字符串)
     * @param algorithm: HASH算法
     * @return 返回字符串形式的16进制结果
     */
    static public String getHash(String message, HashAlgorithmEnum algorithm) {
        if (algorithm == HashAlgorithmEnum.HASH_ALG_SHA256)
            return getSHA256(message);
        else if (algorithm == HashAlgorithmEnum.HASH_ALG_MD5)
            return getMD5(message);

        return "";
    }

    static public String getMD5(String message) {
        MessageDigest messageDigest;
        String hashResult = "";

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            byte[] hashBytes = messageDigest.digest(message.getBytes("UTF-8"));
            hashResult = Hex.encodeHexString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return hashResult;
    }

    /**
     * 对输入字符串，返回字符串形式的散列结果 (SHA-256)
     *
     * @param message: 输入的明文（纯字符串）
     * @return 返回字符串形式的散列结果 (SHA-256)
     */
    static public String getSHA256(String message) {
        MessageDigest messageDigest;
        String hashResult = "";

        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(UtilsHelper.hexStringToBytes(message));
//            byte[] hashBytes = messageDigest.digest(Hex.decodeHex(message));
            hashResult = Hex.encodeHexString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hashResult;
    }

}

package com.xiaoleitech.authapi.global.cipher.symmetric;


import com.xiaoleitech.authapi.global.utils.UtilsHelper;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

@Component
public class SymmetricAlgorithm {
    /**
     * 进行AES算法（加密和解密）,输入输出数据格式为16进制字符串
     *
     * @param hexText     16进制格式的输入字符串（明文或密文）
     * @param key         密钥字符串(源串，非16进制表示的HexString)
     * @param bits        AES算法位数
     * @param encryptMode 密码算法模式：Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
     * @return 16进制字符串格式的计算结果（密文或明文）
     */
    public String doAes(String hexText, String key, int bits, int encryptMode) {
        byte[] result = doAes(UtilsHelper.hexStringToBytes(hexText), key.getBytes(), bits, encryptMode);
        if (result == null)
            return "";
        else
            return UtilsHelper.bytesToHexString(result);
//        if ( (encryptMode != Cipher.ENCRYPT_MODE) && (encryptMode != Cipher.DECRYPT_MODE) )
//            return "";
//
//        try {
//            Security.addProvider(new BouncyCastleProvider());
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
//            SecretKeySpec secretKey = new SecretKeySpec(UtilsHelper.hexStringToBytes(key), "AES");
//            cipher.init(encryptMode, secretKey);
//            byte[] encryptResult = cipher.doFinal(UtilsHelper.hexStringToBytes(hexText));
//            return UtilsHelper.bytesToHexString(encryptResult);
//        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        }
//        return "";
    }

    /**
     * 进行AES算法（加密和解密）,输入输出数据格式为字节流
     *
     * @param source      源数据（字节流）
     * @param key         密钥
     * @param bits        算法位数
     * @param encryptMode 密码算法模式：Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
     * @return 计算结果（字节流）
     */
    public byte[] doAes(byte[] source, byte[] key, int bits, int encryptMode) {
        if ((encryptMode != Cipher.ENCRYPT_MODE) && (encryptMode != Cipher.DECRYPT_MODE))
            return null;

        try {
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "BC");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            cipher.init(encryptMode, secretKey);
            byte[] encryptResult = cipher.doFinal(source);
            return encryptResult;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

}

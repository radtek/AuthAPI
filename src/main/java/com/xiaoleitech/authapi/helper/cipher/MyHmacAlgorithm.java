package com.xiaoleitech.authapi.helper.cipher;

import com.xiaoleitech.authapi.helper.UtilsHelper;
import org.bouncycastle.util.encoders.Hex;

public class MyHmacAlgorithm {
    static public String calculate(String challenge,
                                   String password,
                                   String passwordSalt,
                                   String authKey) {

//        String hashedPassword = HashAlgorithm.getHash(Hex.toHexString(password.getBytes()), HashAlgorithmEnum.HASH_ALG_SHA256);
        StringBuilder stringBuilder = new StringBuilder();
        // 挑战码用传入的字符串，不转格式
        stringBuilder.append(challenge);
        stringBuilder.append(password);
//        stringBuilder.append(passwordSalt);
        stringBuilder.append(authKey);

        // salt中的连接符 - 保留不去掉
        String msg = stringBuilder.toString();

        String hashText = HashAlgorithm.getHash(Hex.toHexString(msg.getBytes()), HashAlgorithmEnum.HASH_ALG_SHA256);

        return hashText;
    }
}

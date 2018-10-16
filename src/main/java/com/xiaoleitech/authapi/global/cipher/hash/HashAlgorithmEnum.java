package com.xiaoleitech.authapi.global.cipher.hash;

public enum HashAlgorithmEnum {
    HASH_ALG_MD5(1),
    HASH_ALG_SHA256(2),
    ;
    private int algorithm;

    HashAlgorithmEnum(int algorithm) {
        this.algorithm = algorithm;
    }
}

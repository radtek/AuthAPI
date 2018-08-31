package com.xiaoleitech.authapi.helper.cipher;

public enum HashAlgorithmEnum {
    HASH_ALG_SHA256(2),
    ;
    private int algorithm;

    HashAlgorithmEnum(int algorithm) {
        this.algorithm = algorithm;
    }
}
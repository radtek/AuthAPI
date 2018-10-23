package com.xiaoleitech.authapi.global.enumeration;

public enum OtpAlgEnum {
    //   OTP算法: integer
    //		0:不使用OTP；1:Google OTP；2: SHA1；3: SM3；默认0
    NO_OTP(0),
    GOOGLE(1),
    SHA1(2),
    SM3(3),;

    private int algorithm;

    OtpAlgEnum(int algorithm) {
        this.algorithm = algorithm;
    }

    public int getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(int algorithm) {
        this.algorithm = algorithm;
    }
}

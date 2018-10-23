package com.xiaoleitech.authapi.global.enumeration;

public enum UserCertEnum {
    //  user_cert: integer，是否使用证书。
    //    1: 使用；0: 不使用；2: Optional 可选
    //    为1时，认证过程需要使用数字证书进行身份鉴别。
    NOT_USE_CERT(0),
    USE_CERT(1),
    OPTIONAL(2),;

    private int userCert;

    UserCertEnum(int useCert) {
        this.userCert = useCert;
    }

    public int getUseCert() {
        return userCert;
    }

    public void setUseCert(int useCert) {
        this.userCert = useCert;
    }
}

package com.xiaoleitech.authapi.global.enumeration;

public enum AuthorizationPolicyEnum {
    //   authorization_policy:integer
    // TODO: 取值2和3的差别
    //		1:如AHAPP已登录则授权；2:如AHAPP登录使用生物识别则授权，否则需要生物识别；3:总是需要生物识别
    AUTH_NEED_LOGIN(1),
    AUTH_NEED_BIOMETRIC(2),
    AUTH_ALWAYS_BIOMETRIC(3),;

    private int authPolicy;

    AuthorizationPolicyEnum(int authPolicy) {
        this.authPolicy = authPolicy;
    }

    public int getAuthPolicy() {
        return authPolicy;
    }

    public void setAuthPolicy(int authPolicy) {
        this.authPolicy = authPolicy;
    }
}

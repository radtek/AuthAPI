package com.xiaoleitech.authapi.global.enumeration;

public enum CertStateEnum {
    NO_CERT(0),
    CERT_PENDING(1),
    CERT_PENDING2(2),
    AVAILABLE(3),;
    private int state;

    CertStateEnum(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}

package com.xiaoleitech.authapi.global.enumeration;

public enum UserAuthStateEnum {
    AUTH_STATE_NOT_AUTHED(0),
    AUTH_STATE_AUTHED(1),;
    private int authState;

    UserAuthStateEnum(int authState) {
        this.authState = authState;
    }

    public int getAuthState() {
        return authState;
    }

    public void setAuthState(int authState) {
        this.authState = authState;
    }
}

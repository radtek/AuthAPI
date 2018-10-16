package com.xiaoleitech.authapi.global.enumeration;

public enum AppStateEnum {
    //    app_state：integer
    //		0:未注册；1:已注册，未激活；2:已激活。
    APP_NOT_REGISTER(0),
    APP_REGISTER_NOT_ACTIVE(1),
    APP_ACTIVE(2),;

    private int state;

    AppStateEnum(int app_state) {
        this.state = app_state;
    }

    public int getApp_state() {
        return state;
    }

    public void setApp_state(int app_state) {
        this.state = app_state;
    }
}

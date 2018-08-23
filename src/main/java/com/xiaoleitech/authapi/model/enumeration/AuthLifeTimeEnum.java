package com.xiaoleitech.authapi.model.enumeration;

public enum AuthLifeTimeEnum {
    //   auth_life_time:integer
    //		1:AHAPP退出登录即终止授权；2:1小时；3:1天；4:1月；5: 一直有效直到取消授权
    AUTH_TIME_ONCE(1),
    AUTH_TIME_ONE_HOUR(2),
    AUTH_TIME_ONE_DAY(3),
    AUTH_TIME_ONE_MONTH(4),
    AUTH_TIME_ALWAYS(5),;

    private int authLifeTime;

    AuthLifeTimeEnum(int authLifeTime) {
        this.authLifeTime = authLifeTime;
    }

    public int getAuthLifeTime() {
        return authLifeTime;
    }

    public void setAuthLifeTime(int authLifeTime) {
        this.authLifeTime = authLifeTime;
    }
}

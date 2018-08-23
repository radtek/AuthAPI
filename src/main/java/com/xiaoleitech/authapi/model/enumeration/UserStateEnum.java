package com.xiaoleitech.authapi.model.enumeration;

public enum UserStateEnum {
    //  user_state：integer，用户状态
    //		0:未注册；1:已注册，未绑定设备；2:已注册，已绑定设备、手机号，已激活；3: 2+已绑定身份证信息；9:用户已注销。
    USER_NOT_REGISTERED(0),
    USER_REG_NO_BINDING(1),
    USER_REG_BINDING_L1(2),
    USER_REG_BINDING_L2(3),
    USER_UNREGISTERED(9),;

    private int state;

    UserStateEnum(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}

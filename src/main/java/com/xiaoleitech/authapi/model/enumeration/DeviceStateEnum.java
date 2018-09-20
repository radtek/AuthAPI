package com.xiaoleitech.authapi.model.enumeration;

public enum DeviceStateEnum {
    //  device_state：integer
    //	0:未注册；1:已注册，未绑定用户；2:已注册，已绑定用户，已验证手机号；9:设备已注销。
    DEV_LOGICAL_DELETE(-99),
    DEV_NOT_REGISTER(0),
    DEV_REGISTER_NO_BINDING(1),
    DEV_REGISTER_AND_BINDING(2),
    DEV_UNREGISTER(9),;

    private int state;

    DeviceStateEnum(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}

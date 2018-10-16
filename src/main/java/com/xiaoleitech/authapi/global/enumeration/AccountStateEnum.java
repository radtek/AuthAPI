package com.xiaoleitech.authapi.global.enumeration;

public enum AccountStateEnum {
    //    account_state：integer
    //		0:未绑定指定应用；1:已绑定指定应用且已激活；2:已绑定指定应用但未激活。
    ACCOUNT_LOGICAL_DELETE(-99),
    ACCOUNT_STATE_UNBINDING(0),
    ACCOUNT_STATE_ACTIVE(1),
    ACCOUNT_STATE_INACTIVE(2),
    ;
    private int state;

    AccountStateEnum(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}

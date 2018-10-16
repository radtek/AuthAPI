package com.xiaoleitech.authapi.global.enumeration;

public enum RealNameScopeEnum {
    //  real_name_scope: int，实名信息
    //		1:电话号码；2:姓名；3:身份证号
    //		可以用字符串组合"123"表示电话号码、姓名性别、身份证号。
    REALNAME_AUTH_PHONE_NUMBER(1),
    REALNAME_AUTH_NAME(2),
    REALNAME_AUTH_ID_NUMBER(3),;

    private int realNameScope;

    RealNameScopeEnum(int realNameScope) {
        this.realNameScope = realNameScope;
    }

    public int getRealNameScope() {
        return realNameScope;
    }

    public void setRealNameScope(int realNameScope) {
        this.realNameScope = realNameScope;
    }
}

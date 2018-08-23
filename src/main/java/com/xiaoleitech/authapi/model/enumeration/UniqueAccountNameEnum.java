package com.xiaoleitech.authapi.model.enumeration;

public enum UniqueAccountNameEnum {
    //   uniq_account_name:integer
    //    1:使用唯一昵称作为APP的账户名；2:使用昵称作为APP的账户名（可不唯一）；3:不使用昵称
    USING_UNIQUE_NICK_NAME(1),
    USING_NICK_NAME(2),
    NOT_USING_NICK_NAME(3),;

    private int uniqueAccountName;

    UniqueAccountNameEnum(int uniqueAccountName) {
        this.uniqueAccountName = uniqueAccountName;
    }

    public int getUniqueAccountName() {
        return uniqueAccountName;
    }

    public void setUniqueAccountName(int uniqueAccountName) {
        this.uniqueAccountName = uniqueAccountName;
    }
}

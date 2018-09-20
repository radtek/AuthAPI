package com.xiaoleitech.authapi.model.enumeration;

public enum SystemDictKeyEnum {
    DICT_KEY_PASSWORD_MODE(1, "GLOBAL_PARAMS_password_mode"),
    DICT_KEY_TOKEN_CHECK_MODE(2, "GLOBAL_PARAMS_token_check_mode"),
    ;

    private int index;
    private String key;

    SystemDictKeyEnum(int index, String key) {
        this.index = index;
        this.key = key;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

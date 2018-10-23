package com.xiaoleitech.authapi.global.enumeration;

public enum SystemDictKeyEnum {
    DICT_KEY_PASSWORD_MODE(1, "GLOBAL_PARAMS_password_mode"),
    DICT_KEY_TOKEN_CHECK_MODE(2, "GLOBAL_PARAMS_token_check_mode"),
    DICT_KEY_REDIS_HOST(3, "GLOBAL_PARAMS_redis_host"),
    DICT_KEY_REDIS_PORT(4, "GLOBAL_PARAMS_redis_port"),
    DICT_KEY_REDIS_PASSWORD(5, "GLOBAL_PARAMS_redis_password"),
    DICT_KEY_MQTT_URL(6, "GLOBAL_PARAMS_mqtt_url"),
    DICT_KEY_MQTT_USERNAME(7, "GLOBAL_PARAMS_mqtt_user_name"),
    DICT_KEY_MQTT_PASSWORD(8, "GLOBAL_PARAMS_mqtt_password"),
    DICT_KEY_ACTIVEMQ_URL(9, "GLOBAL_PARAMS_activemq_url"),
    DICT_KEY_ACTIVEMQ_USER_NAME(10, "GLOBAL_PARAMS_activemq_user_name"),
    DICT_KEY_ACTIVEMQ_PASSWORD(11, "GLOBAL_PARAMS_activemq_password"),;

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

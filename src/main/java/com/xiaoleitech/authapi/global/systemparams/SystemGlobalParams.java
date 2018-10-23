package com.xiaoleitech.authapi.global.systemparams;

import com.xiaoleitech.authapi.dao.helper.SystemDictHelper;
import com.xiaoleitech.authapi.global.enumeration.PasswordModeEnum;
import com.xiaoleitech.authapi.global.enumeration.SystemDictKeyEnum;
import com.xiaoleitech.authapi.global.enumeration.TokenCheckModeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SystemGlobalParams {
    private static boolean isInitialized = false;
    private final SystemDictHelper systemDictHelper;

    private static int passwordMode;               // 密码模式
    private static int tokenCheckMode;             // 令牌验证模式
    private static String redisHost = "";       // redis服务器主机地址
    private static int redisPort = 80;          // redis服务器端口
    private static String redisPassword = "";   // redis服务器访问密码
    private static String mqttServerUrl = "";   // MQTT服务器url
    private static String mqttUserName = "";    // MQTT服务器有访问权限的用户名
    private static String mqttPassword = "";    // MQTT服务器，上述用户名的密码
    private static String activemqUrl = "";     // AcitveMQ服务器url
    private static String activemqUserName = "";     // AcitveMQ 用户名
    private static String activemqPassword = "";     // AcitveMQ 密码


    @Autowired
    public SystemGlobalParams(SystemDictHelper systemDictHelper) {
        this.systemDictHelper = systemDictHelper;
        if (!isInitialized) {
            initGlobalDictionary();
            isInitialized = true;
        }
    }

    public int getPasswordMode() {
        return passwordMode;
    }

    public void setPasswordMode(int passwordMode) {
        SystemGlobalParams.passwordMode = passwordMode;
    }

    public int getTokenCheckMode() {
        return tokenCheckMode;
    }

    public void setTokenCheckMode(int tokenCheckMode) {
        SystemGlobalParams.tokenCheckMode = tokenCheckMode;
    }

    public static String getRedisHost() {
        return redisHost;
    }

    public static void setRedisHost(String redisHost) {
        SystemGlobalParams.redisHost = redisHost;
    }

    public static int getRedisPort() {
        return redisPort;
    }

    public static void setRedisPort(int redisPort) {
        SystemGlobalParams.redisPort = redisPort;
    }

    public static String getRedisPassword() {
        return redisPassword;
    }

    public static void setRedisPassword(String redisPassword) {
        SystemGlobalParams.redisPassword = redisPassword;
    }

    public static String getMqttServerUrl() {
        return mqttServerUrl;
    }

    public static void setMqttServerUrl(String mqttServerUrl) {
        SystemGlobalParams.mqttServerUrl = mqttServerUrl;
    }

    public static String getMqttUserName() {
        return mqttUserName;
    }

    public static void setMqttUserName(String mqttUserName) {
        SystemGlobalParams.mqttUserName = mqttUserName;
    }

    public static String getMqttPassword() {
        return mqttPassword;
    }

    public static void setMqttPassword(String mqttPassword) {
        SystemGlobalParams.mqttPassword = mqttPassword;
    }

    public static String getActivemqUrl() {
        return activemqUrl;
    }

    public static void setActivemqUrl(String activemqUrl) {
        SystemGlobalParams.activemqUrl = activemqUrl;
    }

    public static String getActivemqUserName() {
        return activemqUserName;
    }

    public static void setActivemqUserName(String activemqUserName) {
        SystemGlobalParams.activemqUserName = activemqUserName;
    }

    public static String getActivemqPassword() {
        return activemqPassword;
    }

    public static void setActivemqPassword(String activemqPassword) {
        SystemGlobalParams.activemqPassword = activemqPassword;
    }

    public void initGlobalDictionary() {
        // 从系统全局字典表中读取：密码模式
        int passwordMode = systemDictHelper.getIntegerValue(
                SystemDictKeyEnum.DICT_KEY_PASSWORD_MODE.getKey(),
                PasswordModeEnum.PASSWORD_MODE_PLAIN.getId());
        setPasswordMode(passwordMode);

        // 从系统全局字典表中读取：token 校验模式
        int checkTokenMode = systemDictHelper.getIntegerValue(
                SystemDictKeyEnum.DICT_KEY_TOKEN_CHECK_MODE.getKey(),
                TokenCheckModeEnum.CHECK_TOKEN.getId());
        setTokenCheckMode(checkTokenMode);

        // 从系统全局字典表中读取：redis服务器地址、端口、密码
        String host = systemDictHelper.getStringValue(SystemDictKeyEnum.DICT_KEY_REDIS_HOST.getKey(), "115.28.34.226");
        setRedisHost(host);
        int port = systemDictHelper.getIntegerValue(SystemDictKeyEnum.DICT_KEY_REDIS_PORT.getKey(), 6379);
        setRedisPort(port);
        String password = systemDictHelper.getStringValue(SystemDictKeyEnum.DICT_KEY_REDIS_PASSWORD.getKey(), "Cloud@629");
        setRedisPassword(password);

        // 从系统全局字典表中读取：MQTT服务器地址、用户名、密码
        String url = systemDictHelper.getStringValue(SystemDictKeyEnum.DICT_KEY_MQTT_URL.getKey(), "tcp://115.28.34.226:61613");
        setMqttServerUrl(url);
        String userName = systemDictHelper.getStringValue(SystemDictKeyEnum.DICT_KEY_MQTT_USERNAME.getKey(), "admin");
        setMqttUserName(userName);
        password = systemDictHelper.getStringValue(SystemDictKeyEnum.DICT_KEY_MQTT_PASSWORD.getKey(), "password");
        setMqttPassword(password);

        // 从系统全局字典表中读取：ActiveMQ服务器地址、用户名、密码
        url = systemDictHelper.getStringValue(SystemDictKeyEnum.DICT_KEY_MQTT_URL.getKey(), "tcp://115.28.34.226:62616");
        setActivemqUrl(url);
        userName = systemDictHelper.getStringValue(SystemDictKeyEnum.DICT_KEY_ACTIVEMQ_USER_NAME.getKey(), "admin");
        setActivemqUserName(userName);
        password = systemDictHelper.getStringValue(SystemDictKeyEnum.DICT_KEY_ACTIVEMQ_PASSWORD.getKey(), "admin");
        setActivemqPassword(password);
    }

}

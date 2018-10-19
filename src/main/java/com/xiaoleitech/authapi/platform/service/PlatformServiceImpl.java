package com.xiaoleitech.authapi.platform.service;

import com.xiaoleitech.authapi.global.msgqueue.apollo.MqttSenderConfig;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.platform.bean.response.CloudInfoResponse;
import com.xiaoleitech.authapi.platform.bean.response.PlatformSettingResponse;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlatformServiceImpl implements PlatformService {
    private final SystemErrorResponse systemErrorResponse;
    private final MqttSenderConfig mqttSenderConfig;
    private final CloudInfoResponse cloudInfoResponse;

    @Autowired
    public PlatformServiceImpl(SystemErrorResponse systemErrorResponse, MqttSenderConfig mqttSenderConfig, CloudInfoResponse cloudInfoResponse) {
        this.systemErrorResponse = systemErrorResponse;
        this.mqttSenderConfig = mqttSenderConfig;
        this.cloudInfoResponse = cloudInfoResponse;
    }

    @Override
    public AuthAPIResponse getPlatformSetting() {
        PlatformSettingResponse platformSettingResponse = new PlatformSettingResponse();

        // TODO get_platform_setting: 1. Implement the real code for: platform_name, logo_url, use_ssl
        // TODO get_platform_setting: 2. Do the system check in run-time.
        platformSettingResponse.setPlatform_name("IOS");
        platformSettingResponse.setPlatform_logo_url("www.google.com");
        platformSettingResponse.setUse_ssl(0);

        String[] mqttUrlsList = mqttSenderConfig.getMqttConnectOptions().getServerURIs();
        platformSettingResponse.setMqtt_server(mqttUrlsList[0]);
//        platformSettingResponse.setMqtt_port("");

        systemErrorResponse.fill(platformSettingResponse, ErrorCodeEnum.ERROR_OK);
        return platformSettingResponse;
    }

    @Override
    public AuthAPIResponse getCloudInfo() {
        // TODO: 配置文件或数据库配置
        cloudInfoResponse.setVersion("1.0");
        cloudInfoResponse.setCloud_name("小雷身份认证平台");
        cloudInfoResponse.setSupported_sdk_version("1.0");
        systemErrorResponse.fill(cloudInfoResponse, ErrorCodeEnum.ERROR_OK);
        return cloudInfoResponse;
    }
}

package com.xiaoleitech.authapi.global.phone;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xiaoleitech.authapi.global.enumeration.ClientTypeEnum;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.Devices;
import com.xiaoleitech.authapi.global.msgqueue.apn.ApnGateway;
import com.xiaoleitech.authapi.global.msgqueue.apollo.MqttGateway;
//import org.json.JSONException;
//import org.json.JSONObject;
import com.xiaoleitech.authapi.global.utils.UtilsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PushMessageHelper {
    private final MqttGateway mqttGateway;
    private final ApnGateway apnGateway;

    @Autowired
    public PushMessageHelper(MqttGateway mqttGateway, ApnGateway apnGateway) {
        this.mqttGateway = mqttGateway;
        this.apnGateway = apnGateway;
    }

    public ErrorCodeEnum sendMessage(Devices device, String message) {
        if (device.getDevice_type() == ClientTypeEnum.CLIENT_TYPE_ANDROID.getClientType()) {
            // 安卓系统
            String topic = "dev-" + device.getDevice_uuid();
            mqttGateway.sendToMqtt(message, topic);
        } else if (device.getDevice_type() == ClientTypeEnum.CLIENT_TYPE_IOS.getClientType()) {
            // iOS系统
            String token = device.getDevice_token();
            String alert;
            alert = UtilsHelper.getValueFromJsonString(message, "alert");
            if (alert.isEmpty())
                return ErrorCodeEnum.ERROR_INTERNAL_ERROR;
            apnGateway.sendMessage(token, alert, message);

            return ErrorCodeEnum.ERROR_NOT_IMPLEMENTED;
        }
        return ErrorCodeEnum.ERROR_OK;
    }
}

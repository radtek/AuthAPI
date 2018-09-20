package com.xiaoleitech.authapi.helper.msgqueue;

import com.xiaoleitech.authapi.model.enumeration.ClientTypeEnum;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.Devices;
import org.json.JSONException;
import org.json.JSONObject;
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
            try {
                JSONObject jsonMessage = new JSONObject(message);
                alert = jsonMessage.getString("alert");
            } catch (JSONException e) {
                e.printStackTrace();
                return ErrorCodeEnum.ERROR_INTERNAL_ERROR;
            }
            apnGateway.sendMessage(token, alert, message);

            return ErrorCodeEnum.ERROR_NOT_IMPLEMENTED;
        }
        return ErrorCodeEnum.ERROR_OK;
    }
}

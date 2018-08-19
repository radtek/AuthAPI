package com.xiaoleitech.authapi.service.registration;

import com.xiaoleitech.authapi.mapper.DevicesMapper;
import com.xiaoleitech.authapi.model.bean.RegisterDeviceRequest;
import com.xiaoleitech.authapi.model.bean.RegisterDeviceResponse;
import com.xiaoleitech.authapi.model.pojo.Devices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RegisterDeviceServiceImpl implements RegisterDeviceService {
    private final
    RegisterDeviceResponse registerDeviceResponse;
    private final
    DevicesMapper devicesMapper;

    @Autowired
    public RegisterDeviceServiceImpl(RegisterDeviceResponse registerDeviceResponse, DevicesMapper devicesMapper) {
        this.registerDeviceResponse = registerDeviceResponse;
        this.devicesMapper = devicesMapper;
    }

    @Override
    public RegisterDeviceResponse registerDevcie(RegisterDeviceRequest registerDeviceRequest) {
        String imei = registerDeviceRequest.getImei();
        List<Devices> searchedDevices = devicesMapper.selectDevicesByIMEI(imei);
        if (searchedDevices.size() > 0) {
            registerDeviceResponse.setError_code(1029);
            registerDeviceResponse.setError_message("设备已注册");
            registerDeviceResponse.setDevice_id("DEV_NULL");
            return registerDeviceResponse;
        }

        Devices device = new Devices();
        device.setDevice_uuid(UUID.randomUUID().toString());
        device.setImei(registerDeviceRequest.getImei());
        device.setState(0);
        device.setProtect_method_capability(registerDeviceRequest.getProtect_method_capability());
        device.setDevice_model(registerDeviceRequest.getDevice_model());
        device.setDevice_tee(registerDeviceRequest.getDevice_tee());
        device.setDevice_se(registerDeviceRequest.getDevice_se());
        device.setDevice_token(registerDeviceRequest.getDevice_token());
        java.util.Date utilDate = new java.util.Date();
        java.sql.Time currentTime = new java.sql.Time(utilDate.getTime());
        device.setCreated_at(currentTime);
        device.setUpdated_at(currentTime);
        int num = devicesMapper.insertDevice(device);

        registerDeviceResponse.setError_code(200);
        registerDeviceResponse.setError_message("OK");
        registerDeviceResponse.setDevice_id("DEV_0001");
        return registerDeviceResponse;
    }
}

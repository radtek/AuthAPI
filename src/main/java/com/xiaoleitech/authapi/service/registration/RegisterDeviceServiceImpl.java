package com.xiaoleitech.authapi.service.registration;

import com.xiaoleitech.authapi.mapper.DevicesMapper;
import com.xiaoleitech.authapi.model.bean.RegisterDeviceRequest;
import com.xiaoleitech.authapi.model.bean.RegisterDeviceResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.Devices;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.UUID;

@Component
public class RegisterDeviceServiceImpl implements RegisterDeviceService {
    private final
    RegisterDeviceResponse registerDeviceResponse;
    private final
    DevicesMapper devicesMapper;
    private final SystemErrorResponse systemErrorResponse;

    @Autowired
    public RegisterDeviceServiceImpl(RegisterDeviceResponse registerDeviceResponse, DevicesMapper devicesMapper, SystemErrorResponse systemErrorResponse) {
        this.registerDeviceResponse = registerDeviceResponse;
        this.devicesMapper = devicesMapper;
        this.systemErrorResponse = systemErrorResponse;
    }

    @Override
    public RegisterDeviceResponse registerDevcie(RegisterDeviceRequest registerDeviceRequest, BindingResult bindingResult) {
        // Check if there are any data-binding error. If any, return the warning message to the caller
        if (systemErrorResponse.checkRequestParams(bindingResult, registerDeviceResponse) != ErrorCodeEnum.ERROR_OK) {
            return registerDeviceResponse;
        }

        // Find the device record, see if it is already registered.
        String imei = registerDeviceRequest.getImei();
        List<Devices> searchedDevices = devicesMapper.selectDevicesByIMEI(imei);
        if (searchedDevices.size() > 0) {
            systemErrorResponse.fillErrorResponse(registerDeviceResponse, ErrorCodeEnum.ERROR_DEVICE_REGISTERED);
            return registerDeviceResponse;
        }

        // TODO: extra data validation if needed

        // Prepare the insert record data
        // TODO: need catch the exception
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
        java.sql.Timestamp currentTime = new java.sql.Timestamp(utilDate.getTime());
        device.setCreated_at(currentTime);
        device.setUpdated_at(currentTime);
        int num = devicesMapper.insertDevice(device);

        //
        registerDeviceResponse.setError_code(200);
        registerDeviceResponse.setError_message("OK");
        registerDeviceResponse.setDevice_id("DEV_0001");
        return registerDeviceResponse;
    }
}

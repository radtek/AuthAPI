package com.xiaoleitech.authapi.registration.service;

import com.xiaoleitech.authapi.dao.helper.DevicesTableHelper;
import com.xiaoleitech.authapi.global.utils.UtilsHelper;
import com.xiaoleitech.authapi.dao.mybatis.mapper.DevicesMapper;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.DeviceStateEnum;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.Devices;
import com.xiaoleitech.authapi.registration.bean.request.RegisterDeviceRequest;
import com.xiaoleitech.authapi.registration.bean.response.RegisterDeviceResponse;
import com.xiaoleitech.authapi.registration.bean.response.UnregisterDeviceResponse;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

@Component
public class RegisterDeviceServiceImpl implements RegisterDeviceService {
    private final RegisterDeviceResponse registerDeviceResponse;
    private final UnregisterDeviceResponse unregisterDeviceResponse;
    private final DevicesMapper devicesMapper;
    private final SystemErrorResponse systemErrorResponse;
    private final DevicesTableHelper devicesTableHelper;

    @Autowired
    public RegisterDeviceServiceImpl(RegisterDeviceResponse registerDeviceResponse, UnregisterDeviceResponse unregisterDeviceResponse, DevicesMapper devicesMapper, SystemErrorResponse systemErrorResponse, DevicesTableHelper devicesTableHelper) {
        this.registerDeviceResponse = registerDeviceResponse;
        this.unregisterDeviceResponse = unregisterDeviceResponse;
        this.devicesMapper = devicesMapper;
        this.systemErrorResponse = systemErrorResponse;
        this.devicesTableHelper = devicesTableHelper;
    }

    @Transactional
    @Override
    public AuthAPIResponse registerDevice(RegisterDeviceRequest registerDeviceRequest, BindingResult bindingResult) {
        ErrorCodeEnum errorCode;
        AuthAPIResponse authAPIResponse = new AuthAPIResponse();

        // Check if there are any data-binding error. If any, return the warning message to the caller
        if (systemErrorResponse.checkRequestParams(bindingResult, authAPIResponse) != ErrorCodeEnum.ERROR_OK) {
            return authAPIResponse;
        }

        // TODO: extra data validation if needed

        // Find the device record, see if it is already registered.
        Devices device = devicesTableHelper.getDeviceByImei(registerDeviceRequest.getImei());
        if (device != null) {
            // 系统中有设备记录，检查设备注册状态
            if ((device.getState()) == DeviceStateEnum.DEV_REGISTER_AND_BINDING.getState() ||
                    (device.getState()) == DeviceStateEnum.DEV_REGISTER_NO_BINDING.getState()) {
                // 设备已注册，不管是否已绑定，均返回错误
                registerDeviceResponse.setDevice_id(device.getDevice_uuid());
                systemErrorResponse.fillErrorResponse(registerDeviceResponse, ErrorCodeEnum.ERROR_DEVICE_REGISTERED);
                return registerDeviceResponse;
            } else {
                // 更新设备记录（状态、型号、令牌、TEE、SE、更新时间...）
                // 使用Request数据填充Device
                copyDeviceParamsFromRequest(registerDeviceRequest, device);

                // 设置Device的状态
                device.setState(DeviceStateEnum.DEV_REGISTER_AND_BINDING.getState());
                errorCode = devicesTableHelper.updateOneDeviceRecord(device);
                if (errorCode != ErrorCodeEnum.ERROR_OK) {
                    return systemErrorResponse.getGeneralResponse(errorCode);
                }
            }
        } else {
            // 系统中找不到该设备，增加一条设备新记录
            errorCode = addNewDeviceRecord(registerDeviceRequest);
            if (errorCode != ErrorCodeEnum.ERROR_OK) {
                return systemErrorResponse.getGeneralResponse(errorCode);
            }
        }

        // 返回成功
        systemErrorResponse.fillErrorResponse(registerDeviceResponse, ErrorCodeEnum.ERROR_OK);
        registerDeviceResponse.setDevice_id(devicesTableHelper.getDevicesUuidByImei(registerDeviceRequest.getImei()));
        return registerDeviceResponse;
    }

    @Transactional
    @Override
    public AuthAPIResponse unregisterDevice(String deviceId) {
        Devices device = devicesTableHelper.getDeviceByUuid(deviceId);

        // 系统中找不到指定ID的设备，返回无效设备错误信息
        if (device == null) {
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_DEVICE_NOT_FOUND);
        }

        // 设置设备状态（设备注销）
        device.setState(DeviceStateEnum.DEV_LOGICAL_DELETE.getState());

        // 更新设备记录并返回 response
        return systemErrorResponse.getGeneralResponse(devicesTableHelper.updateOneDeviceRecord(device));
    }

    private ErrorCodeEnum copyDeviceParamsFromRequest(RegisterDeviceRequest registerDeviceRequest, Devices device) {
        // 使用Request数据填充Device
        device.setImei(registerDeviceRequest.getImei());
        device.setProtect_method_capability(registerDeviceRequest.getProtect_method_capability());
        device.setDevice_model(registerDeviceRequest.getDevice_model());
        device.setDevice_tee(registerDeviceRequest.getDevice_tee());
        device.setDevice_se(registerDeviceRequest.getDevice_se());
        device.setDevice_token(registerDeviceRequest.getDevice_token());

        return ErrorCodeEnum.ERROR_OK;
    }

    private ErrorCodeEnum addNewDeviceRecord(RegisterDeviceRequest registerDeviceRequest) {
        // 准备需要插入的设备记录
        Devices device = new Devices();

        // 使用Request数据填充Device
        copyDeviceParamsFromRequest(registerDeviceRequest, device);

        // 设置Device的UUID、状态、创建时间和更新时间
        device.setDevice_uuid(UtilsHelper.generateUuid());
        device.setState(DeviceStateEnum.DEV_REGISTER_AND_BINDING.getState());
        java.sql.Timestamp currentTime = UtilsHelper.getCurrentSystemTimestamp();
        device.setCreated_at(currentTime);
        device.setUpdated_at(currentTime);

        // insertOneDevice 方法返回插入的记录数量，如果成功了，则返回1
        // TODO: need catch the exception ?
        int num = devicesMapper.insertOneDevice(device);

        return (num == 1) ? ErrorCodeEnum.ERROR_OK : ErrorCodeEnum.ERROR_CANNOT_ENROLL;
    }

}

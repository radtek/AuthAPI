package com.xiaoleitech.authapi.service.registration;

import com.xiaoleitech.authapi.helper.DevicesTableHelper;
import com.xiaoleitech.authapi.helper.UtilsHelper;
import com.xiaoleitech.authapi.mapper.DevicesMapper;
import com.xiaoleitech.authapi.model.bean.RegisterDeviceRequest;
import com.xiaoleitech.authapi.model.bean.RegisterDeviceResponse;
import com.xiaoleitech.authapi.model.bean.UnregisterDeviceResponse;
import com.xiaoleitech.authapi.model.enumeration.DeviceStateEnum;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.Devices;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

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
    public RegisterDeviceResponse registerDevcie(RegisterDeviceRequest registerDeviceRequest, BindingResult bindingResult) {
        ErrorCodeEnum errorCode;

        // Check if there are any data-binding error. If any, return the warning message to the caller
        if (systemErrorResponse.checkRequestParams(bindingResult, registerDeviceResponse) != ErrorCodeEnum.ERROR_OK) {
            return registerDeviceResponse;
        }

        // TODO: extra data validation if needed

        // Find the device record, see if it is already registered.
        Devices device = devicesTableHelper.getDeviceByImei(registerDeviceRequest.getImei());
        if (device != null) {
            // 系统中有设备记录，检查设备注册状态
            if ((device.getState()) == DeviceStateEnum.DEV_REGISTER_AND_BINDING.getState() ||
                    (device.getState()) == DeviceStateEnum.DEV_REGISTER_NO_BINDING.getState()) {
                // 设备已注册，不管是否已绑定，均返回错误
                systemErrorResponse.fillErrorResponse(registerDeviceResponse, ErrorCodeEnum.ERROR_DEVICE_REGISTERED);
                registerDeviceResponse.setDevice_id(device.getDevice_uuid());
                return registerDeviceResponse;
            } else {
                // 更新设备记录（状态、型号、令牌、TEE、SE、更新时间...）
                errorCode = updateDevice(registerDeviceRequest, device, DeviceStateEnum.DEV_REGISTER_AND_BINDING);
                if (errorCode != ErrorCodeEnum.ERROR_OK) {
                    systemErrorResponse.fillErrorResponse(registerDeviceResponse, errorCode);
                    return registerDeviceResponse;
                }
            }
        } else {
            // 系统中找不到该设备，增加一条设备新记录
            errorCode = addNewDeviceRecord(registerDeviceRequest);
            if (errorCode != ErrorCodeEnum.ERROR_OK) {
                systemErrorResponse.fillErrorResponse(registerDeviceResponse, errorCode);
                return registerDeviceResponse;
            }
        }

        // 返回成功
        systemErrorResponse.fillErrorResponse(registerDeviceResponse, ErrorCodeEnum.ERROR_HTTP_SUCCESS);
        // TODO: 返回的 device_id 是UUID吗？
        registerDeviceResponse.setDevice_id(devicesTableHelper.getDevicesUuidByImei(registerDeviceRequest.getImei()));
        return registerDeviceResponse;
    }

    @Transactional
    @Override
    public UnregisterDeviceResponse unregisterDevice(@RequestParam("device_id") String deviceID) {
        Devices device = devicesTableHelper.getDeviceByUuid(deviceID);

        // 系统中找不到指定ID的设备，返回无效设备错误信息
        if (device == null) {
            systemErrorResponse.fillErrorResponse(unregisterDeviceResponse, ErrorCodeEnum.ERROR_DEVICE_NOT_FOUND);
            return unregisterDeviceResponse;
        }

        // 设置设备状态（设备注销）和更新时间
        device.setState(DeviceStateEnum.DEV_UNREGISTER.getState());
        device.setUpdated_at(UtilsHelper.getCurrentSystemTimestamp());
        int num = devicesMapper.updateDeviceRecord(device);

        // 成功更新一条记录，返回成功信息，否则返回内部错误
        systemErrorResponse.fillErrorResponse(unregisterDeviceResponse,
                (num == 1) ? ErrorCodeEnum.ERROR_HTTP_SUCCESS : ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        return unregisterDeviceResponse;
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
        device.setDevice_uuid(UUID.randomUUID().toString());
        device.setState(DeviceStateEnum.DEV_REGISTER_AND_BINDING.getState());
        java.sql.Timestamp currentTime = UtilsHelper.getCurrentSystemTimestamp();
        device.setCreated_at(currentTime);
        device.setUpdated_at(currentTime);

        // insertOneDevice 方法返回插入的记录数量，如果成功了，则返回1
        // TODO: need catch the exception ?
        int num = devicesMapper.insertOneDevice(device);

        return (num == 1) ? ErrorCodeEnum.ERROR_OK : ErrorCodeEnum.ERROR_CANNOT_ENROLL;
    }

    private ErrorCodeEnum updateDevice(RegisterDeviceRequest registerDeviceRequest, Devices device, DeviceStateEnum deviceState) {
        // 使用Request数据填充Device
        copyDeviceParamsFromRequest(registerDeviceRequest, device);

        // 设置Device的状态和更新时间
        device.setState(deviceState.getState());
        device.setUpdated_at(UtilsHelper.getCurrentSystemTimestamp());

        // 更新此设备记录的所有可能存在变化的字段
        int num = devicesMapper.updateDeviceRecord(device);
        return (num == 1) ? ErrorCodeEnum.ERROR_OK : ErrorCodeEnum.ERROR_CANNOT_ENROLL;
    }

}

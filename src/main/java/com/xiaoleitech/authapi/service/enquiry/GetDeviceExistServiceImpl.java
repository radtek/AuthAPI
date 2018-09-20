package com.xiaoleitech.authapi.service.enquiry;

import com.xiaoleitech.authapi.helper.table.DevicesTableHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enquiry.DeviceExistResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.Devices;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.stereotype.Component;

@Component
public class GetDeviceExistServiceImpl implements GetDeviceExistService {
    private final SystemErrorResponse systemErrorResponse;
    private final DevicesTableHelper devicesTableHelper;
    private final DeviceExistResponse deviceExistResponse;

    public GetDeviceExistServiceImpl(SystemErrorResponse systemErrorResponse, DevicesTableHelper devicesTableHelper, DeviceExistResponse deviceExistResponse) {
        this.systemErrorResponse = systemErrorResponse;
        this.devicesTableHelper = devicesTableHelper;
        this.deviceExistResponse = deviceExistResponse;
    }

    @Override
    public AuthAPIResponse getDeviceExist(String imei) {
        // 检查请求参数
        if (imei.isEmpty())
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 根据 IMEI 码获取 devices 记录
        Devices device = devicesTableHelper.getDeviceByImei(imei);
        if (device == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_DEVICE_NOT_FOUND);

        deviceExistResponse.setDevice_id(device.getDevice_uuid());
        systemErrorResponse.fillErrorResponse(deviceExistResponse, ErrorCodeEnum.ERROR_DEVICE_REGISTERED);
        return deviceExistResponse;
    }
}

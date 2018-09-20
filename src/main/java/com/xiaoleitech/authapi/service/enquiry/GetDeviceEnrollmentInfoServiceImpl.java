package com.xiaoleitech.authapi.service.enquiry;

import com.xiaoleitech.authapi.helper.jointable.JoinRelyPartAccountHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enquiry.DeviceEnrollmentInfoResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.EnrollDeviceInfo;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class GetDeviceEnrollmentInfoServiceImpl implements GetDeviceEnrollmentInfoService {
    private final DeviceEnrollmentInfoResponse deviceEnrollmentInfoResponse;
    private final SystemErrorResponse systemErrorResponse;
    private final JoinRelyPartAccountHelper joinRelyPartAccountHelper;

    public GetDeviceEnrollmentInfoServiceImpl(DeviceEnrollmentInfoResponse deviceEnrollmentInfoResponse, SystemErrorResponse systemErrorResponse, JoinRelyPartAccountHelper joinRelyPartAccountHelper) {
        this.deviceEnrollmentInfoResponse = deviceEnrollmentInfoResponse;
        this.systemErrorResponse = systemErrorResponse;
        this.joinRelyPartAccountHelper = joinRelyPartAccountHelper;
    }

    @Override
    public AuthAPIResponse getDeviceEnrollmentInfo(String userUuid, String deviceUuid, String appUuid) {
        // 检查请求参数
        if ( (userUuid.isEmpty()) || (deviceUuid.isEmpty()) || (appUuid.isEmpty()))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 获取设备绑定应用信息
        EnrollDeviceInfo enrollDeviceInfo = joinRelyPartAccountHelper.getEnrollDeviceInfo(userUuid, deviceUuid, appUuid);
        if (enrollDeviceInfo == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_ACCOUNT);

        BeanUtils.copyProperties(enrollDeviceInfo, deviceEnrollmentInfoResponse);
        systemErrorResponse.fillErrorResponse(deviceEnrollmentInfoResponse, ErrorCodeEnum.ERROR_OK);
        return deviceEnrollmentInfoResponse;
    }

}

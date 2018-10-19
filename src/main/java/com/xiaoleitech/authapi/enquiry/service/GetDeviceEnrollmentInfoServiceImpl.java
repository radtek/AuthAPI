package com.xiaoleitech.authapi.enquiry.service;

import com.xiaoleitech.authapi.dao.jointable.JoinRelyPartAccountHelper;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.enquiry.bean.response.DeviceEnrollmentInfoResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.EnrollDeviceInfo;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
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
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 获取设备绑定应用信息
        EnrollDeviceInfo enrollDeviceInfo = joinRelyPartAccountHelper.getEnrollDeviceInfo(userUuid, deviceUuid, appUuid);
        if (enrollDeviceInfo == null)
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_INVALID_ACCOUNT);

        BeanUtils.copyProperties(enrollDeviceInfo, deviceEnrollmentInfoResponse);
        systemErrorResponse.fill(deviceEnrollmentInfoResponse, ErrorCodeEnum.ERROR_OK);
        return deviceEnrollmentInfoResponse;
    }

}

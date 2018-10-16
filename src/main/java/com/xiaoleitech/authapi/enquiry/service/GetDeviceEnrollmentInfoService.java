package com.xiaoleitech.authapi.enquiry.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetDeviceEnrollmentInfoService {
    AuthAPIResponse getDeviceEnrollmentInfo(String userUuid, String deviceUuid, String appUuid);
}

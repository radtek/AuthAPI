package com.xiaoleitech.authapi.service.enquiry;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetDeviceEnrollmentInfoService {
    AuthAPIResponse getDeviceEnrollmentInfo(String userUuid, String deviceUuid, String appUuid);
}

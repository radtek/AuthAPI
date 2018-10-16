package com.xiaoleitech.authapi.enquiry.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetAppEnrollmentInfoService {
    AuthAPIResponse getAppEnrollmentInfo(String appUuid);
}

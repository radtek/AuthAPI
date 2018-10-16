package com.xiaoleitech.authapi.enquiry.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetUserEnrollmentInfoService {
    AuthAPIResponse getUserEnrollInfo(String userUuid, String verifyToken);
}

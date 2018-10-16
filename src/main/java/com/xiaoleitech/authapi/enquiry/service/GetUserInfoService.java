package com.xiaoleitech.authapi.enquiry.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetUserInfoService {
    AuthAPIResponse getUserInfo(String userUuid, String verifyToken);
}

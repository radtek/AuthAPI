package com.xiaoleitech.authapi.enrollment.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface SetAccountProtectMethodsService {
    AuthAPIResponse setAccountProtectMethods(String userUuid, String verifyToken, String appUuid, String protectMethods);
}

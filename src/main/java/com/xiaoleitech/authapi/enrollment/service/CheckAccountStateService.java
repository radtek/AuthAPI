package com.xiaoleitech.authapi.enrollment.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface CheckAccountStateService {

    AuthAPIResponse checkAccountState(String userUuid, String verifyToken, String appUuid);
}

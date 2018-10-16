package com.xiaoleitech.authapi.authorization.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface OtpAuthorizeService {

    AuthAPIResponse otpAuthorize(String appUuid, String token, String accountUuid, String accountName, String otp);
}

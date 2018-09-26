package com.xiaoleitech.authapi.service.authorization;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface OtpAuthorizeService {

    AuthAPIResponse otpAuthorize(String appUuid, String token, String accountUuid, String otp, String nonce);
}

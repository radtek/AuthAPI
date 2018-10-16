package com.xiaoleitech.authapi.phoneverify.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface VerifySmsCodeService {
    AuthAPIResponse verifySmsCode(String phoneNo, String smsCode);
}

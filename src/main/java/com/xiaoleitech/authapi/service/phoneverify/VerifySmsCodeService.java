package com.xiaoleitech.authapi.service.phoneverify;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public interface VerifySmsCodeService {
    AuthAPIResponse verifySmsCode(String phoneNo, String smsCode);
}

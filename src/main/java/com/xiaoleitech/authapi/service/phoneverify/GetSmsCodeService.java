package com.xiaoleitech.authapi.service.phoneverify;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetSmsCodeService {
    AuthAPIResponse getSmsCode(String phoneNo);
}

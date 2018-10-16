package com.xiaoleitech.authapi.enrollment.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface ActiveAccountService {

    AuthAPIResponse activeAccount(String appUuid, String token, String appAccountUuid);
}

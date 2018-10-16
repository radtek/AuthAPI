package com.xiaoleitech.authapi.authorization.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface AppLogoutAccountService {

    AuthAPIResponse logoutAccount(String appUuid, String token, String appAccountUuid);
}

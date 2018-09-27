package com.xiaoleitech.authapi.service.authorization;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface AppLogoutAccountService {

    AuthAPIResponse logoutAccount(String appUuid, String token, String appAccountUuid);
}
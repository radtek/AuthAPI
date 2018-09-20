package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface ActiveAccountService {

    AuthAPIResponse activeAccount(String appUuid, String token, String appAccountUuid);
}

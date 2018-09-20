package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface SetAccountProtectMethodsService {
    AuthAPIResponse setAccountProtectMethods(String userUuid, String verifyToken, String appUuid, String protectMethods);
}

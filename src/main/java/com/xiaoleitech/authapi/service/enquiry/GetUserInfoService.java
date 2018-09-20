package com.xiaoleitech.authapi.service.enquiry;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetUserInfoService {
    AuthAPIResponse getUserInfo(String userUuid, String verifyToken);
}

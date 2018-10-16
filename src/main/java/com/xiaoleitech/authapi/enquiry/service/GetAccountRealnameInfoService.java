package com.xiaoleitech.authapi.enquiry.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetAccountRealnameInfoService {
    AuthAPIResponse getRealnameInfo(String appUuid, String token, String accountUuid);
}

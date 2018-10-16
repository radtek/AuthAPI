package com.xiaoleitech.authapi.enquiry.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetAccountInfoService {
    AuthAPIResponse getAccountInfo(String appUuid, String token, String accountUuid, String accountName);
}

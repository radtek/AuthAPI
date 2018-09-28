package com.xiaoleitech.authapi.service.enquiry;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetAccountInfoService {
    AuthAPIResponse getAccountInfo(String appUuid, String token, String accountUuid, String accountName);
}

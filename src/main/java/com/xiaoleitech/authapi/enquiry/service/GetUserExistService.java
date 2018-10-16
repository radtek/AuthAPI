package com.xiaoleitech.authapi.enquiry.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetUserExistService {
    AuthAPIResponse getUserExist(String phoneNo, String appUuid);
}

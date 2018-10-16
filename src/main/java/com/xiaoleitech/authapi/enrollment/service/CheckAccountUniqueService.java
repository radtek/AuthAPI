package com.xiaoleitech.authapi.enrollment.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface CheckAccountUniqueService {

    AuthAPIResponse checkAccountUnique(String appUuid, String appAccountName);
}

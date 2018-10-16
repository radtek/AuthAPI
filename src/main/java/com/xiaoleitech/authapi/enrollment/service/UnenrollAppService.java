package com.xiaoleitech.authapi.enrollment.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface UnenrollAppService {

    AuthAPIResponse unenrollApp(String userUuid, String verifyToken, String appUuid);
}

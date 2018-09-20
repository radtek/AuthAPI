package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface UnenrollAppService {

    AuthAPIResponse unenrollApp(String userUuid, String verifyToken, String appUuid);
}

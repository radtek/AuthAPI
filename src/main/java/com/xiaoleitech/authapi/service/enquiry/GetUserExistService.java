package com.xiaoleitech.authapi.service.enquiry;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetUserExistService {
    AuthAPIResponse getUserExist(String phoneNo, String appUuid);
}

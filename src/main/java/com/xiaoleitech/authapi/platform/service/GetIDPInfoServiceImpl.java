package com.xiaoleitech.authapi.platform.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.platform.bean.response.IDPInfoResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetIDPInfoServiceImpl implements GetIDPInfoService {
    private final IDPInfoResponse idpInfoResponse;
    private final SystemErrorResponse systemErrorResponse;

    @Autowired
    public GetIDPInfoServiceImpl(IDPInfoResponse idpInfoResponse, SystemErrorResponse systemErrorResponse) {
        this.idpInfoResponse = idpInfoResponse;
        this.systemErrorResponse = systemErrorResponse;
    }

    @Override
    public AuthAPIResponse getIDPInfo() {
        // TODO get_idp_info: 1. retrieve the real version.
        // TODO get_idp_info: 2. Treat with the exception.
        idpInfoResponse.setVersion("1.0.0.0");
        idpInfoResponse.setSupported_ios_app_version("1.0.0.0");
        idpInfoResponse.setSupported_android_app_version("1.0.0.0");

        systemErrorResponse.fillErrorResponse(idpInfoResponse, ErrorCodeEnum.ERROR_OK);

        return idpInfoResponse;
    }
}

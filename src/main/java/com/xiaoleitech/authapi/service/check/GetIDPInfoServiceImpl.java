package com.xiaoleitech.authapi.service.check;

import com.xiaoleitech.authapi.model.bean.IDPInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetIDPInfoServiceImpl implements GetIDPInfoService {
    private final
    IDPInfoResponse idpInfoResponse;

    @Autowired
    public GetIDPInfoServiceImpl(IDPInfoResponse idpInfoResponse) {
        this.idpInfoResponse = idpInfoResponse;
    }

    @Override
    public IDPInfoResponse getIDPInfo() {
        // TODO get_idp_info: 1. retrieve the real version.
        // TODO get_idp_info: 2. Treat with the exception.
        idpInfoResponse.setError_code(200);
        idpInfoResponse.setError_message("OK");
        idpInfoResponse.setVersion("1.0.0.1");
        idpInfoResponse.setSupported_ios_app_version("10.1");
        idpInfoResponse.setSupported_android_app_version("6.0");

        return idpInfoResponse;
    }
}

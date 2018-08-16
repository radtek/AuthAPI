package com.xiaoleitech.authapi;

import com.xiaoleitech.authapi.pojo.IDPInfoResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class XL_CheckAPI {
    @RequestMapping(value = "/api/ping", method = RequestMethod.GET)
    public String ping() {
        return "200: OK";
    }

    @RequestMapping(value = "/api/get_idp_info", method = RequestMethod.GET)
    public @ResponseBody
    IDPInfoResponse get_idp_info() {
        IDPInfoResponse idpInfoResponse = new IDPInfoResponse();
        idpInfoResponse.setError_code(0);
        idpInfoResponse.setError_message("Success");
        idpInfoResponse.setVersion("1.0.0.1");
        idpInfoResponse.setSupported_ios_app_version("10.1");
        idpInfoResponse.setSupported_android_app_version("6.0");

        return idpInfoResponse;
    }
}

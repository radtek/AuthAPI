package com.xiaoleitech.authapi.service.platform;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.platform.PlatformSettingResponse;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlatformServiceImpl implements PlatformService {
    private final SystemErrorResponse systemErrorResponse;

    @Autowired
    public PlatformServiceImpl(SystemErrorResponse systemErrorResponse) {
        this.systemErrorResponse = systemErrorResponse;
    }

    @Override
    public AuthAPIResponse getPlatformSetting() {
        PlatformSettingResponse platformSettingResponse = new PlatformSettingResponse();

        // TODO get_platform_setting: 1. Implement the real code for: platform_name, logo_url, use_ssl
        // TODO get_platform_setting: 2. Do the system check in run-time.
        platformSettingResponse.setPlatform_name("IOS");
        platformSettingResponse.setPlatform_logo_url("www.google.com");
        platformSettingResponse.setUse_ssl(0);

        systemErrorResponse.fillErrorResponse(platformSettingResponse, ErrorCodeEnum.ERROR_HTTP_SUCCESS);
        return platformSettingResponse;
    }
}

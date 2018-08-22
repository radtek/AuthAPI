package com.xiaoleitech.authapi.service.platform;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.bean.PlatformSettingResponse;
import org.springframework.stereotype.Component;

@Component
public class PlatformServiceImpl implements PlatformService {
    @Override
    public AuthAPIResponse getPlatformSetting() {
        PlatformSettingResponse platformSettingResponse = new PlatformSettingResponse();

        // TODO get_platform_setting: 1. Implement the real code for: platform_name, logo_url, use_ssl
        // TODO get_platform_setting: 2. Do the system check in run-time.
        platformSettingResponse.setError_code(200);
        platformSettingResponse.setError_message("Success");
        platformSettingResponse.setPlatform_name("IOS");
        platformSettingResponse.setPlatform_logo_url("www.google.com");
        platformSettingResponse.setUse_ssl(0);
        return platformSettingResponse;
    }
}

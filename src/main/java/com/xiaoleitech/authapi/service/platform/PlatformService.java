package com.xiaoleitech.authapi.service.platform;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;

public interface PlatformService {
    /**
     * 获得平台设置
     * get https://server/api/get_platform_setting
     *
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * platform_name: platform_name,
     * platform_logo_url: platform_logo_url,
     * use_ssl: use_ssl // 0: not use ssl, 1: RSA2048/SHA256, 2: SM2/SM3
     * }
     */
    AuthAPIResponse getPlatformSetting();
}

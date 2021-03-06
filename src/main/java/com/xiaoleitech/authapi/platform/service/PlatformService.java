package com.xiaoleitech.authapi.platform.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;

public interface PlatformService {
    /**
     * 获得平台设置
     * get https://server/api/get_platform_setting
     *
     * @return {
     * error_code: errorCode,
     * error_message: errorMessage,
     * platform_name: platformName,
     * platform_logo_url: platform_logo_url,
     * use_ssl: use_ssl // 0: not use ssl, 1: RSA2048/SHA256, 2: SM2/SM3
     * }
     */
    AuthAPIResponse getPlatformSetting();

    AuthAPIResponse getCloudInfo();
}

package com.xiaoleitech.authapi.platform.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;

public interface GetIDPInfoService {
    /**
     * 获取IDP资料
     * get https://server/api/get_idp_info
     *
     * @return {
     * error_code: errorCode,
     * error_message: errorMessage,
     * version: version,
     * supported_ios_app_version: version,
     * supported_android_app_version: version
     * }
     */
    AuthAPIResponse getIDPInfo();
}

package com.xiaoleitech.authapi.service.check;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;

public interface GetIDPInfoService {
    /**
     * 获取IDP资料
     * get https://server/api/get_idp_info
     *
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * version: version,
     * supported_ios_app_version: version,
     * supported_android_app_version: version
     * }
     */
    AuthAPIResponse getIDPInfo();
}

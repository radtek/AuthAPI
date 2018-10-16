package com.xiaoleitech.authapi.platform.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;

public interface PingService {
    AuthAPIResponse ping();

    /**
     * PING (RP/APP)
     * get http://server/api/ping
     *
     * @return 200: OK
     */
    String pingResponseString();
}

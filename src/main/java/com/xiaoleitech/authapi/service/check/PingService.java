package com.xiaoleitech.authapi.service.check;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;

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

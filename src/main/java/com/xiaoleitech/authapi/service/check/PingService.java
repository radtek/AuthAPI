package com.xiaoleitech.authapi.service.check;

import com.xiaoleitech.authapi.model.bean.PingResponse;

public interface PingService {
    /**
     * PING (RP/APP)
     * get http://server/api/ping
     *
     * @return 200: OK
     */
    PingResponse ping();

    String pingResponseString();
}

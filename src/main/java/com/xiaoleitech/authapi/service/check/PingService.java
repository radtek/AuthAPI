package com.xiaoleitech.authapi.service.check;

import com.xiaoleitech.authapi.model.bean.PingResponse;

public interface PingService {
    PingResponse ping();

    String pingResponseString();
}

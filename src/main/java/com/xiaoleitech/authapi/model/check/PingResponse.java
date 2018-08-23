package com.xiaoleitech.authapi.model.check;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public class PingResponse extends AuthAPIResponse {
    // For Now, besides the ErrorInfo in AuthAPIResponse, no other data are needed.
}

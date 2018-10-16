package com.xiaoleitech.authapi.authentication.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAuthFailResponse  extends AuthAPIResponse {
    private int remain_retry_count;
}

package com.xiaoleitech.authapi.enrollment.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class CheckAccountStateResponse extends AuthAPIResponse {
    private int account_state;
}

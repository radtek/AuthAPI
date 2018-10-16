package com.xiaoleitech.authapi.enrollment.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class EnrollAppResponse extends AuthAPIResponse {
    private String app_account_id;
    private int account_state;
    private int authorization_policy;
    private int otp_alg;
    private int otp_inteval;
    private String otp_seed;
    private int otp_digits;
}

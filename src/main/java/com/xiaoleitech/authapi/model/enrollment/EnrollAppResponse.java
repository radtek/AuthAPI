package com.xiaoleitech.authapi.model.enrollment;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class EnrollAppResponse extends AuthAPIResponse {
    private String app_account_id;
    private int account_state;
    private String authorization_policy;
    private int otp_alg;
    private int otp_inteval;
    private String otp_seed;
    private int otp_digits;
}

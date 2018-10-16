package com.xiaoleitech.authapi.authorization.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class OtpAuthResponse extends AuthAPIResponse {
    private String redirect_url;
}

package com.xiaoleitech.authapi.rpmanage.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class ResetRelyPartAuthKeyResponse extends AuthAPIResponse {
    private String app_key;
}

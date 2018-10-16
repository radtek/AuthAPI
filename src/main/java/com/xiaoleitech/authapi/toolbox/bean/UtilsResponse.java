package com.xiaoleitech.authapi.toolbox.bean;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class UtilsResponse extends AuthAPIResponse {
    private String response;
}

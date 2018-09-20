package com.xiaoleitech.authapi.controller.utilities;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class UtilsResponse extends AuthAPIResponse {
    private String response;
}

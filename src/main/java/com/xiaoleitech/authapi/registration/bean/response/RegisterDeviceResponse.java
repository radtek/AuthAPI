package com.xiaoleitech.authapi.registration.bean.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
// JsonInclude(...) 用于在组装响应数据为JSON时，忽略 null 属性（不组装）。（未经验证）
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterDeviceResponse extends AuthAPIResponse {
    private String device_id;
}

package com.xiaoleitech.authapi.registration.bean.request;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterDeviceRequest {
    @NotBlank(message = "IMEI不能为空！")
    private String imei;
    @NotBlank(message = "验证方式不能为空！")
    private String protect_method_capability;
    // TODO: Find the way to react with the input int params blank, NotNull has no effect.
    // Binding result is :Failed to convert property value of type 'java.lang.String' to required type 'int' for property 'device_type';
    // nested exception is java.lang.NumberFormatException: For input string: \"\"
    //@NotNull(message = "必须填写设备类型！")
//    @Range(min = 1, max = 9, message = "设备类型取值范围1--9！")
    private int device_type;
    @NotBlank(message = "设备型号不能为空！")
    private String device_model;
    //    @Range(min = 1, max = 9, message = "TEE取值范围1--9！")
    private int device_tee;
    //    @Range(min = 1, max = 9, message = "SE取值范围1--9！")
    private int device_se;
    //    @NotBlank(message = "设备令牌不能为空！")
    private String device_token;
}

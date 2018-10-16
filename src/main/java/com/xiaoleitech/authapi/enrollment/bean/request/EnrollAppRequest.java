package com.xiaoleitech.authapi.enrollment.bean.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EnrollAppRequest {
    private String app_id;
    private String user_id;
    @NotBlank(message = "验证令牌不能为空！")
    private String verify_token;
    private String account_name;
    private String protect_methods;
    private String id_code;
    private String active_code;
    private int sex;
    private String email;
}

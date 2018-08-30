package com.xiaoleitech.authapi.model.enrollment;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EnrollAppRequest {
    private int app_id;
    private int user_id;
    @NotBlank(message = "验证令牌不能为空！")
    private String verify_token;
    private String account_name;
    private String protect_methods;
    private String id_code;
    private String active_code;
    private int sex;
    private String email;
}

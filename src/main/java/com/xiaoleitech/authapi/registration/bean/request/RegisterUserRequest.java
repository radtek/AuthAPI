package com.xiaoleitech.authapi.registration.bean.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterUserRequest {
    @NotBlank(message = "电话号码不能为空！")
    private String phone_no;
    @NotBlank(message = "密码不能为空！")
    private String password;
    private String device_id;
    //    @NotBlank(message = "用户真实姓名不能为空！")
    private String user_realname;
    //    @NotBlank(message = "用户身份证号不能为空！")
    private String id_no;
    private String real_address;
    //    @NotBlank(message = "保护方式不能为空！")
    private String protect_methods;
    //    @NotBlank(message = "用户证书公钥不能为空！")
    private String user_certificate_public_key;
}

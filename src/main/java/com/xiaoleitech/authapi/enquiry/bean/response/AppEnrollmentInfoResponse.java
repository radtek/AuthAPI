package com.xiaoleitech.authapi.enquiry.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class AppEnrollmentInfoResponse extends AuthAPIResponse {
    private String app_name;
    private String app_logo;
    private String app_protect_methods;
    private String obtain_realname_info_scope;
    private int need_uniq_username;
    private int new_account_policy;
    private int need_info;
    private int use_cert;
    private int cert_type;
    private String cert_template;
}

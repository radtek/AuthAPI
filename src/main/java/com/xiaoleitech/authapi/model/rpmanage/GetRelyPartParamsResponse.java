package com.xiaoleitech.authapi.model.rpmanage;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class GetRelyPartParamsResponse extends AuthAPIResponse {
    private String rp_name;
    private String rp_logo;
    private String verify_methods;
    private String realname_scope;
    private int new_account_policy;
    private int account_name_policy;
    private int auth_life_time;
    private int otp_alg;
    private int otp_digits;
    private int otp_interval;
    private int otp_cover;
    private int otp_c;
    private String otp_q;
    private int need_active_code;
    private int use_cert;
    private int cert_type;
    private int ca_id;
    private int need_info;
    private String rp_account_authorized_callback_url;
    private String rp_account_unauthorized_callback_url;
    private String rp_account_enrolleded_callback_url;
    private String rp_account_unenrolled_callback_url;
    private String rp_account_exist_callback_url;
}

package com.xiaoleitech.authapi.model.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 依赖方（渠道）
 */
@Component
@Data
public class RelyParts {
    private int rp_id;
    private int manager_id;
    private String rp_uuid;
    private String rp_name;
    private String rp_logo_file_url;
    private String rp_login_redirection_url;
    private String rp_account_authorized_callback_url;
    private String rp_account_unauthorized_callback_url;
    private String rp_account_enroll_callback_url;
    private String rp_account_unenroll_callback_url;
    private int state;
    private java.sql.Timestamp expire_at;
    private int client_type;
    private String real_name_scope;
    private String rp_protect_methods;
    private int new_account_policy;
    private int uniq_account_name;
    private int authorization_policy;
    private int auth_life_time;
    private int use_cert;
    private int cert_type;
    private int caid;
    private String app_key;
    private int otp_alg;
    private int otp_digits;
    private int inteval;
    private int strong;
    private int otp_c;
    private String otp_q;
    private int need_active_code;
    private java.sql.Timestamp created_at;
    private java.sql.Timestamp updated_at;
    private String otp_key;
    private int need_info;
    private String rp_account_exist_callback_url;
}

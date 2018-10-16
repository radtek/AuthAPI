package com.xiaoleitech.authapi.dao.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class EnrollUserInfo {
    private String app_id;
    private String app_name;
    private String app_logo;
    private String obtain_realname_info_scope;
    private String app_protect_methods;
    private int need_uniq_username; // 3:不需要用户名；2:需要用户名但不必唯一；1:需要唯一用户名
    private int new_account_policy;
    private int auth_life_time;
    private int authorization_policy;
    private int account_state;
    private String app_account_name;
    private String app_account_id;
    private int app_authorization_state;
    private java.sql.Timestamp enrolled_time;
    private java.sql.Timestamp app_authorized_at;
    private String authorization_token;
    private int client_type;
    private int otp_alg; // 0: no; 1: Google; 2: custom TOTP; 3: GM SM3
    private int use_cert; // 0 : not use cert, 1: use cert
    private int cert_state; // 0 : not ok, 1: ok
    private int cert_type;
}

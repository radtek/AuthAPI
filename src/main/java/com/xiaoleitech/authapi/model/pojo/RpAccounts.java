package com.xiaoleitech.authapi.model.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RpAccounts {
    private int rpaccount_id;
    private int rp_id;
    private int user_id;
    private String protect_methods;
    private String rp_account_name;
    private String rp_account_uuid;
    private int state;
    private int authred;
    private java.sql.Timestamp authr_at;
    private String authorization_token;
    private String otp_seed;
    private String cert_key;
    private String cert;
    private int cert_state;
    private java.sql.Timestamp created_at;
    private java.sql.Timestamp updated_at;
    private String sdk_auth_key;
    private String sdk_verify_token;
    private java.sql.Timestamp auth_at;
}

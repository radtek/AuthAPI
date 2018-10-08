package com.xiaoleitech.authapi.model.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class OtpAuthHistories {
    private int id;
    private int rp_id;
    private int user_id;
    private int rp_account_id;
    private String rp_account_name;
    private String otp;
    private java.sql.Timestamp auth_at;
}

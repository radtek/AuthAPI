package com.xiaoleitech.authapi.model.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AccountAuthHistories {
    private int id;
    private int user_id;
    private int rp_id;
    private int protect_method;
    private String auth_ip;
    private double auth_latitude;
    private double auth_longitude;
    private java.sql.Timestamp auth_at;
    private java.sql.Timestamp created_at;
    private java.sql.Timestamp updated_at;
}

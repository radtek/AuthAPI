package com.xiaoleitech.authapi.rpmanage.bean;

import lombok.Data;

@Data
public class AuthHistoryRecord {
    private String auth_ip;
    private double auth_latitude;
    private double auth_longitude;
    private java.sql.Timestamp auth_at;
    private int verify_method;
}

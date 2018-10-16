package com.xiaoleitech.authapi.dao.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Devices {
    private int id;
    private String device_uuid;
    private String imei;
    private int state;
    private String protect_method_capability;
    private String device_model;
    private int device_tee;
    private int device_se;
    private int device_type;
    private String device_token;
    private java.sql.Timestamp created_at;
    private java.sql.Timestamp updated_at;
}

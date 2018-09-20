package com.xiaoleitech.authapi.model.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Users {
    private int user_id;
    private int device_id;   // 设备的UUID
    private String user_uuid;
    private String real_name;
    private String phone_no;
    private int sex;
    private java.sql.Timestamp birthday;
    private String hukou_address;
    private String real_address;
    private String id_no;
    private java.sql.Timestamp id_expire_at;
    private String protect_methods;
    private String auth_key;
    private String password;
    private String password_salt;
    private int second_factor_attempt_fail_count;
    private int password_attempt_fail_count;
    private java.sql.Timestamp second_factor_lock_to;
    private java.sql.Timestamp password_lock_to;
    private int authenticated;
    private int protect_method;
    private double auth_latitude;
    private double auth_longitude;
    private java.sql.Timestamp auth_at;
    private String verify_token;
    private int face_enrolled;
    private int user_state;
    private java.sql.Timestamp created_at;
    private java.sql.Timestamp updated_at;
}

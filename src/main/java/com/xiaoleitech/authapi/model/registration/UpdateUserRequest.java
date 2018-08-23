package com.xiaoleitech.authapi.model.registration;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String user_id;
    private String verify_token;
    private String password;
    private String user_realname;
    private String id_no;
    private java.sql.Timestamp id_expire_at;
    private String protect_methods;
    private int face_enrolled;
}

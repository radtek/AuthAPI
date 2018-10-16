package com.xiaoleitech.authapi.dao.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AppUsers {
    private int rp_id;
    private String rp_uuid;
    private String rp_name;
    private int user_id;
    private String user_uuid;
    private String user_name;
    private int device_id;
    private String device_uuid;
    private int account_id;
    private String account_uuid;
    private String account_name;
    private int account_state;
}

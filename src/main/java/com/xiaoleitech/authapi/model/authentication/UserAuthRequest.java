package com.xiaoleitech.authapi.model.authentication;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserAuthRequest {
    private String app_id;
    private String user_id;
    private int protect_method;
    private double latitude;
    private double longitude;
    private String response;
}

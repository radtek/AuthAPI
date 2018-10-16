package com.xiaoleitech.authapi.enquiry.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class UserInfoResponse extends AuthAPIResponse {
    private String user_realname;
    private String phone_no;
    private String protect_methods;
    private String real_address;
    private String id_no;
    private int face_enrolled;
}

package com.xiaoleitech.authapi.enquiry.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class UserExistResponse extends AuthAPIResponse {
    private String user_id;
    private String account_name;
    private String id_no;
    private String user_realname;
}

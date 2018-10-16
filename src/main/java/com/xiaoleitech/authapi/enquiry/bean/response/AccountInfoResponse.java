package com.xiaoleitech.authapi.enquiry.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountInfoResponse extends AuthAPIResponse {
    private String account_id;
    private String account_name;
    private int authorized;
    private java.sql.Timestamp authorized_at;
}

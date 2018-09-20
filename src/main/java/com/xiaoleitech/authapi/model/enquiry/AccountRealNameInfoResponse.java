package com.xiaoleitech.authapi.model.enquiry;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountRealNameInfoResponse extends AuthAPIResponse {
    private String user_realname;
    private String phone_no;
    private String id_no;
    private java.sql.Timestamp id_expire_at;
}

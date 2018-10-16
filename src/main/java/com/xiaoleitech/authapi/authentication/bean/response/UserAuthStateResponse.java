package com.xiaoleitech.authapi.authentication.bean.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAuthStateResponse  extends AuthAPIResponse {
//    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp expire_at;
}

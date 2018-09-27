package com.xiaoleitech.authapi.model.authentication;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAuthStateResponse  extends AuthAPIResponse {
    private java.sql.Timestamp expire_at;
}
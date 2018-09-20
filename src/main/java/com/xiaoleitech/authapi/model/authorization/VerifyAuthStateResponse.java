package com.xiaoleitech.authapi.model.authorization;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class VerifyAuthStateResponse extends AuthAPIResponse {
    private int auth_life_time;
    private java.sql.Timestamp expire_at;
}

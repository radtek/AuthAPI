package com.xiaoleitech.authapi.model.enrollment;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class CheckAccountStateResponse extends AuthAPIResponse {
    private int account_state;
}

package com.xiaoleitech.authapi.model.enquiry;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAuthConfigResponse extends AuthAPIResponse {
    private String protect_methods;
    private int face_enrolled;
}

package com.xiaoleitech.authapi.model.rpmanage;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class CreateRelyPartResponse extends AuthAPIResponse {
    private String app_id;
    private String app_key;
}

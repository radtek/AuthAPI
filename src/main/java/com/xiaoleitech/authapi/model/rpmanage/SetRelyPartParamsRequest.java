package com.xiaoleitech.authapi.model.rpmanage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class SetRelyPartParamsRequest extends RelyPartParams{
    private String app_id;
    private String token;
}
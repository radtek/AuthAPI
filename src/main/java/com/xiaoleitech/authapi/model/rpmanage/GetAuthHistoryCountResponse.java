package com.xiaoleitech.authapi.model.rpmanage;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class GetAuthHistoryCountResponse extends AuthAPIResponse {
    private int count;
}

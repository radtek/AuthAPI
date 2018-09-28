package com.xiaoleitech.authapi.model.platform;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class CloudInfoResponse extends AuthAPIResponse {
    private String version;
    private String supported_sdk_version;
    private String cloud_name;
}

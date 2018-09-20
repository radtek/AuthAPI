package com.xiaoleitech.authapi.model.enquiry;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.pojo.EnrollUserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class UserEnrollmentInfoResponse extends AuthAPIResponse {
    private List<EnrollUserInfo> app;
}

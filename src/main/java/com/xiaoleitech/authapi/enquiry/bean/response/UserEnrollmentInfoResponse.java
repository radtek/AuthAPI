package com.xiaoleitech.authapi.enquiry.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.dao.pojo.EnrollUserInfo;
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

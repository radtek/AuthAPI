package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enrollment.EnrollAppRequest;
import com.xiaoleitech.authapi.model.enrollment.EnrollAppResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class EnrollmentServiceImpl implements EnrollmentService {
    private final SystemErrorResponse systemErrorResponse;
    private final EnrollAppResponse enrollAppResponse;

    @Autowired
    public EnrollmentServiceImpl(SystemErrorResponse systemErrorResponse, EnrollAppResponse enrollAppResponse) {
        this.systemErrorResponse = systemErrorResponse;
        this.enrollAppResponse = enrollAppResponse;
    }

    @Override
    public AuthAPIResponse enrollApp(EnrollAppRequest enrollAppRequest, BindingResult bindingResult) {

        // TODO: 没有实现。缺APP表
        systemErrorResponse.fillErrorResponse(enrollAppResponse, ErrorCodeEnum.ERROR_NOT_IMPLEMENTED);
        return enrollAppResponse;
    }
}

package com.xiaoleitech.authapi.enrollment.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.enrollment.bean.request.EnrollAppRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public interface EnrollmentService {

    AuthAPIResponse enrollApp(EnrollAppRequest enrollAppRequest, BindingResult bindingResult);

}

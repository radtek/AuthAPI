package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enrollment.EnrollAppRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public interface EnrollmentService {

    AuthAPIResponse enrollApp(EnrollAppRequest enrollAppRequest, BindingResult bindingResult);

}

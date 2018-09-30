package com.xiaoleitech.authapi.service.rpmanage;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.rpmanage.CreateRelyPartRequest;
import com.xiaoleitech.authapi.model.rpmanage.SetRelyPartParamsRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public interface RelyPartManageService {

    AuthAPIResponse createRelyPart(CreateRelyPartRequest createRelyPartRequest, BindingResult bindingResult);

    AuthAPIResponse setRelyPartParams(SetRelyPartParamsRequest setRelyPartParamsRequest, BindingResult bindingResult);

    AuthAPIResponse getRelyPartParams(String appUuid, String token);

    AuthAPIResponse resetRelyPartAppKey(String appUuid, String token);
}

package com.xiaoleitech.authapi.rpmanage.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.rpmanage.bean.request.CreateRelyPartRequest;
import com.xiaoleitech.authapi.rpmanage.bean.request.SetRelyPartParamsRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public interface RelyPartManageService {

    AuthAPIResponse createRelyPart(CreateRelyPartRequest createRelyPartRequest, BindingResult bindingResult);

    AuthAPIResponse setRelyPartParams(SetRelyPartParamsRequest setRelyPartParamsRequest, BindingResult bindingResult);

    AuthAPIResponse getRelyPartParams(String appUuid, String token);

    AuthAPIResponse resetRelyPartAppKey(String appUuid, String token);
}

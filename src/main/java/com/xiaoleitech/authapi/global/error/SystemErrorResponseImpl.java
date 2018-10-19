package com.xiaoleitech.authapi.global.error;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Component
public class SystemErrorResponseImpl implements SystemErrorResponse {
    @Override
    public ErrorCodeEnum checkRequestParams(BindingResult bindingResult, AuthAPIResponse authAPIResponse) {
        // Check if there are any errors
        if (!bindingResult.hasErrors())
            return ErrorCodeEnum.ERROR_OK;

        // Check if there are any field errors
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (fieldErrors.size() == 0)
            return ErrorCodeEnum.ERROR_OK;

        // Prepare the params-validation results
        StringBuilder errorMsg = new StringBuilder();
        FieldError fieldError;
        for (FieldError fieldError1 : fieldErrors) {
            fieldError = fieldError1;
            errorMsg.append(fieldError.getDefaultMessage());
            errorMsg.append(" ");
        }

        // Compose the error message and params-validation result. Fill the error code.
        authAPIResponse.setError_code(ErrorCodeEnum.ERROR_PARAMETER.getCode());
        errorMsg.insert(0, ErrorCodeEnum.ERROR_PARAMETER.getMsg() + ": ");
        authAPIResponse.setError_message(errorMsg.toString());

        // Return the error code.
        return ErrorCodeEnum.ERROR_PARAMETER;
    }

    @Override
    public void fill(AuthAPIResponse authAPIResponse, ErrorCodeEnum errorCodeEnum) {
        authAPIResponse.setError_code(errorCodeEnum.getCode());
        authAPIResponse.setError_message(errorCodeEnum.getMsg());
    }

    @Override
    public AuthAPIResponse response(ErrorCodeEnum errorCode) {
        AuthAPIResponse authAPIResponse = new AuthAPIResponse();
        authAPIResponse.setError_code(errorCode.getCode());
        authAPIResponse.setError_message(errorCode.getMsg());

        return authAPIResponse;
    }

    @Override
    public AuthAPIResponse success() {
        return response(ErrorCodeEnum.ERROR_OK);
    }

    @Override
    public AuthAPIResponse notImplemented() {
        return response(ErrorCodeEnum.ERROR_NOT_IMPLEMENTED);
    }

    @Override
    public AuthAPIResponse needParameters() {
        return response(ErrorCodeEnum.ERROR_NEED_PARAMETER);
    }
}

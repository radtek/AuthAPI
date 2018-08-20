package com.xiaoleitech.authapi.service.exception;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
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

        // Return the errro code.
        return ErrorCodeEnum.ERROR_PARAMETER;
    }

    @Override
    public void fillErrorResponse(AuthAPIResponse authAPIResponse, ErrorCodeEnum errorCodeEnum) {
        authAPIResponse.setError_code(errorCodeEnum.getCode());
        authAPIResponse.setError_message(errorCodeEnum.getMsg());
    }
}

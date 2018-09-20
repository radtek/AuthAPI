package com.xiaoleitech.authapi.service.exception;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import org.springframework.validation.BindingResult;

public interface SystemErrorResponse {
    /** 检查请求参数的数据绑定结果，并将错误信息填入响应结果里，同时返回错误码
     * @param bindingResult   Has the parameters validation result
     * @param authAPIResponse Fill the system ( AuthAPI ) error info (code & message)
     * @return ErrorCodeEnum. e.g., ERROR_OK means SUCCESS, without any errors.
     * @author ytwei
     */
    ErrorCodeEnum checkRequestParams(BindingResult bindingResult, AuthAPIResponse authAPIResponse);

    /** 按错误码将错误信息填入响应结果里。
     * @param authAPIResponse Fill the system error info (code & message)
     * @param errorCodeEnum   Error code
     */
    void fillErrorResponse(AuthAPIResponse authAPIResponse, ErrorCodeEnum errorCodeEnum);

    /** 创建一个AuthApiResponse对象，并填充传入参数 errorCode 的 code 和 message
     *
     * @param errorCode: 错误码
     * @return authApiResponse: AuthApiResponse 对象
     */
    AuthAPIResponse getGeneralResponse(ErrorCodeEnum errorCode);

    /** 返回一个HTTP成功(200)的响应结果
     *
     * @return HTTP成功的代码和信息，打包在 AuthAPIResponse 对象中返回
     */
    AuthAPIResponse getSuccessResponse();
}

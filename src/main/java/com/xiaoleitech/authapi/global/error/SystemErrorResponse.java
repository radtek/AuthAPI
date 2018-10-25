package com.xiaoleitech.authapi.global.error;

import com.alibaba.fastjson.JSONObject;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import org.springframework.validation.BindingResult;

public interface SystemErrorResponse {
    /**
     * 检查请求参数的数据绑定结果，并将错误信息填入响应结果里，同时返回错误码
     *
     * @param bindingResult   Has the parameters validation result
     * @param authAPIResponse Fill the system ( AuthAPI ) error info (code & message)
     * @return ErrorCodeEnum. e.g., ERROR_OK means SUCCESS, without any errors.
     * @author ytwei
     */
    ErrorCodeEnum checkRequestParams(BindingResult bindingResult, AuthAPIResponse authAPIResponse);

    /**
     * 按错误码将错误信息填入响应结果里。
     *
     * @param authAPIResponse Fill the system error info (code & message)
     * @param errorCodeEnum   Error code
     */
    void fill(AuthAPIResponse authAPIResponse, ErrorCodeEnum errorCodeEnum);

    /**
     * 创建一个AuthApiResponse对象，并填充传入参数 errorCode 的 code 和 message
     *
     * @param errorCode: 错误码
     * @return authApiResponse: AuthApiResponse 对象
     */
    AuthAPIResponse response(ErrorCodeEnum errorCode);

    /**
     * 创建一个AuthApiResponse对象，并填充传入参数 jsonObject 的 code 和 message
     * @param jsonObject 含错误码和错误信息的JSON对象
     * @return AuthApiResponse 对象
     */
    AuthAPIResponse response(JSONObject jsonObject);

    /**
     * 将 errorCode 转换为JSON对象返回给调用者
     * @param errorCode  错误码
     * @return  jsonObject 含错误码和错误信息的JSON对象
     */
    public static JSONObject getJSON(ErrorCodeEnum errorCode){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error_code", errorCode.getCode());
        jsonObject.put("error_message", errorCode.getMsg());
        return jsonObject;
    }

    /**
     * 返回一个成功(0)的响应结果
     *
     * @return 成功的代码和信息，打包在 AuthAPIResponse 对象中返回
     */
    AuthAPIResponse success();

    /**
     * 返回一个未实现的响应结果
     *
     * @return ERROR_NOT_IMPLEMENTED
     */
    AuthAPIResponse notImplemented();

    /**
     * 返回一个缺少参数的响应结果
     *
     * @return ERROR_NEED_PARAMETER
     */
    AuthAPIResponse needParameters();

    /**
     * 返回一个用户找不到的响应结果
     *
     * @return ERROR_USER_NOT_FOUND
     */
    AuthAPIResponse userNotFound();

    /**
     * 返回一个无效令牌的响应结果
     *
     * @return ERROR_INVALID_TOKEN
     */
    AuthAPIResponse invalidToken();

    AuthAPIResponse appNotFound();
}

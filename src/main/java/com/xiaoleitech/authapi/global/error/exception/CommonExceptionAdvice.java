package com.xiaoleitech.authapi.global.error.exception;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@ResponseBody
public class CommonExceptionAdvice {
    private static Logger logger = LoggerFactory.getLogger(CommonExceptionAdvice.class);
    private final SystemErrorResponse systemErrorResponse;

    public CommonExceptionAdvice(SystemErrorResponse systemErrorResponse) {
        this.systemErrorResponse = systemErrorResponse;
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public AuthAPIResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        logger.error("缺少请求参数: " + e.getParameterName());
        AuthAPIResponse authAPIResponse = new AuthAPIResponse();
        authAPIResponse.setError_code(ErrorCodeEnum.ERROR_NEED_PARAMETER.getCode());
        String errorMsg = ErrorCodeEnum.ERROR_NEED_PARAMETER.getMsg();
        errorMsg += ": " + e.getParameterName();
        authAPIResponse.setError_message(errorMsg);
        return authAPIResponse;
    }

    /** 必须在 application.properties 中设置
     * spring.mvc.throw-exception-if-no-handler-found=true
     * 404 - Not Found
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
//    @ExceptionHandler
    public AuthAPIResponse noHandlerFoundException(NoHandlerFoundException e) {
        logger.error("接口不存在: " + e.getMessage());
        AuthAPIResponse authAPIResponse = new AuthAPIResponse();
        authAPIResponse.setError_code(ErrorCodeEnum.ERROR_INTERFACE_NOT_FOUND.getCode());
        String errorMsg = ErrorCodeEnum.ERROR_INTERFACE_NOT_FOUND.getMsg();
        errorMsg += ": " + e.getRequestURL();
        authAPIResponse.setError_message(errorMsg);
        return authAPIResponse;
    }


    /**
     * 500 - Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException .class)
    public AuthAPIResponse handleServiceException(RuntimeException  e) {
        if (e instanceof NullPointerException) {
            logger.error("系统发生异常: 空指针");
            StackTraceElement element = e.getStackTrace()[0];
            String errorInfo = "类名: " + element.getClassName() +
                    "\t文件: " + element.getFileName() +
                    "\t方法: " + element.getMethodName() +
                    "\t行数: " + element.getLineNumber();
            logger.error(errorInfo);
        }
        return systemErrorResponse.response(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
    }
}

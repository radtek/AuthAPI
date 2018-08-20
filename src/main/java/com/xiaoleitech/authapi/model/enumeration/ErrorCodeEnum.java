package com.xiaoleitech.authapi.model.enumeration;

public enum ErrorCodeEnum {
    ERROR_OK(0, "OK"),
    ERROR_HTTP_SUCCESS(200, "OK"),
    ERROR_INTERNAL_ERROR(998, "内部错误"),
    ERROR_NOT_IMPLEMENTED(999, "待实现"),
    ERROR_GENERAL_ERROR(1001, "未知错误"),
    ERROR_INVALID_AUTHGATE(1002, "非法的认证网关"),
    ERROR_PARAMETER(1003, "参数错误"),
    ERROR_INVALID_DEVICE(1004, "设备错误"),
    ERROR_INVALID_USER(1005, "用户错误"),
    ERROR_INVALID_APP(1006, "应用错误"),
    ERROR_DEVICE_NOT_FOUND(1007, "设备未找到"),
    ERROR_USER_NOT_FOUND(1008, "用户未找到"),
    ERROR_APP_NOT_FOUND(1009, "应用未找到"),
    ERROR_USER_INACTIVE(1010, "用户未激活"),
    ERROR_DEVICE_INACTIVE(1011, "设备未激活"),
    ERROR_APP_INACTIVE(1012, "应用未激活"),
    ERROR_USER_NOT_ENROLLED(1013, "用户未绑定"),
    ERROR_AUTH_FAILED(1014, "认证失败"),
    ERROR_INVALID_PASSWORD(1015, "口令错误"),
    ERROR_INVALID_OTP(1016, "OTP错误"),
    ERROR_PUSH_NOT_REACH(1017, "PUSH未成功"),
    ERROR_USER_REJECTED(1018, "用户拒绝授权"),
    ERROR_INVALID_USER_CERT(1019, "非法用户证书"),
    ERROR_INVALID_DEVICE_CERT(1020, "非法设备证书"),
    ERROR_USERNAME_USED(1021, "该用户名已被注册"),
    ERROR_DEVICE_NOT_SUPPORT(1022, "不支持该设备"),
    ERROR_NEED_PARAMETER(1023, "参数不足"),
    ERROR_NO_DEVICE(1024, "没有对应的设备"),
    ERROR_CHALLENGE_EXPIRED(1025, "挑战码失效"),
    ERROR_INVALID_TOKEN(1026, "错误的TOKEN"),
    ERROR_ALREADY_ENROLLED(1027, "已经绑定"),
    ERROR_USER_REGISTERED(1028, "用户已注册"),
    ERROR_DEVICE_REGISTERED(1029, "设备已注册"),
    ERROR_USER_NOT_REACHED(1030, "用户未打开客户端"),
    ERROR_USER_NOT_ACTIVATED(1031, "用户未激活"),
    ERROR_CANNOT_ENROLL(1032, "无法绑定"),
    ERROR_USER_PASSWORD_LOCKED(1033, "用户口令已锁定"),
    ERROR_USER_2FA_LOCKED(1034, "用户生物识别已锁定"),
    ERROR_TOKEN_EXPIRED(1035, "TOKEN已失效"),
    ERROR_INVALID_VERIFY_CODE(1036, "错误的验证码"),
    ERROR_USER_NOT_AUTHENTICATED(1037, "用户未登录"),
    ERROR_DECRYPT_FAILED(1038, "解密失败"),
    ERROR_NEED_REALNAMEINFO(1039, "实名信息不足"),
    ERROR_INVALID_ACCOUNT(1040, "错误的账号"),
    ERROR_ACCOUNT_NOT_AUTHED(1041, "用户未授权"),
    ERROR_REQUEST_TOO_OFTEN(1042, "请求过于频繁"),
    ERROR_INVALID_PICTURE(1043, "错误的图片"),
    ERROR_PICTURE_NOT_MATCH(1044, "图片数据和实名信息不符合"),
    ERROR_EXPIRED_PICTURE(1045, "证件已过期"),
    ERROR_CERTIFICATE_NO_CERTREQ(1046, "没有产生证书请求"),
    ERROR_CERTIFICATE_CERTREQ_NOT_SIGN(1047, "证书请求尚未签名"),
    ERROR_CERTIFICATE_PENDING(1048, "证书申请已发送"),
    ERROR_SIGNATURE_VERIFY_FAILED(1049, "验证签名失败"),
    ERROR_CERTIFICATE_PARSE_FAILED(1050, "证书解析失败"),
    ERROR_CERTIFICATE_NOT_FOUND(1051, "未找到证书"),
    ERROR_CERTIFICATE_NEED_REVOKE(1052, "需要先撤销证书"),
    ERROR_TIME_OUT(1053, "超时"),
    ERROR_NEED_INFO(1054, "需要补充信息"),
    ERROR_WRONG_INFO(1055, "补充信息错误"),;

    private Integer code;
    private String msg;

    ErrorCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

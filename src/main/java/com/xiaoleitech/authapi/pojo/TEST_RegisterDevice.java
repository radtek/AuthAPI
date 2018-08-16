package com.xiaoleitech.authapi.pojo;

public class TEST_RegisterDevice {
    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(ErrorInfo errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    private ErrorInfo errorInfo;
    private String device_id;
}

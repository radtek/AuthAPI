package com.xiaoleitech.authapi.pojo;

public class IDPInfoResponse {
    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSupported_ios_app_version() {
        return supported_ios_app_version;
    }

    public void setSupported_ios_app_version(String supported_ios_app_version) {
        this.supported_ios_app_version = supported_ios_app_version;
    }

    public String getSupported_android_app_version() {
        return supported_android_app_version;
    }

    public void setSupported_android_app_version(String supported_android_app_version) {
        this.supported_android_app_version = supported_android_app_version;
    }

    private int error_code;
    private String error_message;
    private String version;
    private String supported_ios_app_version;
    private String supported_android_app_version;
}

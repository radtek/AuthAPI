package com.xiaoleitech.authapi.pojo;

public class PlatformSettingResponse {
    private int error_code;
    private String error_message;
    private String platform_name;
    private String platform_logo_url;
    // 0: not use ssl, 1: RSA2048/SHA256, 2: SM2/SM3
    private int use_ssl;

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

    public String getPlatform_name() {
        return platform_name;
    }

    public void setPlatform_name(String platform_name) {
        this.platform_name = platform_name;
    }

    public String getPlatform_logo_url() {
        return platform_logo_url;
    }

    public void setPlatform_logo_url(String platform_logo_url) {
        this.platform_logo_url = platform_logo_url;
    }

    public int getUse_ssl() {
        return use_ssl;
    }

    public void setUse_ssl(int use_ssl) {
        this.use_ssl = use_ssl;
    }
}

package com.xiaoleitech.authapi.model.bean;

public class PlatformSettingResponse extends AuthAPIResponse {
    private String platform_name;
    private String platform_logo_url;
    // 0: not use ssl, 1: RSA2048/SHA256, 2: SM2/SM3
    private int use_ssl;

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

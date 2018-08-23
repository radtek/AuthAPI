package com.xiaoleitech.authapi.model.check;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public class IDPInfoResponse extends AuthAPIResponse {
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

    private String version;
    private String supported_ios_app_version;
    private String supported_android_app_version;
}

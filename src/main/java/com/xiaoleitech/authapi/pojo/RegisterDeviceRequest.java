package com.xiaoleitech.authapi.pojo;

public class RegisterDeviceRequest {
    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getProtect_method_capability() {
        return protect_method_capability;
    }

    public void setProtect_method_capability(String protect_method_capability) {
        this.protect_method_capability = protect_method_capability;
    }

    public int getDevice_type() {
        return device_type;
    }

    public void setDevice_type(int device_type) {
        this.device_type = device_type;
    }

    public int getDevice_tee() {
        return device_tee;
    }

    public void setDevice_tee(int device_tee) {
        this.device_tee = device_tee;
    }

    public int getDevice_se() {
        return device_se;
    }

    public void setDevice_se(int device_se) {
        this.device_se = device_se;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    private String imei;
    private String protect_method_capability;
    private int device_type;
    private int device_tee;
    private int device_se;
    private String device_token;
}

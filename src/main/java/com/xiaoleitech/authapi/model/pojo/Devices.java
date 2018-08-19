package com.xiaoleitech.authapi.model.pojo;

import org.springframework.stereotype.Component;

import java.sql.Time;

@Component
public class Devices {
    private String device_uuid;
    private String imei;
    private int state;
    private String protect_method_capability;
    private String device_model;
    private int device_tee;
    private int device_se;
    private int device_type;
    private String device_token;
    private java.sql.Time created_at;
    private java.sql.Time updated_at;

    public String getDevice_uuid() {
        return device_uuid;
    }

    public void setDevice_uuid(String device_uuid) {
        this.device_uuid = device_uuid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getProtect_method_capability() {
        return protect_method_capability;
    }

    public void setProtect_method_capability(String protect_method_capability) {
        this.protect_method_capability = protect_method_capability;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
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

    public int getDevice_type() {
        return device_type;
    }

    public void setDevice_type(int device_type) {
        this.device_type = device_type;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public Time getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Time created_at) {
        this.created_at = created_at;
    }

    public Time getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Time updated_at) {
        this.updated_at = updated_at;
    }
}

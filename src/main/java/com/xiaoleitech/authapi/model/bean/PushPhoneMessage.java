package com.xiaoleitech.authapi.model.bean;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class PushPhoneMessage {
    private int message_type;
    private String alert;
    private String app_id;
    private String nonce;
    private String ip;
    private String address;
}

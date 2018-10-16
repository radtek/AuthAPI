package com.xiaoleitech.authapi.global.cipher.otp;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class OtpParams {
    private String owner;      // 用于缓存中存放的标记，比如redis的键(key)
    private String otp_seed;
    private int strong;
    private int otp_alg;
    private int inteval;
    private int otp_digits;
    private int otp_c;
    private String otp_q;
}

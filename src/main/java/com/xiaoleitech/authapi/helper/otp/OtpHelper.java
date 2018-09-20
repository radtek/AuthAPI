package com.xiaoleitech.authapi.helper.otp;

import com.xiaoleitech.authapi.helper.UtilsHelper;
import com.xiaoleitech.authapi.helper.cache.RedisService;
import com.xiaoleitech.authapi.model.bean.OtpParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OtpHelper {
    static private int otpValidPeriod = -1; // OTP有效时间段，单位秒。比如60秒，300秒，600秒
    private final RedisService redisService;
    private final TOtpProvider tOtpProvider;
    private static final String CRYPTO = "HmacSHA1";

    @Autowired
    public OtpHelper(RedisService redisService, TOtpProvider tOtpProvider) {
        this.redisService = redisService;
        this.tOtpProvider = tOtpProvider;

        initOtpValidPeriod();
    }

    private int getOtpValidPeriod() { return otpValidPeriod; }

    private void initOtpValidPeriod() {
        if (otpValidPeriod == -1)
            otpValidPeriod = 60;
    }

    public String getOtpCachedKey(String owner) {
        return "OTP_" + owner;
    }

    /**
     * 根据输入的OTP参数，生成OTP，并存放到缓存中（指定生存时间）
     * @param otpParams OTP计算所需的各种参数，以及所有者 ( owner )
     * @return OTP字符串
     */
    public String generateOtp(OtpParams otpParams) {
        tOtpProvider.initialize(otpParams.getOtp_alg(), 5,
                otpParams.getOtp_digits(), otpParams.getInteval());
        String otp = tOtpProvider.getCode(otpParams.getOtp_seed(), System.currentTimeMillis());
        return otp;
//        String otp = "111222";
//        String key = getOtpCachedKey(otpParams.getOwner());
//        redisService.setValueForSeconds(key, otp, getOtpValidPeriod());
    }

    /**
     * 校验OTP，检查缓存中的OTP是否还存活，并检查是否和输入的OTP一致
     * @param otpParams OTP计算所需的各种参数，以及所有者 ( owner )
     * @param otp 待检查的OTP
     * @return true -- OTP有效且正确；false -- OTP校验失败
     */
    public boolean checkOtp(OtpParams otpParams, String otp) {
        tOtpProvider.initialize(otpParams.getOtp_alg(), 5,
                otpParams.getOtp_digits(), otpParams.getInteval());

        String seed = otpParams.getOtp_seed();
        int otpDigits = Integer.parseInt(otp);
        return tOtpProvider.checkCode(otpParams.getOtp_seed(), Integer.parseInt(otp));
    }
}

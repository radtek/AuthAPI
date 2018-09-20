package com.xiaoleitech.authapi.helper.otp;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

@Component
public class TOtpProvider {
    private int algorithm = 2;
    private int window = 2;
    private int otpCodeLength = 6;
    private int interval = 60;
    private int strongLevel = 3;
    private String otpQ = "";
    private int otpC = 0;


    private static String CRYPTO = "HmacSHA1";

    private static final int[] DIGITS_POWER
            // 0 1 2 3 4 5 6 7 8
            = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000 };

    public TOtpProvider() {
    }

    public TOtpProvider(int algorithm,
                        int window,
                        int otpCodeLength,
                        int interval) {
        initialize(algorithm, window, otpCodeLength, interval);
    }

    public void initialize(int algorithm,
                           int window,
                           int otpCodeLength,
                           int interval) {
        this.algorithm = algorithm;
        this.window = window;
        this.otpCodeLength = otpCodeLength;
        this.interval = interval;
    }

    public TOtpProvider(int algorithm,
                        int window,
                        int otpCodeLength,
                        int interval,
                        int strongLevel,
                        String otpQ,
                        int otpC) {
        initialize(algorithm, window, otpCodeLength, interval, strongLevel, otpQ, otpC);
    }

    public void initialize(int algorithm,
                           int window,
                           int otpCodeLength,
                           int interval,
                           int strongLevel,
                           String otpQ,
                           int otpC) {
        this.algorithm = algorithm;
        this.window = window;
        this.otpCodeLength = otpCodeLength;
        this.interval = interval;
        this.strongLevel = strongLevel;
        this.otpQ = otpQ;
        this.otpC = otpC;
    }

    /**
     * This method uses the JCE to provide the crypto algorithm. HMAC computes a
     * Hashed Message Authentication Code with the crypto hash algorithm as a
     * parameter.
     *
     * @param crypto
     *            : the crypto algorithm (HmacSHA1, HmacSHA256, HmacSHA512)
     * @param keyBytes
     *            : the bytes to use for the HMAC key
     * @param text
     *            : the message or text to be authenticated
     */

    private byte[] hmacSha(String crypto, byte[] keyBytes, byte[] text) {
        try {
            Mac hmac;
            hmac = Mac.getInstance(crypto);
            SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }

    private int generateTOTP(byte[] key, long time, int digits, String crypto) {

        byte[] msg = ByteBuffer.allocate(8).putLong(time).array();
        byte[] hash = hmacSha(crypto, key, msg);

        // put selected bytes into result int
        int offset = hash[hash.length - 1] & 0xf;

        int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16)
                | ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);

        int otp = binary % DIGITS_POWER[digits];
        return otp;
    }

    private String getCode(byte[] secrect, int codeLength, long time) {
        long hash = generateTOTP(secrect, time, codeLength, CRYPTO);
        StringBuilder code = new StringBuilder("" + hash);
        while (code.length() < codeLength) {
            code.insert(0, "0");
        }

        return code.toString();
    }
    public String getCode(String secret, long timeInput){
        byte[] decodedKey = secret.getBytes();
        long time;
        if (timeInput <= 0) { // if time is 0, then get current time
            time = getCurrentTimePoint();
        } else {
            time = timeInput / (1000 * interval);
        }

        return getCode(decodedKey, otpCodeLength, time);
    }

    public boolean checkCode(String secret, long code) {
        // Base32 codec = new Base32();
        // byte[] decodedKey = codec.decode(secret);
        byte[] decodedKey = secret.getBytes();

        // Window is used to check codes generated in the near past.
        // You can use this value to tune how far you're willing to go.
        long currentPoint = getCurrentTimePoint();

        for (int i = -window; i <= window; ++i) {
            long hash = generateTOTP(decodedKey, currentPoint + i, otpCodeLength, CRYPTO);

            if (hash == code) {
                return true;
            }
        }

        // The validation code is invalid.
        return false;
    }

    /**
     * 取当前时间节点。当前时间取秒数，再除以间隔时间段( interval )
     * @return 整型节点值
     */
    private long getCurrentTimePoint() {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        return currentTimeSeconds / interval;
    }


}

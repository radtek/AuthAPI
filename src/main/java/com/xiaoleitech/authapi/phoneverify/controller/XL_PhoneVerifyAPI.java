package com.xiaoleitech.authapi.phoneverify.controller;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.phoneverify.service.GetSmsCodeService;
import com.xiaoleitech.authapi.phoneverify.service.VerifySmsCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class XL_PhoneVerifyAPI {
    private final GetSmsCodeService getSmsCodeService;
    private final VerifySmsCodeService verifySmsCodeService;

    @Autowired
    public XL_PhoneVerifyAPI(GetSmsCodeService getSmsCodeService, VerifySmsCodeService verifySmsCodeService) {
        this.getSmsCodeService = getSmsCodeService;
        this.verifySmsCodeService = verifySmsCodeService;
    }

    /**
     * 发送手机验证码
     * get https://server/api/get_sms_code?phone_no=<phone_no>
     *
     * @param phoneNo 产生验证码，并发送到指定的手机号
     * @return {
     * error_code: errorcode,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/get_sms_code", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse getSmsCode(@RequestParam("phone_no") String phoneNo) {
        return getSmsCodeService.getSmsCode(phoneNo);
    }

    /**
     * 验证手机验证码
     * get https://server/api/verify_sms_code?phone_no=<phone_no>&verify_code=<verify_code>
     *
     * @param phoneNo 需验证的手机号
     * @param smsCode 需判定是否有效的验证码
     * @return return
     * {
     * error_code: errorcode,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/verify_sms_code", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse verifySmsCode(@RequestParam("phone_no") String phoneNo, @RequestParam("verify_code") String smsCode) {
        return verifySmsCodeService.verifySmsCode(phoneNo, smsCode);
    }
}

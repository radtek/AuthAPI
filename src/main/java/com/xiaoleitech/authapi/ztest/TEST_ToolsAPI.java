package com.xiaoleitech.authapi.ztest;

import com.alibaba.fastjson.JSONObject;
import com.xiaoleitech.authapi.auxiliary.authentication.AuthenticationHelper;
import com.xiaoleitech.authapi.auxiliary.entity.RelyPartHelper;
import com.xiaoleitech.authapi.global.msgqueue.activemq.MyActiveMqProducer;
import com.xiaoleitech.authapi.global.cipher.otp.OtpHelper;
import com.xiaoleitech.authapi.global.utils.UtilsHelper;
import com.xiaoleitech.authapi.auxiliary.authentication.ChallengeHelper;
import com.xiaoleitech.authapi.global.cache.redis.RedisService;
import com.xiaoleitech.authapi.global.cipher.base64.Base64Coding;
import com.xiaoleitech.authapi.global.cipher.hmac.MyHmacAlgorithm;
import com.xiaoleitech.authapi.global.cipher.symmetric.SymmetricAlgorithm;
import com.xiaoleitech.authapi.global.msgqueue.apollo.MqttGateway;
import com.xiaoleitech.authapi.global.phone.PushMessageHelper;
import com.xiaoleitech.authapi.global.phone.SmsHelper;
import com.xiaoleitech.authapi.dao.helper.DevicesTableHelper;
import com.xiaoleitech.authapi.dao.helper.RelyPartsTableHelper;
import com.xiaoleitech.authapi.dao.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.cipher.otp.OtpParams;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.Devices;
import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import com.xiaoleitech.authapi.dao.pojo.RpAccounts;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;

@Slf4j
@RestController
public class TEST_ToolsAPI {
    private final MqttGateway mqttGateway;
    private final PushMessageHelper pushMessageHelper;
    private final DevicesTableHelper devicesTableHelper;
    private final SystemErrorResponse systemErrorResponse;
    private final RedisService redisService;
    private final ChallengeHelper challengeHelper;
    private final SymmetricAlgorithm symmetricAlgorithm;
    private final OtpHelper otpHelper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final RelyPartHelper relyPartHelper;
    private final SmsHelper smsHelper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public TEST_ToolsAPI(SystemErrorResponse systemErrorResponse,
                         RedisService redisService,
                         ChallengeHelper challengeHelper,
                         SymmetricAlgorithm symmetricAlgorithm,
                         OtpHelper otpHelper,
                         MqttGateway mqttGateway,
                         PushMessageHelper pushMessageHelper,
                         DevicesTableHelper devicesTableHelper,
                         RpAccountsTableHelper rpAccountsTableHelper,
                         RelyPartsTableHelper relyPartsTableHelper,
                         RelyPartHelper relyPartHelper,
                         SmsHelper smsHelper, AuthenticationHelper authenticationHelper) {
        this.systemErrorResponse = systemErrorResponse;
        this.challengeHelper = challengeHelper;
        this.redisService = redisService;
        this.symmetricAlgorithm = symmetricAlgorithm;
        this.otpHelper = otpHelper;
        this.mqttGateway = mqttGateway;
        this.pushMessageHelper = pushMessageHelper;
        this.devicesTableHelper = devicesTableHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.relyPartHelper = relyPartHelper;
        this.smsHelper = smsHelper;
        this.authenticationHelper = authenticationHelper;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public class TestResponse extends AuthAPIResponse {
        String result;
    }

    @RequestMapping(value ="/test/send_sms_code", method = RequestMethod.GET)
    public @ResponseBody
    Object sendSmsCode(@RequestParam("phone_no")String phoneNo,
                       @RequestParam("verify_code")String verifyCode) {

        String smsCode = challengeHelper.generateSmsCode(phoneNo, verifyCode);
        ErrorCodeEnum errorCode = smsHelper.sendSmsVerifyCode(phoneNo, smsCode);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error_code", errorCode.getCode());
        jsonObject.put("error_message", errorCode.getMsg());
        jsonObject.put("sms_code", smsCode);
        return jsonObject;
    }

    @RequestMapping(value ="/test/show_sms_code", method = RequestMethod.GET)
    public @ResponseBody
    Object showSmsCode(@RequestParam("phone_no") String phoneNo) {
        TestResponse testResponse = new TestResponse();
        String smsCode = challengeHelper.getSmsCode(phoneNo);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error_code", ErrorCodeEnum.ERROR_OK.getCode());
        jsonObject.put("error_message", ErrorCodeEnum.ERROR_OK.getMsg());
        jsonObject.put("sms_code", smsCode);
        return jsonObject;
    }

    @RequestMapping(value = "/test/get_encrypted_password", method = RequestMethod.GET)
    public @ResponseBody
    Object getEncryptedPassword(@RequestParam("password") String password,
                                         @RequestParam("salt") String salt) {
        String result = authenticationHelper.getEncryptedPassword(password, salt);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error_code", ErrorCodeEnum.ERROR_OK.getCode());
        jsonObject.put("error_message", ErrorCodeEnum.ERROR_OK.getMsg());
        jsonObject.put("result", result);
        return jsonObject;
    }

    @RequestMapping(value ="/test/hmac_challenge", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse testHmac(@RequestParam("challenge") String challenge,
                             @RequestParam("password") String password,
                             @RequestParam("password_salt") String passwordSalt,
                             @RequestParam("auth_key") String authKey) {
        TestResponse testResponse = new TestResponse();

        String hmac = MyHmacAlgorithm.calculate(challenge, password, passwordSalt, authKey);
        testResponse.setResult(hmac);
        systemErrorResponse.fillErrorResponse(testResponse, ErrorCodeEnum.ERROR_OK);

        return testResponse;
    }

    @RequestMapping(value ="/test/aes_encrypt_nonce", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse testAesNonce(@RequestParam("nonce") String nonce,
                                 @RequestParam("auth_key") String authKey) {
        TestResponse testResponse = new TestResponse();

//        String cryptResult = symmetricAlgorithm.doAes(UtilsHelper.bytesToHexString(nonce.getBytes()), authKey, 256, Cipher.ENCRYPT_MODE);
//        byte [] bytesResult = UtilsHelper.hexStringToBytes(cryptResult);
        byte [] bytesResult = symmetricAlgorithm.doAes(nonce.getBytes(), authKey.getBytes(), 256, Cipher.ENCRYPT_MODE);
        String encodeResult = Base64Coding.encode(bytesResult);
        testResponse.setResult(encodeResult);
        systemErrorResponse.fillErrorResponse(testResponse, ErrorCodeEnum.ERROR_OK);

        return testResponse;
    }

    @RequestMapping(value ="/test/aes_decrypt_nonce", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse testAesDecrypt(@RequestParam("cipher_text") String cipherText,
                                   @RequestParam("auth_key") String authKey) {
        byte[] testBytes = Base64Coding.decode("L/ICLWRHRXBiMlft4DFZFw==");

        TestResponse testResponse = new TestResponse();
        String decodeText = Base64Coding.decodeToHexString(cipherText);
        String decryptResult = symmetricAlgorithm.doAes(decodeText, authKey, 256, Cipher.DECRYPT_MODE);

        byte[] bytesResult = UtilsHelper.hexStringToBytes(decryptResult);
        String stringResult = new String(bytesResult);

        testResponse.setResult(stringResult);
        systemErrorResponse.fillErrorResponse(testResponse, ErrorCodeEnum.ERROR_OK);

        return testResponse;
    }

    @RequestMapping(value ="/test/show_otp", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse showOtp(@RequestParam("account_uuid") String accountUuid,
                            @RequestParam("app_uuid") String appUuid) {
        TestResponse testResponse = new TestResponse();

        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpAccountUuid(accountUuid);
        OtpParams otpParams = new OtpParams();
        otpParams.setOwner(rpAccount.getRp_account_uuid());
        BeanUtils.copyProperties(relyPart, otpParams);
        otpParams.setOtp_seed(rpAccount.getOtp_seed());
        String otp = otpHelper.generateOtp(otpParams);

        testResponse.setResult(otp);
        systemErrorResponse.fillErrorResponse(testResponse, ErrorCodeEnum.ERROR_OK);

        return testResponse;
    }

    @RequestMapping("/test/sendMqtt.do")
    public String sendMqtt(String sendData, String topic) {
        mqttGateway.sendToMqtt(sendData, topic);
        return "OK";
    }

    @RequestMapping(value = "/test/pushPhoneMsg", method = RequestMethod.GET)
    public String pushPhoneMsg(@RequestParam("device_id") String deviceUuid,
                               @RequestParam("message")String message) {
        Devices device = devicesTableHelper.getDeviceByUuid(deviceUuid);
        if (device == null)
            return "Error";

        pushMessageHelper.sendMessage(device, message);
        return "OK";
    }

    @RequestMapping(value ="/test/get_token", method = RequestMethod.GET)
    public String getToken(@RequestParam("app_uuid")String rpUuid) {
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(rpUuid);
        String token = relyPartHelper.generateToken(relyPart);
        return token;
    }

    @Autowired private MyActiveMqProducer myActiveMqProducer;

    @RequestMapping(value = "/test/activemq_produce", method = RequestMethod.GET)
    public String activeMqProduce(@RequestParam("topic") String topic,
                                  @RequestParam("queue") String queue,
                                  @RequestParam("message") String message) {

        if ( (topic != null) && !topic.isEmpty()) {
            myActiveMqProducer.sendToTopic(topic, message);
        }

        if ( (queue != null) && !queue.isEmpty()) {
            myActiveMqProducer.sendToQueue(queue, message);
        }

        return ErrorCodeEnum.ERROR_OK.getCodeString();
    }

}

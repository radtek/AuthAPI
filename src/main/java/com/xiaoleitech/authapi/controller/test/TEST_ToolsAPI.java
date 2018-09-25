package com.xiaoleitech.authapi.controller.test;

import com.xiaoleitech.authapi.helper.RelyPartHelper;
import com.xiaoleitech.authapi.helper.msgqueue.MyActiveMqProducer;
import com.xiaoleitech.authapi.helper.otp.OtpHelper;
import com.xiaoleitech.authapi.helper.UtilsHelper;
import com.xiaoleitech.authapi.helper.authenticate.ChallengeHelper;
import com.xiaoleitech.authapi.helper.cache.RedisService;
import com.xiaoleitech.authapi.helper.cipher.Base64Coding;
import com.xiaoleitech.authapi.helper.cipher.MyHmacAlgorithm;
import com.xiaoleitech.authapi.helper.cipher.SymmetricAlgorithm;
import com.xiaoleitech.authapi.helper.msgqueue.MqttGateway;
import com.xiaoleitech.authapi.helper.msgqueue.PushMessageHelper;
import com.xiaoleitech.authapi.helper.sms.SmsHelper;
import com.xiaoleitech.authapi.helper.table.DevicesTableHelper;
import com.xiaoleitech.authapi.helper.table.RelyPartsTableHelper;
import com.xiaoleitech.authapi.helper.table.RpAccountsTableHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.bean.OtpParams;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.Devices;
import com.xiaoleitech.authapi.model.pojo.RelyParts;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
//import org.fusesource.stomp.jms.StompJmsConnectionFactory;
//import org.fusesource.stomp.jms.StompJmsDestination;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
//import org.fusesource.stomp.jms.StompJmsConnectionFactory;
//import org.fusesource.stomp.jms.StompChannel;
//import org.fusesource.hawtdispatch.transport.SslTransport;

//import javax.jms.*;

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
                         SmsHelper smsHelper) {
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
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public class TestResponse extends AuthAPIResponse {
        String result;
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

    @RequestMapping(value ="/test/hmac", method = RequestMethod.GET)
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

    @RequestMapping(value ="/test/smscode", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse testGetSmsCode(@RequestParam("phone_no") String phoneNo) {
        TestResponse testResponse = new TestResponse();
        String smsCode = challengeHelper.getSmsCode(phoneNo);
        testResponse.setResult(smsCode);
        systemErrorResponse.fillErrorResponse(testResponse, ErrorCodeEnum.ERROR_OK);
        return testResponse;
    }

    @RequestMapping(value ="/test/aes_nonce", method = RequestMethod.GET)
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

    @RequestMapping(value ="/test/aes_decrypt", method = RequestMethod.GET)
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

    @RequestMapping(value ="/test/get_token", method = RequestMethod.GET)
    public String getToken(@RequestParam("app_uuid")String rpUuid) {
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(rpUuid);
        String token = relyPartHelper.generateToken(relyPart);
        return token;
    }

    @RequestMapping(value ="/test/send_sms_code", method = RequestMethod.GET)
    public String sendSmsCode(@RequestParam("phone_no")String phoneNo,
                          @RequestParam("verify_code")String verifyCode) {

//        AuthAPIResponse authAPIResponse = new AuthAPIResponse();
//        systemErrorResponse.fillErrorResponse(authAPIResponse, ErrorCodeEnum.ERROR_OK);
//        JSONObject jsonObject = JSONObject.fromObject({"code":"12321"});
//        String message = jsonObject.toString();

        ErrorCodeEnum errorCode = smsHelper.sendSmsVerifyCode(phoneNo, verifyCode);
        return errorCode.getCodeString();
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

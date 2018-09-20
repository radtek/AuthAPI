package com.xiaoleitech.authapi.controller.test;

import com.xiaoleitech.authapi.helper.RelyPartHelper;
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
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
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

    @RequestMapping(value = "/test/send_websocket", method = RequestMethod.GET)
    public String sendWebSocket(@RequestParam("topic") String topic,
                                @RequestParam("message") String message) {
//        String user = "admin";
//        String password = "password";
//        String url = "ws://115.28.34.226:61623";
//        String destination = "/topic/event2";
//
//        StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
//        factory.setBrokerURI(url);
//        try {
//            Connection connection = factory.createConnection(user, password);
//            connection.start();
//            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//            Destination dest = new StompJmsDestination(destination);
//            MessageProducer producer = session.createProducer(dest);
//            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
//
//            String body = "abcdefghijklmnopqrstuvwxyz";
//            TextMessage msg = session.createTextMessage(body);
//            msg.setIntProperty("id", 1);
//            producer.send(msg);
//
//            producer.send(session.createTextMessage("SHUTDOWN"));
//            connection.close();
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }

//        String clientUrl = "ws://localhost:18080/JeeSite/client";
//        String clientUrl = "ws://admin:password@115.28.34.226:61623";
//
//        WebSocketClient client = new StandardWebSocketClient();
//
//        WebSocketStompClient stompClient = new WebSocketStompClient(client);
//
//        stompClient.setMessageConverter(new StringMessageConverter());
//
//        stompClient.setReceiptTimeLimit(3000);
//        //配置心跳频率，默认就是下面这个间隔
//        stompClient.setDefaultHeartbeat(new long[]{10000l,10000l});
//
//        //stompsession 使用必须配置Receiptable， taskScheduler作用是
//        //Configure a scheduler to use for heartbeats and for receipt tracking.
//        //为配置心跳频率和跟踪发送状态 准备的线程池
//        ThreadPoolTaskScheduler task = new ThreadPoolTaskScheduler();
//        task.initialize();
//
//        stompClient.setTaskScheduler(task);
//
//
//        StompSessionHandler sessionHandler = new MyStompSessionHandler();
//
//
//
//        ListenableFuture<StompSession> ret = stompClient.connect(clientUrl, sessionHandler);
//        ret.addCallback(new ListenableFutureCallback<StompSession>() {
//
//            @Override
//            public void onSuccess(StompSession session) {
//                session.subscribe("/app/subscribeme", new StompFrameHandler() {
//
//                    @Override
//                    public void handleFrame(StompHeaders headers, Object payload) {
//                        System.out.println("subscribe message : ");
//                        System.out.println(payload);
//                    }
//
//                    @Override
//                    public Type getPayloadType(StompHeaders headers) {
//                        return String.class;
//                    }
//                });
//                session.subscribe("/topic/hi", new StompFrameHandler() {
//
//                    @Override
//                    public void handleFrame(StompHeaders headers, Object payload) {
//                        System.out.println(payload);
//                    }
//
//                    @Override
//                    public Type getPayloadType(StompHeaders headers) {
//                        return Object.class;
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(Throwable ex) {
//                ex.printStackTrace();
//            }
//        });
//        StompSession sess = null;
//        try {
//            sess = ret.get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        //跟踪发送消息返回状态，必须启用该项
//        sess.setAutoReceipt(true);
//        //发一个消息
//        StompSession.Receiptable recpt = sess.send("/app/hi", "i'm java stomp client monkey stanthem.");
//
//        recpt.addReceiptTask(new Runnable() {
//
//            @Override
//            public void run() {
//                System.out.println("java client endpoint send msg success.");
//            }
//
//        });
//


//        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//        String url = "ws://115.28.34.226:61623/topic/WS2-01";
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        try {
//            Session session = container.connectToServer(MyStompClient.class, new URI(url));
//            Thread.sleep(1500);
//            session.getBasicRemote().sendText("123132132131");
//        } catch (DeploymentException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        WebSocketClient webSocketClient = new StandardWebSocketClient();
//        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//        stompClient.setTaskScheduler(new ConcurrentTaskScheduler());
//
//        String url = "ws://115.28.34.226:61623";
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        MyStompSessionHandler myStompSessionHandler = new MyStompSessionHandler(countDownLatch);
//        stompClient.connect(url, myStompSessionHandler);
//        while (stompClient.getPhase() == 2147483647) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }


//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        System.out.println("---> stompClient.connect");
//        stompClient.connect(url, new StompSessionHandlerAdapter(){
//            @Override
//            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//                session.send("/topic/WS-01", "payload");
//            }
//        });
//        try {
////            StompClient
//            MyWebSocketClient webSocketClient = new MyWebSocketClient(url);
//            if (webSocketClient != null) {
//                System.out.println("---->状态：" + webSocketClient.getReadyState());
//                webSocketClient.connect();
//                while (webSocketClient.getReadyState() != WebSocket.READYSTATE.OPEN) {
//                    System.out.println("---->状态：" + webSocketClient.getReadyState());
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//                webSocketClient.send(message);
//            }
//
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            return ErrorCodeEnum.ERROR_INTERNAL_ERROR.getCodeString();
//        }

        return ErrorCodeEnum.ERROR_OK.getCodeString();
    }
}

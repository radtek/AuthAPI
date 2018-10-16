package com.xiaoleitech.authapi.zold.tempCode;

public class TempTestCodeForStomp {
    //        String user = "admin";
//        String password = "password";
//        String url = "tcp://115.28.34.226:61613";
////        String url = "tcp://localhost:61613";
//        String destination = "/topic/event2";
//
//        StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
//        factory.setBrokerURI(url);
//        try {
//            Connection connection = factory.createConnection(user, password);
//            connection.setClientID("Client_11223344");
//            String clientID = connection.getClientID();
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
//        System.out.println("---> stompClient.connect");
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

}

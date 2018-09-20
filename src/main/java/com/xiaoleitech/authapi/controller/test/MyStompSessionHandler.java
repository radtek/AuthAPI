package com.xiaoleitech.authapi.controller.test;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.util.concurrent.CountDownLatch;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {
//    private final CountDownLatch countDownLatch;

    public MyStompSessionHandler() {
    }

//    public MyStompSessionHandler(CountDownLatch countDownLatch) {
//        this.countDownLatch = countDownLatch;
//    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        // ...
        System.out.println("---> MyStompSessionHandler afterConnected");

//        countDownLatch.countDown();
    }
}
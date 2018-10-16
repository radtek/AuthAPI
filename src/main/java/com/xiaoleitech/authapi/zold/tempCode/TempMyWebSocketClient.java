//package com.xiaoleitech.authapi.global.msgqueue;
//
//import org.springframework.web.socket.client.WebSocketClient;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.drafts.Draft_6455;
//import org.java_websocket.handshake.ServerHandshake;
//import org.springframework.stereotype.Component;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.Iterator;
//
//public class MyWebSocketClient extends WebSocketClient {
//
//    public MyWebSocketClient(String url) throws URISyntaxException {
//        super(new URI(url));
//        // TODO Auto-generated constructor stub
//    }
//
//    @Override
//    public void onOpen(ServerHandshake shake) {
//        // TODO Auto-generated method stub
//        System.out.println("握手...");
//        for(Iterator<String> it = shake.iterateHttpFields(); it.hasNext();) {
//            String key = it.next();
//            System.out.println(key+":"+shake.getFieldValue(key));
//        }
//    }
//
//    @Override
//    public void onMessage(String paramString) {
//        // TODO Auto-generated method stub
//        System.out.println("接收到消息："+paramString);
//    }
//
//    @Override
//    public void onClose(int paramInt, String paramString, boolean paramBoolean) {
//        // TODO Auto-generated method stub
//        System.out.println("关闭...");
//    }
//
//    @Override
//    public void onError(Exception e) {
//        // TODO Auto-generated method stub
//        System.out.println("异常"+e);
//
//    }
//}

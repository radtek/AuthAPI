package com.xiaoleitech.authapi.helper.websocket;

import com.xiaoleitech.authapi.helper.UtilsHelper;
//import org.json.JSONException;
//import org.json.JSONObject;
//import net.sf.json.JSONException;
//import net.sf.json.JSONObject;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/cable")
@Component
public class MyWebSocket {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<MyWebSocket>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    // 与Session关联的标识
    private String identifier;

    class MySocketResponse {
        public String mode;
        public String msg;
    }

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
//        try {
//            MySocketResponse mySocketResponse = new MySocketResponse();
//            mySocketResponse.mode = "ping";
//            mySocketResponse.msg = "ping";
//            net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(mySocketResponse);
//            String message = jsonObject.toString();
//            sendMessage(message);
            String message = "{\"type\":\"ping\"}";
        try {
            sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("向客户端发送：" + message);
        }
//        } catch (IOException e) {
//            System.out.println("IO异常");
//        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);

        String command = getCommand(message);
        if (command.equals("subscribe")) {
            identifier = generateIdentifier(message);
        }

//        //群发消息
//        for (MyWebSocket item : webSocketSet) {
//            try {
//                item.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 发生错误时调用
     * */
     @OnError
     public void onError(Session session, Throwable error) {
     System.out.println("发生错误");
     error.printStackTrace();
     }

     public static void websocketNotifyRedirect(String appUuid,
                                                String accountUuid,
                                                String authorizeToken,
                                                String nonce,
                                                String redirectUrl) {
         MyWebSocket socket = getConnectSocket(appUuid, nonce);
         if (socket == null)
             return;

         // 构建通知消息对象

         JSONObject allJson = new JSONObject();
         allJson.put("account_id", accountUuid);
         allJson.put("authorization_token", authorizeToken);
         allJson.put("nonce", nonce);
         allJson.put("redirect_url", redirectUrl);
         String message = allJson.toString();

         JSONObject msgJsonL1 = new JSONObject();
         msgJsonL1.put("message", message);

         JSONObject msgJsonL2 = new JSONObject();
         msgJsonL2.put("message", msgJsonL1.toString());

         // 发送消息
         try {
             socket.sendMessage(msgJsonL2.toString());
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

     public static MyWebSocket getConnectSocket(String appUuid, String nonce) {
         for (MyWebSocket socket : webSocketSet) {
             String inputIdentifier = formatIdentifier(appUuid, nonce);
             String socketIdentifier = socket.getIdentifier();

             if (socketIdentifier.equals(inputIdentifier))
                 return socket;
         }
         return null;
     }


     public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
     }


     /**
      * 群发自定义消息
      * */
    public static void sendInfo(String message) throws IOException {
        for (MyWebSocket item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    /**
     * 从message中提取能跟Session唯一对应的信息，绑定，以便Server能发送信息给指定的客户端
     * @param message 客户端发送来的消息
     * @return identifier;
     */
    public String generateIdentifier(String message) {
        if ( (message == null) || (message.isEmpty()) )
            return "";

//        try {
            String identString = UtilsHelper.getValueFromJsonString(message, "identifier");
            String appUuid = UtilsHelper.getValueFromJsonString(identString, "app_id");
            String nonce = UtilsHelper.getValueFromJsonString(identString, "nonce");
//
//                    org.json.JSONObject jsonMessage = new org.json.JSONObject(message);
//            String identString = jsonMessage.getString("identifier");
//            org.json.JSONObject jsonIdentifier = new org.json.JSONObject(identString);
//            String appUuid = jsonIdentifier.getString("app_id");
//            String nonce = jsonIdentifier.getString("nonce");
            return formatIdentifier(appUuid, nonce);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return "";
//        }
    }

    private static String formatIdentifier(String appUuid, String nonce) {
        return appUuid + "_" + nonce;
    }

    /**
     * 从message中提取command类型
     * @param message 客户端发送来的消息
     * @return command类型字串
     */
    public String getCommand(String message) {
        if ( (message == null) || (message.isEmpty()) )
            return "";

            String command = UtilsHelper.getValueFromJsonString(message, "command");
            return command;
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }

    public String getIdentifier() {
        return identifier;
    }
}
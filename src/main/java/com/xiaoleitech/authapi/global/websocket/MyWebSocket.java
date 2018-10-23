package com.xiaoleitech.authapi.global.websocket;

import com.alibaba.fastjson.JSONObject;
import com.xiaoleitech.authapi.global.utils.UtilsHelper;
//import net.sf.json.JSONObject;
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

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());

        // 在建立连接时回复一个 ping message
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "ping");
        try {
            sendMessage(jsonObject.toString());
            System.out.println("向客户端发送：" + jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);

        String command = getCommand(message);
        // 处理注册命令，保存消息中的标识符
        if (command.equals("subscribe")) {
            identifier = generateIdentifier(message);
            session.getId();
        }
    }

    /**
     * 发生错误时调用
     */
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
        // 查找对应的socket连接
        MyWebSocket socket = getConnectSocket(appUuid, nonce);
        if (socket == null)
            return;

        // 构建通知消息对象
        JSONObject messageJson = new JSONObject();
        messageJson.put("account_id", accountUuid);
        messageJson.put("authorization_token", authorizeToken);
//         messageJson.put("nonce", nonce);
        messageJson.put("redirect_url", redirectUrl);
        String message = messageJson.toString();

        // 消息又包了两层
        JSONObject packJsonL1 = new JSONObject();
        packJsonL1.put("message", message);
        JSONObject packJsonL2 = new JSONObject();
        packJsonL2.put("message", packJsonL1.toString());

        // 发送消息
        try {
            socket.sendMessage(packJsonL2.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据appUuid和nonce查找对应的socket连接
     *
     * @param appUuid 应用UUID
     * @return 查到的socket连接
     */
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
        System.out.println("WebSocket SendMessage: " + message);
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }


    /**
     * 群发自定义消息
     */
    public static void broadcast(String message) throws IOException {
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
     *
     * @param message 客户端发送来的消息
     * @return identifier;
     */
    public String generateIdentifier(String message) {
        if ((message == null) || (message.isEmpty()))
            return "";

        String identString = UtilsHelper.getValueFromJsonString(message, "identifier");
        String appUuid = UtilsHelper.getValueFromJsonString(identString, "app_id");
        String nonce = UtilsHelper.getValueFromJsonString(identString, "nonce");
        return formatIdentifier(appUuid, nonce);
    }

    private static String formatIdentifier(String appUuid, String nonce) {
        return appUuid + nonce;
    }

    /**
     * 从message中提取command类型
     *
     * @param message 客户端发送来的消息
     * @return command类型字串
     */
    public String getCommand(String message) {
        if ((message == null) || (message.isEmpty()))
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


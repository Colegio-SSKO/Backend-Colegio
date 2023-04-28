package com.example.backendcol.api;


import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ServerEndpoint("/questionChatHandler")
public class questionChatHandler {


    public static final HashMap<Integer, Session> userIdToChatWebSocketId = new HashMap<>();

    @OnOpen
    public void onOpen(Session session){
        System.out.println("WebSocket for quession chat opened: " + session.getId());

    }


    @OnMessage
    public void onMessage(String message, Session session) throws IOException {

        System.out.println(message);
        JSONObject messageData = new JSONObject(message);
        if (messageData.getBoolean("config")){
            String token = messageData.getString("token");
            JWT jwt = new JWT();
            jwt.decodeJWT(token);
            jwt.createToken();
            jwt.sign();
            if (!jwt.validate()){
                System.out.println("Invalid token");
            }
            else{
                System.out.println("wedwed");
                System.out.println(jwt.payload.getInt("sub"));
                userIdToChatWebSocketId.put(jwt.payload.getInt("sub"), session);
                System.out.println("onna ehenm chat ek connfigure una");
            }
        }
        else{
            System.out.println("nene");
            System.out.println(messageData.getInt("receiver"));
//            Session receiver = userIdToChatWebSocketId.get(messageData.getInt("receiver"));

        }


//        System.out.println("message : " + message);
//        JSONObject messageData = new JSONObject(message);
//
//        //handle database
//
//
//
//        //sending the message to the receiver
//        String receiverSocketID = userIdToWebSocketId.get(messageData.getInt("receiver"));
//        Session receiver =  questionChatSessions.get(receiverSocketID);
//        if (receiver == null){
//            System.out.println("U nidi");
//        }
//        else {
//            receiver.getAsyncRemote().sendText(message);
//        }
//
//
//        //sending the message back to the sender
//        String senderSocketID = userIdToWebSocketId.get(messageData.getInt("sender"));
//        Session sender =  questionChatSessions.get(senderSocketID);
//        if (sender == null){
//            System.out.println("Uth nidi");
//        }
//        else {
//            sender.getAsyncRemote().sendText(message);
//        }
//


    }
}

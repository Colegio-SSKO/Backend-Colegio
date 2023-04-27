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

    public static final HashMap<String,Session> questionChatSessions = new HashMap<>();
    public static final HashMap<Integer, String> userIdToWebSocketId = new HashMap<>();

    @OnOpen
    public void onOpen(Session session){
        System.out.println("WebSocket for quession chat opened: " + session.getId());
        questionChatSessions.put(session.getId(), session);

        //get the user id from the session. hardcoded for now
        userIdToWebSocketId.put(1, session.getId());
        System.out.println("webuID"+userIdToWebSocketId.get(1));
    }


    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("message : " + message);
        JSONObject messageData = new JSONObject(message);

        //handle database



        //sending the message to the receiver
        String receiverSocketID = userIdToWebSocketId.get(messageData.getInt("receiver"));
        Session receiver =  questionChatSessions.get(receiverSocketID);
        if (receiver == null){
            System.out.println("U nidi");
        }
        else {
            receiver.getAsyncRemote().sendText(message);
        }


        //sending the message back to the sender
        String senderSocketID = userIdToWebSocketId.get(messageData.getInt("sender"));
        Session sender =  questionChatSessions.get(senderSocketID);
        if (sender == null){
            System.out.println("Uth nidi");
        }
        else {
            sender.getAsyncRemote().sendText(message);
        }



    }
}

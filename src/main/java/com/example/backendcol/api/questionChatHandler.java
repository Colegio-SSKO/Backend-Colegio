package com.example.backendcol.api;


import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ServerEndpoint("/questionChatHandler")
public class questionChatHandler {

    public static final List<Session> questionChatSessions = new ArrayList<Session>();
    public static final HashMap<Integer, String> userIdToWebSocketId = new HashMap<>();

    @OnOpen
    public void onOpen(Session session){
        System.out.println("WebSocket for quession chat opened: " + session.getId());
        questionChatSessions.add(session);

        //get the user id from the session. hardcoded for now
        userIdToWebSocketId.put(1, session.getId());
        System.out.println("webuID"+userIdToWebSocketId.get(1));
    }
}

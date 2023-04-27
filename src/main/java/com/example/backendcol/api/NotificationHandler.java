package com.example.backendcol.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;

import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import jakarta.websocket.server.HandshakeRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint("/notificationHandler")
public class NotificationHandler {

    public static final HashMap<String,Session> notificationSessions = new HashMap<>();
    public static final HashMap<Integer, String> userIdToWebSocketId = new HashMap<>();
    @OnOpen
    public void onOpen(Session session,  HandshakeRequest handshakeRequest){
        System.out.println("WebSocket for notification is open: " + session.getId());
        notificationSessions.put(session.getId(), session);
        String authHeader = handshakeRequest.getHeaders().get("Authorization").get(0);
        String jwtToken = authHeader.substring("Bearer ".length());
        System.out.println("token is: " + jwtToken);



    }
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        try {
            JSONObject messageData = new JSONObject(message);
            if (messageData.getBoolean("config")){
                String token = (String) messageData.get("token");
                System.out.println(token);
            }
            else{
                System.out.println("aaaa");
            }
        }
        catch (Exception exception){
            System.out.println(exception);
        }
    }

}

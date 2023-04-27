package com.example.backendcol.api;


import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

import java.io.IOException;


@ServerEndpoint("/notificationHandler")
public class notificationHandler {
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

            }
        }
        else{
            System.out.println("nene");
        }
    }

}

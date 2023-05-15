package com.example.backendcol.api;


import com.example.backendcol.ServerData;
import com.example.backendcol.User;
import jakarta.enterprise.inject.New;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;


@ServerEndpoint("/notificationHandler")
public class notificationHandler {


    @OnOpen
    public void onOpen(Session session){
        System.out.println("WebSocket for notifications opened: " + session.getId());


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
                User owner = (User) ServerData.users.get(jwt.payload.getInt("sub"));
                owner.notificationSession = session;
                System.out.println("onna ehenm notification ek connfigure una");
            }
        }
        else{
            System.out.println("nene");
        }
    }

}

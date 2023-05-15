package com.example.backendcol.api;

import java.io.IOException;

import com.example.backendcol.ServerData;
import com.example.backendcol.User;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


@ServerEndpoint("/commentsHandler")
public class CommentsHandler {




    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket for quiz comments opened: " + session.getId());



    }
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("comment 0");
        System.out.println(message);
        JSONObject messageData = new JSONObject(message);
        System.out.println("comment 1");
        if (messageData.getBoolean("config")){
            String token = messageData.getString("token");
            JWT jwt = new JWT();
            jwt.decodeJWT(token);
            jwt.createToken();
            jwt.sign();
            System.out.println("comment 2");
            if (!jwt.validate()){
                System.out.println("Invalid token");
            }
            else{
                System.out.println("wedwed");
                System.out.println(jwt.payload.getInt("sub"));
                User owner = (User) ServerData.users.get(jwt.payload.getInt("sub"));
                owner.commentSession = session;
                System.out.println("onna ehenm comment ek connfigure una");
            }
        }
        else{
            System.out.println("nene");
        }
    }

    @OnClose
    public void onClose(Session session) {

        System.out.println("Session " + session.getId() + " has disconnected");
    }
}

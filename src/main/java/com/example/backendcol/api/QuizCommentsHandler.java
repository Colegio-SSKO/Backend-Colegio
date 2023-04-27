package com.example.backendcol.api;

import java.io.IOException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ServerEndpoint("/quizCommentsHandler")
public class QuizCommentsHandler {

    public static final List<Session> quizCommentSessions = new ArrayList<Session>();


    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket for quiz comments opened: " + session.getId());

        quizCommentSessions.add(session);
        System.out.println(quizCommentSessions.size());

    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("length : " + quizCommentSessions.size());
        System.out.println("Received message: " + message);
        for (Session receiver : quizCommentSessions){
            System.out.println("ki parak enwd");
            receiver.getAsyncRemote().sendText(message);
        }
    }

    @OnClose
    public void onClose(Session session) {
        quizCommentSessions.remove(session);
        System.out.println("Session " + session.getId() + " has disconnected");
    }
}

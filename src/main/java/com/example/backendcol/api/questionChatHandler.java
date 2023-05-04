package com.example.backendcol.api;


import com.example.backendcol.*;
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

        try{

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
                    System.out.println(jwt.payload.getInt("sub"));
                    User ownerObject = (User) ServerData.users.get(jwt.payload.getInt("sub"));
                    ownerObject.session = session;
                    System.out.println("onna ehenm chat ek connfigure una user id: " + jwt.payload.getInt("sub"));
                    System.out.println("eke session id ek: " + ownerObject.session.getId());


                }
            }
            else{
                System.out.println("nene");
                System.out.println(messageData.getInt("receiver"));

                Object receiver = ServerData.users.get(messageData.getInt("receiver"));
                Object sender = ServerData.users.get(messageData.getInt("sender"));

                if (receiver == null) {
                    System.out.println("receiver not logged in");
                }
                else if(sender == null) {
                    System.out.println("sender logged out");
                }
                else{


                    Boolean isAcceptedSender = false;

                    if (sender instanceof Teacher){
                        System.out.println(sender.getClass().getName());
                        Question question = ((Teacher) sender).questions.get(messageData.getInt("questionId"));
                        Question answerQuestion = ((Teacher) sender).answeringQuestions.get(messageData.getInt("questionId"));
                        if (question != null){
                            System.out.println("question");
                            if (question.data.getInt("question.user_id") == messageData.getInt("sender")
                                    &&
                                    question.data.getInt("teacher.user_ID") == messageData.getInt("receiver")
                            ){
                                isAcceptedSender = true;
                                question.storeMessage(messageData.getString("message"), ((Teacher) sender).userID);
                            }
                            else {
                                System.out.println("invalid message");
                            }
                        }
                        else if (answerQuestion != null){
                            System.out.println("question answer");
                            if (answerQuestion.data.getInt("question.user_id") == messageData.getInt("receiver")
                                    &&
                                    answerQuestion.data.getInt("teacher.user_ID") == messageData.getInt("sender")
                            ){
                                isAcceptedSender = true;
                                answerQuestion.storeMessage(messageData.getString("message"), ((Teacher) sender).userID);
                            }
                            else {
                                System.out.println("invalid message");
                            }
                        }
                        else {
                            System.out.println("invalid sender");
                        }

                    }

                    else {
                        System.out.println(sender.getClass().getName());
                        Question question = ((User) sender).questions.get(messageData.getInt("questionId"));
                        if (question != null){
                            if (question.data.getInt("question.user_id") == messageData.getInt("sender")
                                    &&
                                    question.data.getInt("teacher.user_ID") == messageData.getInt("receiver")
                            ){
                                isAcceptedSender = true;
                                question.storeMessage(messageData.getString("message"), ((User) sender).userID);
                            }
                        }

                        else {
                            System.out.println("invalid sender");
                        }

                    }

                    if (isAcceptedSender){
                        System.out.println("valid massage");
                        ((User)receiver).session.getAsyncRemote().sendText(message);

                    }

                }



//                if (receiver == null) {
//                    System.out.println("receiver not logged in");
//                }
//                else if(sender == null) {
//                    System.out.println("sender logged out");
//                }
//                else{
//                    System.out.println("receiver" + receiver.userID);
//                    System.out.println("sender" + sender.userID);
//                    Question question = sender.questions.get(messageData.getInt("questionId"));
//                    System.out.println(question.data.getInt("question.question_id"));
//                    question.storeMessage(message, sender.userID);
//                }
//
//               receiver.session.getAsyncRemote().sendText(message);

            }
        }
        catch (Exception exception){
            System.out.println(exception);
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

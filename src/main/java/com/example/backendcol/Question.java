package com.example.backendcol;

import jakarta.websocket.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Question {

    public JSONObject data;
    JSONArray messages;


    public Question(JSONObject jsonObject){
        data = jsonObject;

        try{


            Connection connection = Driver.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from accept  WHERE accept.question_id = ?");
            preparedStatement.setInt(1, data.getInt("question.question_id"));

            ResultSet resultSet = preparedStatement.executeQuery();
            JSONArray jsonArray = JsonHandler.createJSONArray(resultSet, "message", "isTeacherSent", "time", "chat_id");
            messages = jsonArray;

        }catch (Exception exception){
            System.out.println(exception);
        }
    }

    public void storeMessage(String message, Integer senderId){
       try {
           Integer isTeacherSent = 0;
           if (senderId == this.data.getInt("question.accept_teacher_id")){
               isTeacherSent = 1;
           }

           Connection connection = Driver.getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement("Insert into accept (teacher_id, question_id, message, isTeacherSent) values (?,?,?,?)");
           preparedStatement.setInt(1,this.data.getInt("question.accept_teacher_id"));
           preparedStatement.setInt(2,this.data.getInt("question.question_id"));
           preparedStatement.setString(3,message);
           preparedStatement.setInt(4,isTeacherSent);

           Integer result = preparedStatement.executeUpdate();
       }
       catch (Exception exception){
           System.out.println(exception);
       }
    }
}

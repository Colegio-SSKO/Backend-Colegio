package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Quiz extends Content {

    List<QuizQuestion> quizQuestions;

    public Quiz(JSONObject jsonObject){
        System.out.println("cunstructor called");
        try {
            quizQuestions = new ArrayList<>();
            this.data = jsonObject;
            Connection connection = Driver.getConnection();
            System.out.println(jsonObject.getInt("content_id"));
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from quiz_question inner join quiz on quiz_question.quiz_id = quiz.quiz_id WHERE quiz.content_id = ?;");
            preparedStatement.setInt(1,jsonObject.getInt("content_id"));
            ResultSet resultSet = preparedStatement.executeQuery();

            JSONArray jsonArray = JsonHandler.createJSONArray(resultSet, "question", "op1", "op2", "op3", "op4", "answer", "quiz_qid");

            System.out.println(jsonArray.length());
            for (int i = 0; i< jsonArray.length() ; i++){
                QuizQuestion newQuizQuestion = new QuizQuestion(jsonArray.getJSONObject(i));
                System.out.println("meka deprk run und");
                quizQuestions.add(newQuizQuestion);
            }
        }
        catch (Exception exception){
            System.out.println(exception);
        }
    }


}

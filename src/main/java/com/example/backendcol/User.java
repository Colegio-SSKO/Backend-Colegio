package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;

public class User extends ApiHandler {

    public JSONObject editProfile(Integer id, JSONObject requestObject){


        JSONObject jsonObject = new JSONObject();
        Connection connection = Driver.getConnection();
        try{

            //JDBC part
            PreparedStatement statement = connection.prepareStatement("UPDATE user SET f_name = ?, l_name = ? WHERE user_id = ?");
            PreparedStatement statement1 = connection.prepareStatement("UPDATE student SET education_level = ?, gender = ? WHERE user_id = ?");
            System.out.println(requestObject.getString("edu"));
            statement.setString(1,requestObject.getString("fName"));
            statement.setString(2,requestObject.getString("lName"));
            statement1.setString(1,requestObject.getString("edu"));
            statement1.setString(2,requestObject.getString("gender"));

            statement1.setInt(3,id);
            statement.setInt(3,id);
            int resultSet = statement.executeUpdate();
            int resultSet1 = statement1.executeUpdate();
            if(resultSet1==0 || resultSet == 0){
                jsonObject.put("message", "Inavlid User!");
                jsonObject.put("isError", 1);
                return jsonObject;
            }
            System.out.printf("Methnta enkn wed");
            jsonObject.put("message", "Profile successfully Updated!");
            jsonObject.put("isError", 0);
            return jsonObject;


        }catch (SQLException sqlException){
            System.out.println(sqlException);
            jsonObject.put("message", "Database error!");
            jsonObject.put("isError", 1);
            return jsonObject;
        }


    }


    public JSONObject changePassword(Integer id, JSONObject requestObject){
            JSONObject jsonObject = new JSONObject();
        try{
            Connection connection = Driver.getConnection();
            PreparedStatement statement1 = connection.prepareStatement("select * from user where user_id = ?");
            statement1.setInt(1,id);

            ResultSet resultSet = statement1.executeQuery();
            if (!resultSet.next()){
                jsonObject.put("isError", 0);
                jsonObject.put("message", "Invalid user");
                return jsonObject;
            }
            if (requestObject.getString("currPassword").equals(resultSet.getString("password"))){
                if (requestObject.getString("newPassword").equals(requestObject.getString("againPassword"))){
                    PreparedStatement statement = connection.prepareStatement("UPDATE user SET password = ? WHERE user_id = ?");
                    statement.setString(1,requestObject.getString("newPassword"));
                    statement.setInt(2,id);
                    int resultset = statement.executeUpdate();
                    jsonObject.put("isError", 0);
                    jsonObject.put("message", "Password successfully updated!");
                    return jsonObject;

                }else {
                    jsonObject.put("message", "New Password does not match");
                    jsonObject.put("isError",1);
                    return jsonObject;
                }

            }else {
                jsonObject.put("message", "Enter valid old password");
                jsonObject.put("isError", 1);
                return jsonObject;
            }



        }catch (SQLException sqlException){
            System.out.println(sqlException);
            jsonObject.put("isError", 1);
            jsonObject.put("message", "Invalid user");
            return jsonObject;
        }


    }




    public JSONObject editEmail(Integer id, JSONObject requestObject){
        JSONObject jsonObject = new JSONObject();
        try{
            Connection connection = Driver.getConnection();
            PreparedStatement statement1 = connection.prepareStatement("select * from user where user_id = ?");
            statement1.setInt(1,id);
            ResultSet resultSet = statement1.executeQuery();


            if (!resultSet.next()){
                jsonObject.put("message", "invalid user");
                jsonObject.put("isError", 1);
                return jsonObject;
            }
            if (requestObject.getString("currPassword1").equals(resultSet.getString("password"))){
                System.out.println("adoo");
                System.out.println(requestObject.getString("newEmail1"));
                if (requestObject.getString("currEmail1").equals(resultSet.getString("email"))){
                    System.out.println("aiii");
                    PreparedStatement statement = connection.prepareStatement("UPDATE user SET email = ? WHERE user_id = ?");
                    statement.setString(1,requestObject.getString("newEmail1"));
                    statement.setInt(2,id);
                    int resultset = statement.executeUpdate();
                    jsonObject.put("message", "email successfully Updated!");
                    jsonObject.put("isError", 0);
                    return jsonObject;

                }else {

                    jsonObject.put("message", "New Email does not match");
                    jsonObject.put("isError", 1);
                    return jsonObject;

                }

            }else {
                jsonObject.put("message", "Enter valid password");
                jsonObject.put("isError", 1);
                return jsonObject;
            }
        }catch (SQLException sqlException){
            System.out.println(sqlException);
            jsonObject.put("isError", 1);
            jsonObject.put("message", "Invalid user");
            return jsonObject;
        }


    }



    public JSONArray myCources(Integer id, JSONObject requestObject){
        JSONArray jasonarray = new JSONArray();
        Connection connection = Driver.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM purchase inner join content on content.content_id = purchase.content_id INNER JOIN publisher on publisher.user_id = content.user_id INNER join teacher on teacher.user_ID = publisher.user_id inner join organization on organization.user_id = publisher.user_id INNER JOIN course on course.content_id = content.content_id INNER join course_media on course_media.course_id = course.course_id INNER join user on user.user_id = publisher.user_id WHERE purchase.user_id = ?;");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            jasonarray = JsonHandler.createJSONArray(resultSet, "course_title", "f_name", "l_name", "decription", "media" ,"publisher_title", "media_description", "content_id" , "pro_pic");
        }catch (Exception exception){
            System.out.println(exception);
        }


        return jasonarray;
    }




    public JSONArray myQuizes(Integer id, JSONObject requestObject){

        JSONArray jasonarray = new JSONArray();
        Connection connection = Driver.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM quiz INNER JOIN content on content.content_id = quiz.content_id " +
                    "INNER JOIN publisher on publisher.user_id = content.user_id" +
                    " INNER join organization on organization.user_id = publisher.user_id" +
                    " inner JOIN teacher on teacher.user_ID = publisher.user_id" +
                    " INNER join user on user.user_id = publisher.user_id" +
                    " INNER join course_media on quiz.quiz_id = quiz_media.quiz_id;");

            ResultSet resultSet = statement.executeQuery();

            jasonarray = JsonHandler.createJSONArray(resultSet, "course_title", "f_name", "l_name", "decription", "media" ,"publisher_title", "media_description", "content_id");
        }catch (Exception exception){
            System.out.println(exception);
        }


        return jasonarray;
    }


    public JSONObject deleteProfile(Integer id, JSONObject requestObject){
        System.out.printf("kccccc");
        JSONObject jsonObject = new JSONObject();
        Connection connection = Driver.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE user " +
                    "SET status = 1 " +
                    "WHERE user_id = ?;");
            System.out.printf("1111111");

            statement.setInt(1,id);
            Integer resID = statement.executeUpdate();
            jsonObject.put("isError", 0);
            System.out.printf("222222");

            jsonObject.put("message", "Profile deleted successfully!");
            return jsonObject;

        }catch (Exception exception){
            System.out.println(exception);
            jsonObject.put("isError", 1);
            jsonObject.put("message", "Invalid user");
            return jsonObject;
        }

    }


    public JSONArray myQuestions(Integer id, JSONObject requestObject){

        JSONArray jasonarray = new JSONArray();
        Connection connection = Driver.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * from question INNER join teacher on question.teacher_id = teacher.teacher_id INNER join user on user.user_id = teacher.user_ID INNER join question_media on question.question_id = question_media.question_id WHERE question.user_id = ?;");
            System.out.println("aaaa");
            statement.setInt(1,id);
            ResultSet resultSet = statement.executeQuery();

            jasonarray = JsonHandler.createJSONArray(resultSet,  "question_Id", "question_img","question_title","question_description", "f_name" , "l_name","status", "media", "qulification_level" );
        }catch (Exception exception){
            System.out.println(exception);
        }


        return jasonarray;
    }




    public JSONObject profileCard(Integer id, JSONObject requestObject){

        JSONObject jasonobject = new JSONObject();
        Connection connection = Driver.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * from user where user_id=?");
            ResultSet resultSet = statement.executeQuery();
            statement.setInt(1,id);
            System.out.printf("HElloo from kbm");

            jasonobject = JsonHandler.createJSONObject(resultSet, "course_title", "f_name", "l_name", "decription", "media" ,"publisher_title", "media_description", "content_id");
        }catch (Exception exception){
            System.out.println(exception);
        }


        return jasonobject;
    }




}

package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;

public class User extends ApiHandler {
    public JSONArray viewpurchaseCourse(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println("DB connection1111");

        JSONArray jsonArray= new JSONArray();
        try{
            System.out.println("DB connection123");
            Statement st= connection.createStatement();
            ResultSet rs= st.executeQuery("Select course.decription as description, course.introduction_media as content_image , course.course_title as course_title, purchase.content_id as content_ID, user.f_name as f_name, user.l_name as l_name from course inner join purchase on purchase.content_id= course.content_id inner join content on course.content_id= content.content_id inner join user on content.user_id= user.user_id where purchase.user_id=1");

            jsonArray = JsonHandler.createJSONArray(rs, "content_image", "description", "course_title" , "content_ID", "f_name", "l_name");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }





    public JSONArray viewcart(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println("DB connectionqq");

        JSONArray jsonArray= new JSONArray();
        try{
            System.out.println("DB connectiontt");
            Statement st= connection.createStatement();
            ResultSet rs= st.executeQuery("Select course.decription as description, course.introduction_media as img_src , course.course_title as title, course.price as price, user.date_joined as description2, CONCAT(user.f_name, user.l_name) as author from content inner join cart on cart.content_id= content.content_id inner join course on content.content_id= course.content_id inner join user on content.user_id= user.user_id where cart.user_id=4;");

            jsonArray = JsonHandler.createJSONArray(rs, "img_src", "description", "title" , "price", "description2", "author");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }



    public JSONArray viewquession(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println("DB connectionqq");

        JSONArray jsonArray= new JSONArray();
        try{
            System.out.println("DB connectiontt");
            Statement st= connection.createStatement();
            ResultSet rs= st.executeQuery("Select question.question_description as description, question.question_title as img_src , course.course_title as title, question_question_image as price, user.date_joined as description2, CONCAT(user.f_name, user.l_name) as author from content inner join cart on cart.content_id= content.content_id inner join course on content.content_id= course.content_id inner join user on content.user_id= user.user_id where cart.user_id=4;");

            jsonArray = JsonHandler.createJSONArray(rs, "img_src", "description", "title" , "price", "description2", "author");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }


    public JSONObject viewprofile(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println("DB connectionqq");

        JSONObject jsonObject= new JSONObject();
        try{
            System.out.println("DB connectiontt");
            Statement st= connection.createStatement();
            ResultSet rs= st.executeQuery("Select CONCAT(user.f_name, user.l_name) as name, user.pro_pic as img_src, user.date_joined as date, student.education_level as level from user inner join student on user.user_id=student.user_id where user.user_id=4");

            jsonObject = JsonHandler.createJSONObject(rs, "name", "img_src", "date", "level");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }
}

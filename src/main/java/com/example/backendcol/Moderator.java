package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Moderator extends ApiHandler {

    public JSONArray generate_report(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("select report_course.reason as reason, report_course.date as date, concat(user.f_name,' ', user.l_name) as name, course.course_title as title, content.content_id as content_id, report_course.course_id as course_id from course INNER JOIN report_course on report_course.course_id= course.course_id INNER JOIN user on report_course.user_id= user.user_id INNER JOIN content on course.content_id= content.content_id where report_course.course_id=?;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "date", "name", "title", "reason", "course_id","content_id");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }


    public JSONArray view_reported_courses(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("select distinct report_course.course_id as course_id, course.course_title as title, concat(user.f_name,' ', user.l_name) as name, subject.name as subject, course.introduction_media as img_src from report_course inner join course on report_course.course_id= course.course_id INNER JOIN content on course.content_id= content.content_id INNER JOIN user on content.user_id= user.user_id INNER JOIN subject on content.subject_id= subject.subject_id where content.status=0;");
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "course_id","title", "name", "subject", "img_src");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }



    public JSONObject disable_course(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println("sewwwwwwwww");

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("UPDATE content SET status = 1 WHERE content_id = ?;");
            statement.setInt(1,requestObject.getInt("content_id"));
            Integer res_id= statement.executeUpdate();

            statement = connection.prepareStatement("INSERT INTO resolve_reports VALUES (?,?);");
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("content_id"));
            Integer res_id2= statement.executeUpdate();

            if(res_id2==1){
                jsonObject.put("message","Disable course successfully");
            }
            else{
                jsonObject.put("message","Error");
            }

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }



    public JSONArray handle_users(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT DISTINCT user.user_id as user_id, concat(user.f_name,' ', user.l_name)as name, user.email as email, user.pro_pic as img_src from user INNER JOIN report_user on report_user.user_id= user.user_id where user.status=0;");
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "user_id","email", "name", "img_src");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }



    public JSONArray generate_report_person(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("select report_user.reason as reason, report_user.date as date, concat(user.f_name,' ', user.l_name) as name, user.email as u_email, report_user.user_id as user_id from user INNER JOIN report_user on report_user.reported_userid= user.user_id where report_user.user_id=?;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "date", "name", "u_email", "reason", "user_id");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }


    public JSONObject disable_person(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println("sewwwwwwwww");

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("UPDATE user SET status = 1 WHERE user_id = ?;");
            statement.setInt(1,requestObject.getInt("user_id"));
            Integer res_id= statement.executeUpdate();
            System.out.println(res_id);
            System.out.println("danu");

            statement = connection.prepareStatement("INSERT INTO resolve_user_report VALUES (?,?);");
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("user_id"));
            Integer res_id2= statement.executeUpdate();
            System.out.println("kuma");
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }

}











package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;


import java.util.Random;

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


    public JSONArray generate_report_quiz(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("select report_quiz.reason as reason, report_quiz.date as date, concat(user.f_name,' ', user.l_name) as name, quiz.quiz_title as title, content.content_id as content_id, report_quiz.quiz_id as quiz_id from quiz INNER JOIN report_quiz on report_quiz.quiz_id= quiz.quiz_id INNER JOIN user on report_quiz.user_id= user.user_id INNER JOIN content on quiz.content_id= content.content_id where report_quiz.quiz_id=? && content.status=0;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "date", "name", "title", "reason", "quiz_id","content_id");

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


    public JSONArray view_reported_quiz(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("select distinct report_quiz.quiz_id as quiz_id, quiz.quiz_title as title, concat(user.f_name,' ', user.l_name) as name, subject.name as subject, quiz.image as img_src from report_quiz inner join quiz on report_quiz.quiz_id= quiz.quiz_id INNER JOIN content on quiz.content_id= content.content_id INNER JOIN user on content.user_id= user.user_id INNER JOIN subject on content.subject_id= subject.subject_id where content.status=0;");
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "quiz_id","title", "name", "subject", "img_src");

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

        JSONObject jsonObject2= new JSONObject();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("UPDATE content SET status = 1 WHERE content_id = ?;");
            statement.setInt(1,requestObject.getInt("content_id"));
            Integer res_id= statement.executeUpdate();

            statement = connection.prepareStatement("INSERT INTO resolve_reports VALUES (?,?);");
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("content_id"));
            Integer res_id2= statement.executeUpdate();



//            notification part
            PreparedStatement statement2;
            statement2= connection.prepareStatement("Select user_id from content where content_id=?");
            statement2.setInt(1,requestObject.getInt("content_id"));
            ResultSet rs2= statement2.executeQuery();
            Integer user_id= rs2.getInt("user_id");

            Date date = Date.valueOf(currentDate);
            Time time = Time.valueOf(currentTime);
            int type = 13;
            statement = connection.prepareStatement("INSERT INTO notification (date, time, type, user_id_receiver,mod_id_sender,status) values (?,?,?,?,?,0)");
            statement.setDate(1, date);
            statement.setTime(2, time);
            statement.setInt(3, user_id);
            statement.setInt(4,id);
            Integer num2 = statement.executeUpdate();
            jsonObject.put("date", date);
            jsonObject.put("time", time);
            jsonObject.put("user_id_sender", id);
            jsonObject.put("user_id_receiver", user_id);
            jsonObject.put("type", type);
            System.out.println(jsonObject.toString());
            System.out.println(user_id);
            System.out.println(id);

            if (!ServerData.users.containsKey(user_id)){
                System.out.println("receiver is offline");
            }
            else{
                User receiver = (User) ServerData.users.get(user_id);
                receiver.notificationSession.getAsyncRemote().sendText(jsonObject.toString());
            }

            return jsonObject;


//            if(res_id2==1 && num2==1){
//                jsonObject.put("message","Disable course successfully");
//            }
//            else{
//                jsonObject.put("message","Error");
//            }

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }



    public JSONObject disable_quiz(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println("sewwwwwwwww");

        JSONObject jsonObject= new JSONObject();

        JSONObject jsonObject2= new JSONObject();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("UPDATE content SET status = 1 WHERE content_id = ?;");
            statement.setInt(1,requestObject.getInt("content_id"));
            Integer res_id= statement.executeUpdate();

            statement = connection.prepareStatement("INSERT INTO resolve_quiz VALUES (?,?);");
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("content_id"));
            Integer res_id2= statement.executeUpdate();


            //notification_part

            PreparedStatement statement2;
            statement2= connection.prepareStatement("Select user_id from content where content_id=?");
            statement2.setInt(1,requestObject.getInt("content_id"));
            ResultSet rs2= statement2.executeQuery();
            Integer user_id= rs2.getInt("user_id");


            statement = connection.prepareStatement("INSERT INTO notification (date, time, type,message,user_id_receiver,mod_id_sender,status) values (?,?,13,'because of the reports moderator disable your quiz',?,?,0)");
            statement.setDate(1, Date.valueOf(currentDate));
            statement.setTime(2, Time.valueOf(currentTime));
            statement.setInt(3, user_id);
            statement.setInt(4,id);
            Integer num2 = statement.executeUpdate();
//
//            if(res_id2==1){
//                jsonObject.put("message","Disable quiz successfully");
//            }
//            else{
//                jsonObject.put("message","Error");
//            }

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


    public JSONArray view_verification_list(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * from upgrade_to_teacher INNER JOIN user ON upgrade_to_teacher.user_id= user.user_id where upgrade_to_teacher.status=0;");
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "user_id","education_level", "f_name", "l_name","pro_pic", "certificate","refers","email");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }



    public JSONObject generate_report_verify_teacher(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * from upgrade_to_teacher INNER JOIN user ON upgrade_to_teacher.user_id= user.user_id where upgrade_to_teacher.status=0 && upgrade_to_teacher.user_id=? ;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            jsonObject = JsonHandler.createJSONObject(rs, "user_id","upgrade_id","education_level", "f_name", "l_name","pro_pic", "certificate","refers","email", "gender");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }


    public JSONObject accept_teacher_verify(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();


        try{

            Integer min = 1;
            Integer max = 10000000;

            Random random = new Random();
            Integer randomNumber  = random.nextInt((max-min) + 1) + min;

            PreparedStatement statement;
            statement = connection.prepareStatement("UPDATE upgrade_to_teacher SET status=2 WHERE upgrade_id=?;");
            statement.setInt(1,requestObject.getInt("upgrade_id"));
            Integer num= statement.executeUpdate();
            System.out.println("upgrade ek1");

            statement = connection.prepareStatement("INSERT INTO verify_teacher (upgrade_id, moderator_id, status) VALUES (?,?,0);");
            statement.setInt(1,requestObject.getInt("upgrade_id"));
            statement.setInt(2,id);
            Integer num2= statement.executeUpdate();


            String numberString = String.valueOf(requestObject.getInt("user_id"));
            char firstDigit = numberString.charAt(0);
            int firstDigitInt = Character.getNumericValue(firstDigit);
            Integer tag= randomNumber+ firstDigitInt;

            statement = connection.prepareStatement("INSERT INTO teacher (gender, qulification_level, tag, user_ID) VALUES (?,?,?,?);");
            statement.setString(1, requestObject.getString("gender"));
            statement.setString(2, requestObject.getString("qulification_level"));
            statement.setInt(3,tag);
            statement.setInt(4,requestObject.getInt("user_id"));
            Integer num3= statement.executeUpdate();

            statement = connection.prepareStatement("UPDATE student SET status=1 WHERE user_id=?;");
            statement.setInt(1,requestObject.getInt("user_id"));
            Integer num4= statement.executeUpdate();

            statement = connection.prepareStatement("UPDATE user SET verification_status=2 WHERE user_id=?;");
            statement.setInt(1,requestObject.getInt("user_id"));
            Integer num5= statement.executeUpdate();


        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }



    public JSONObject delete_teacher_verify(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("UPDATE upgrade_to_teacher SET status=1 WHERE upgrade_id=?;");
            statement.setInt(1,requestObject.getInt("upgrade_id"));
            Integer num= statement.executeUpdate();

            statement = connection.prepareStatement("INSERT INTO verify_teacher (upgrade_id, moderator_id, status) VALUES (?,?,1);");
            statement.setInt(1,requestObject.getInt("upgrade_id"));
            statement.setInt(2,id);
            Integer num2= statement.executeUpdate();

            statement = connection.prepareStatement("UPDATE user SET verification_status=3 WHERE user_id=?;");
            statement.setInt(1,requestObject.getInt("user_id"));
            Integer num5= statement.executeUpdate();


        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }



}











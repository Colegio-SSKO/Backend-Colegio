package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;


public class Teacher extends User {


    public static Teacher parseTeacher(User user){
        Teacher teacher = new Teacher();
        teacher.userID = user.userID;
        teacher.name = user.name;
        teacher.email = user.email;
        return teacher;
    }
    public JSONObject teacher_send_req(Integer id, JSONObject requestObject){
        System.out.println("athulata awa");
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        JSONObject jsonObject2= new JSONObject();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        jsonObject.put("message","send request successfully");
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * from teacher_req_org inner join teacher on teacher_req_org.teacher_id=teacher.teacher_id where teacher.user_ID=? && organization_id=? && status=2");
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("organization_id"));
            ResultSet rs= statement.executeQuery();

            if(rs.next()){
                statement = connection.prepareStatement("UPDATE teacher_req_org teacher inner join teacher on teacher_req_org.teacher_id=teacher.teacher_id set status=0 where teacher.user_ID=? && organization_id=?");
                statement.setInt(1,id);
                statement.setInt(2,requestObject.getInt("organization_id"));
                Integer res_id = statement.executeUpdate();
            }

            else{
                statement = connection.prepareStatement("SELECT * from teacher_req_org inner join teacher on teacher_req_org.teacher_id=teacher.teacher_id where teacher.user_ID=? && organization_id=? && status=0");
                statement.setInt(1,id);
                statement.setInt(2,requestObject.getInt("organization_id"));
                ResultSet rs2= statement.executeQuery();

                if(rs2.next()){
                    jsonObject.put("message","You already send request");
                }

                else{
                    statement = connection.prepareStatement("SELECT * from org_has_teacher  inner join teacher on org_has_teacher.teacher_id= teacher.teacher_id where teacher.user_ID=? && org_has_teacher.organization_id=? && org_has_teacher.status=0");
                    statement.setInt(1,id);
                    statement.setInt(2,requestObject.getInt("organization_id"));
                    ResultSet rs3= statement.executeQuery();

                    if(rs3.next()){
                        jsonObject.put("message","You already a teacher of this organization");
                    }

                    else{
                        statement= connection.prepareStatement("Select teacher_id from teacher where user_ID=?");
                        statement.setInt(1,id);
                        ResultSet rs4= statement.executeQuery();
                        Integer teacherid= rs4.getInt("teacher_id");
                        statement = connection.prepareStatement("INSERT INTO teacher_req_org (status, teacher_id, organization_id) values (0,?,?)");
                        statement.setInt(1,id);
                        statement.setInt(2,teacherid);
                        Integer res_id = statement.executeUpdate();
                        jsonObject.put("message","Send request successfully");


                        //notification part
//                        PreparedStatement statement2;
//                        statement2= connection.prepareStatement("Select user_id from organization where organization_id=?");
//                        statement2.setInt(1,requestObject.getInt("organization_id"));
//                        ResultSet rs5= statement2.executeQuery();
//                        jsonObject2 = JsonHandler.createJSONObject(rs5, "user_id");
//                        System.out.println(jsonObject2.getInt("user_id"));
//                        System.out.println("sew");
//
//
//                        statement = connection.prepareStatement("INSERT INTO notification (title, description, date, time, type, user_id_receiver, user_id_sender) VALUES (\"Teacher Request\", \"You have a teacher request\", ?, ?,1, ?,?);");
//                        statement.setDate(1, Date.valueOf(currentDate));
//                        statement.setTime(2, Time.valueOf(currentTime));
//                        statement.setInt(3, jsonObject2.getInt("user_id"));
//                        statement.setInt(4,id);
//                        Integer num = statement.executeUpdate();
                    }
                }
            }
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }
        return jsonObject;
    }


    public JSONArray teacher_view_org_req(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("select concat(user.f_name,' ', user.l_name) as name, user.pro_pic as img_src, organization.organization_id as organization_id, organization.address as address from organization INNER JOIN org_teacher_request on org_teacher_request.organization_id= organization.organization_id INNER JOIN user on organization.user_id= user.user_id WHERE org_teacher_request.teacher_id=? && org_teacher_request.status=0;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "name", "img_src", "organization_id","address");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }


    public JSONObject teacher_accept_org(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * from org_has_teacher where organization_id=? && teacher_id=?");
            statement.setInt(2,id);
            statement.setInt(1,requestObject.getInt("organization_id"));
            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                statement = connection.prepareStatement("UPDATE org_has_teacher SET status=0 WHERE organization_id=? && teacher_id=?");
                statement.setInt(2,id);
                statement.setInt(1,requestObject.getInt("organization_id"));
                Integer res_id = statement.executeUpdate();
            }
            else{
                statement = connection.prepareStatement("INSERT INTO org_has_teacher (organization_id, teacher_id, status) VALUES (?, ?, 0);");
                statement.setInt(2,id);
                statement.setInt(1,requestObject.getInt("organization_id"));
                Integer res_id = statement.executeUpdate();
            }


            statement = connection.prepareStatement("UPDATE org_teacher_request SET status=1 WHERE teacher_id=? && organization_id=?; ");
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("organization_id"));
            Integer res_id2 = statement.executeUpdate();

            if(res_id2==1){
                jsonObject.put("message","Join organization successfully");
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



    public JSONObject teacher_remove_org_req(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("UPDATE org_teacher_request SET status=2 WHERE teacher_id=? && organization_id=?");
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("organization_id"));
            Integer res_id = statement.executeUpdate();

            if(res_id==1){
                jsonObject.put("message","Remove organization request successfully");
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

    public JSONArray teacher_published_course(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            System.out.println("DB connectiontt");
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT course.introduction_media as img_src, course.course_title as title, course.decription as description, course.price as price, content.content_id as content_id, content.status as status from course INNER JOIN content on course.content_id= content.content_id INNER JOIN user on content.user_id=user.user_id INNER JOIN publisher on publisher.user_id= content.user_id where publisher.user_id=? && content.status=0;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            jsonArray = JsonHandler.createJSONArray(rs, "description", "img_src", "title", "status", "content_id","price");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }


    public JSONArray teacher_published_quiz(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            System.out.println("DB connectiontt");
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT quiz.image as img_src, quiz.quiz_title as title, quiz.description as description, quiz.price as price, content.content_id as content_id, content.status as status from quiz INNER JOIN content on quiz.content_id= content.content_id INNER JOIN user on content.user_id=user.user_id INNER JOIN publisher on publisher.user_id= content.user_id where publisher.user_id=?;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            jsonArray = JsonHandler.createJSONArray(rs, "description", "img_src", "title", "status", "content_id","price");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }


    public JSONObject teacher_disable_content(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            System.out.println("DB connectiontt");
            PreparedStatement statement;
            statement = connection.prepareStatement("UPDATE content SET status=1 WHERE content_id=?;");
            statement.setInt(1, requestObject.getInt("content_id"));
            Integer num = statement.executeUpdate();
            jsonObject.put("num",1);
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }





    public JSONObject published_quiz(Integer id, JSONObject requestObject){
        System.out.println("wedaaaaaaaaaaaaa");
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        LocalDate currentDate = LocalDate.now();

        Integer generatedKey = -100;
        Integer generatedKey2 = -100;



        try{
            PreparedStatement statement;


            //create new content
            statement = connection.prepareStatement("INSERT INTO content (user_id, subject_id, date, status, type, price) VALUES (?, ?,?,0,1,?)",Statement.RETURN_GENERATED_KEYS );
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("subject"));
            statement.setDate(3, Date.valueOf(currentDate));
            statement.setInt(4, requestObject.getInt("price"));
            Integer result = statement.executeUpdate();
            System.out.println("Hri meka wada");

            if (result == 1){
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()){
                    generatedKey = resultSet.getInt(1);
                    System.out.println("key is : "+ generatedKey);
                }
            }


            //insert details to quiz table
            statement = connection.prepareStatement("INSERT INTO quiz (description, quiz_title, content_id) VALUES (?,?,?)",Statement.RETURN_GENERATED_KEYS );
            statement.setString(1,requestObject.getString("description"));
            statement.setString(2, requestObject.getString("title"));
            statement.setInt(3, generatedKey);
            Integer num = statement.executeUpdate();

            if (num == 1){
                ResultSet resultSet2 = statement.getGeneratedKeys();
                if(resultSet2.next()){
                    generatedKey2 = resultSet2.getInt(1);
                    System.out.println("key is : "+ generatedKey2);
                }
            }


            //get questions array in the request object
            JSONArray quizQuestions = requestObject.getJSONArray("quizQuestions");
            System.out.println("Question thiyna array eka gaththa");

            //insert questions to the quiz_question table
            statement = connection.prepareStatement("INSERT INTO quiz_question (answer, question, op1, op2, op3, op4, quiz_id) VALUES (?,?,?,?,?,?,?)");

            for (int i = 0;i<quizQuestions.length();i++){
                statement.setString(1, quizQuestions.getJSONObject(i).getString("answer"));
                statement.setString(2, quizQuestions.getJSONObject(i).getString("question"));
                statement.setString(3, quizQuestions.getJSONObject(i).getString("opt1"));
                statement.setString(4, quizQuestions.getJSONObject(i).getString("opt2"));
                statement.setString(5, quizQuestions.getJSONObject(i).getString("opt3"));
                statement.setString(6, quizQuestions.getJSONObject(i).getString("opt4"));
                statement.setInt(7, generatedKey2);
                statement.addBatch();
            }

            int[] numberOdUpdates = statement.executeBatch();

            if (numberOdUpdates.length == quizQuestions.length()){
                System.out.println("Update ek lssnt una");
            }
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }
}
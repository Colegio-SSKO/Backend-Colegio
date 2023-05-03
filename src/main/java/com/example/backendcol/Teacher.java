package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;


public class Teacher extends User {

    public HashMap<Integer,Question> answeringQuestions;

    public Teacher(){

    }

    public Teacher(JSONObject jsonObject){
        try {
            JSONArray jsonArray = new JSONArray();

            Connection connection = Driver.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM question INNER JOIN teacher ON question.accept_teacher_id= teacher.teacher_id INNER JOIN user ON question.user_id= user.user_id INNER join question_media on question.question_id = question_media.question_id WHERE (question.status=1 OR question.status=2) AND teacher.user_ID=?;");
            preparedStatement.setInt(1, this.userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("mkkd oi me questions teacher");
            jsonArray = JsonHandler.createJSONArray(resultSet,  "question.question_id", "question_img","question_title","question_description","media", "f_name" , "l_name","pro_pic","question.user_id","question.status");
            for (int i = 0; i<jsonArray.length() ; i++){
                Question newQuestion = new Question(jsonArray.getJSONObject(i));
                answeringQuestions.put( jsonArray.getJSONObject(i).getInt("question.question_id"),newQuestion);
                System.out.println("nidimthai q a");
            }



            System.out.println(questions.size());
        }catch (Exception exception){
            System.out.println(exception);
        }

    }




    public static Teacher parseTeacher(User user){
        Teacher teacher = new Teacher();
        teacher.userID = user.userID;
        teacher.name = user.name;
        teacher.email = user.email;
        teacher.cart = user.cart;
        teacher.purchasedContent = user.purchasedContent;
        teacher.questions = user.questions;
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
            statement = connection.prepareStatement("SELECT course.introduction_media as img_src, content.title as title, course.decription as description, course.price as price, content.content_id as content_id, content.status as status from course INNER JOIN content on course.content_id= content.content_id INNER JOIN user on content.user_id=user.user_id INNER JOIN publisher on publisher.user_id= content.user_id where publisher.user_id=? && content.status=0;");
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


    @Override
    public JSONObject viewprofile(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            System.out.println("DB connectiontt");
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT user.pro_pic as img_src, CONCAT(user.f_name,' ', user.l_name) as name, teacher.tag as tag, teacher.qulification_level as quli, teacher.gender as gender, user.user_id as user_id from user INNER JOIN teacher on teacher.user_ID= user.user_id where teacher.user_ID=?;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            jsonObject = JsonHandler.createJSONObject(rs, "name", "img_src", "quli", "gender", "user_id","tag");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }



    public JSONObject teacher_send_req_org(Integer id, JSONObject requestObject){
        System.out.println("athulata awa");
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * FROM teacher WHERE user_ID=?");
            statement.setInt(1,id);
            ResultSet rs= statement.executeQuery();

            if(rs.next()){
                Integer teacher_id= rs.getInt("teacher_id");

                statement = connection.prepareStatement("INSERT INTO org_teacher_request (teacher_id, organization_id, status, type) VALUES (?,?,0,0) ON DUPLICATE KEY UPDATE status = 0, type=1;");
                statement.setInt(2,requestObject.getInt("organization_id"));
                statement.setInt(1,teacher_id);
                Integer num = statement.executeUpdate();
                System.out.println("request table eka update una");




                //Notification part

                PreparedStatement statement2;
                statement2= connection.prepareStatement("SELECT * FROM organization WHERE organization_id=?");
                statement2.setInt(1,requestObject.getInt("organization_id"));
                ResultSet rs2= statement2.executeQuery();

                if(rs2.next()){
                    Integer organization_userid= rs2.getInt("user_id");
                    System.out.println("user id eka gaththa");


                    statement = connection.prepareStatement("INSERT INTO notification (date, time, type, user_id_receiver,user_id_sender,status) values (?,?,1,?,?,0)");
                    statement.setDate(1, Date.valueOf(currentDate));
                    statement.setTime(2, Time.valueOf(currentTime));
                    statement.setInt(3, organization_userid);
                    statement.setInt(4,id);
                    Integer num2 = statement.executeUpdate();
                    System.out.println("notification eka damma");

                    jsonObject.put("message", "Send request successfully");
                }

            }



        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }




    @Override
    public JSONObject editProfile(Integer id, JSONObject requestObject){


        JSONObject jsonObject = new JSONObject();
        Connection connection = Driver.getConnection();
        try{

            //JDBC part
            PreparedStatement statement = connection.prepareStatement("UPDATE user SET f_name = ?, l_name = ?, DOB= ? WHERE user_id = ?");
            PreparedStatement statement1 = connection.prepareStatement("UPDATE teacher SET gender = ? WHERE user_id = ?");
            statement.setString(1,requestObject.getString("fName"));
            statement.setString(2,requestObject.getString("lName"));
            statement.setString(3,requestObject.getString("dob"));
            statement1.setString(1,requestObject.getString("gender"));



            statement1.setInt(2,id);
            statement.setInt(4,id);
            int resultSet = statement.executeUpdate();
            int resultSet1 = statement1.executeUpdate();
            System.out.println(resultSet);
            System.out.println(resultSet1);

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




    @Override
    public JSONArray vieworganization(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        Integer value;

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT concat(user.f_name,' ', user.l_name) as name, organization.address as address, user.pro_pic as img_src, organization.organization_id as organization_id from user INNER JOIN organization on organization.user_id= user.user_id where user.status=0;");
            ResultSet rs = statement.executeQuery();

            jsonArray = JsonHandler.createJSONArray(rs, "name", "address", "img_src", "organization_id");

            PreparedStatement statement2;
            statement2= connection.prepareStatement("SELECT * FROM org_teacher_request INNER JOIN teacher ON org_teacher_request.teacher_id= teacher.teacher_id WHERE teacher.user_id=? && org_teacher_request.organization_id=? && (org_teacher_request.status=0 || org_teacher_request.status=2);");

            for (int i = 0;i<jsonArray.length();i++){
                Integer organization_id= jsonArray.getJSONObject(i).getInt("organization_id");
                statement2.setInt(1,id);
                statement2.setInt(2,organization_id);
                ResultSet rs2= statement2.executeQuery();

                if(rs2.next()){
                    value= 1;
                }
                else {
                    value=0;
                }

                jsonArray.getJSONObject(i).put("value", value);
            }

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }
}
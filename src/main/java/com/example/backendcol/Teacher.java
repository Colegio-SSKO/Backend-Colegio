package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Teacher extends User {

    public HashMap<Integer,Question> answeringQuestions;



    public Teacher(Integer userid){
        System.out.println("Teacher Constructor called");
        try {
            JSONArray jsonArray = new JSONArray();

            Connection connection = Driver.getConnection();

            System.out.println("teacher ge id ek : " + userid);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM question INNER JOIN teacher ON question.accept_teacher_id= teacher.teacher_id INNER JOIN user ON question.user_id= user.user_id INNER join question_media on question.question_id = question_media.question_id WHERE (question.status=1 OR question.status=2) AND teacher.user_ID=?;");
            preparedStatement.setInt(1, userid);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("mkkd oi me questions teacher");
            jsonArray = JsonHandler.createJSONArray(resultSet,  "question.question_id", "question_img","question_title","question_description","media", "f_name" , "l_name","pro_pic","question.user_id","question.status", "teacher.user_ID", "question.accept_teacher_id");
            System.out.println("meka empty kypnko: "+ jsonArray.length());
            this.answeringQuestions = new HashMap<>();
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
        Teacher teacher = new Teacher(user.userID);
        teacher.userID = user.userID;
        teacher.name = user.name;
        teacher.email = user.email;
        teacher.cart = user.cart;
        teacher.purchasedContent = user.purchasedContent;
        teacher.questions = user.questions;
        teacher.type = user.type;
        teacher.phone = user.phone;
        teacher.address = user.address;
        teacher.city = user.city;
        teacher.country = user.country;
        teacher.profilePicture = user.profilePicture;
        return teacher;
    }



    public JSONObject teacher_accept_org(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println(requestObject);

        JSONObject jsonObject= new JSONObject();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        try{
            PreparedStatement statement;
            statement= connection.prepareStatement("Select * from organization where user_id=?");
            statement.setInt(1,requestObject.getInt("sender_userid"));
            ResultSet rs= statement.executeQuery();

            System.out.println("weda klaaa1");

            if(rs.next()){
                Integer organization_id= rs.getInt("organization_id");
                statement= connection.prepareStatement("Select * from teacher where user_ID=?");
                statement.setInt(1,id);
                ResultSet rs2= statement.executeQuery();

                System.out.println("weda klaaa2");

                if(rs2.next()){
                    Integer teacher_id= rs2.getInt("teacher_id");
                    statement = connection.prepareStatement("INSERT INTO org_has_teacher (organization_id, teacher_id, status) VALUES (?,?,0) ON DUPLICATE KEY UPDATE status = 0;");
                    statement.setInt(1,organization_id);
                    statement.setInt(2,teacher_id);
                    Integer num= statement.executeUpdate();
                    System.out.println("weda klaaa3");

                    statement = connection.prepareStatement("UPDATE org_teacher_request SET status=2 where organization_id=? && teacher_id=?");
                    statement.setInt(1,organization_id);
                    statement.setInt(2,teacher_id);
                    Integer num2= statement.executeUpdate();
                    System.out.println("weda klaaa4");

                    statement = connection.prepareStatement("UPDATE notification SET status=1 WHERE notification_id=?");
                    statement.setInt(1,requestObject.getInt("notification_id"));
                    Integer num3= statement.executeUpdate();
                    System.out.println("weda klaaa6");


                    Date date = Date.valueOf(currentDate);
                    Time time = Time.valueOf(currentTime);
                    String message = "accept your request to join with your organization";
                    statement = connection.prepareStatement("INSERT INTO notification (date, time, message, user_id_sender, user_id_receiver, status, type) VALUES (?,?,?,?,?,0,2);");
                    statement.setDate(1, date);
                    statement.setTime(2, time);
                    statement.setString(3,message);
                    statement.setInt(4,requestObject.getInt("sender_userid"));
                    statement.setInt(4,id);
                    Integer num4= statement.executeUpdate();
                    System.out.println("weda klaaa67");
                    JSONObject notificationObject = new JSONObject();
                    jsonObject.put("date", date);
                    jsonObject.put("time", time);
                    jsonObject.put("message", message);
                    jsonObject.put("user_id_sender", this.userID);
                    jsonObject.put("user_id_receiver", id);
                    jsonObject.put("type", type);
                    System.out.println(jsonObject.toString());
                    System.out.println(id);
                    System.out.println(this.userID);

                    if (!ServerData.users.containsKey(id)){
                        System.out.println("receiver is offline");
                    }
                    else{
                        User receiver = (User) ServerData.users.get(id);
                        receiver.notificationSession.getAsyncRemote().sendText(jsonObject.toString());
                    }

                    jsonObject.put("message", "Request accepted successfully");
                    return jsonObject;

                }
            }
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }


    public JSONObject teacher_delete_org_request(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println(requestObject);

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement= connection.prepareStatement("Select * from organization where user_id=?");
            statement.setInt(1,requestObject.getInt("sender_userid"));
            ResultSet rs= statement.executeQuery();

            System.out.println("weda klaaa1");

            if(rs.next()){
                Integer organization_id= rs.getInt("organization_id");
                statement= connection.prepareStatement("Select * from teacher where user_ID=?");
                statement.setInt(1,id);
                ResultSet rs2= statement.executeQuery();

                System.out.println("weda klaaa2");

                if(rs2.next()){
                    Integer teacher_id= rs2.getInt("teacher_id");
                    statement = connection.prepareStatement("UPDATE org_teacher_request SET status=1 where organization_id=? && teacher_id=?");
                    statement.setInt(1,organization_id);
                    statement.setInt(2,teacher_id);
                    Integer num2= statement.executeUpdate();
                    System.out.println("weda klaaa4");

                    statement = connection.prepareStatement("UPDATE notification SET status=1 WHERE notification_id=?");
                    statement.setInt(1,requestObject.getInt("notification_id"));
                    Integer num3= statement.executeUpdate();
                    System.out.println("weda klaaa6");
                }
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
            statement = connection.prepareStatement("SELECT content.image as img_src, content.title as title, content.description as description, content.price as price, content.content_id as content_id, content.status as status from course INNER JOIN content on course.content_id= content.content_id INNER JOIN user on content.user_id=user.user_id INNER JOIN teacher on teacher.user_ID= content.user_id where content.user_id=?");
            statement.setInt(1,this.userID);
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
            statement = connection.prepareStatement("SELECT content.image as img_src, content.title as title, content.description as description, content.price as price, content.content_id as content_id, content.status as status from quiz INNER JOIN content on quiz.content_id= content.content_id INNER JOIN user on content.user_id=user.user_id INNER JOIN teacher on teacher.user_ID= content.user_id where content.user_id=?;");
            statement.setInt(1,this.userID);
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
        jsonObject.put("isError", true);
        LocalDate currentDate = LocalDate.now();

        Integer generatedKey = -100;
        Integer generatedKey2 = -100;



        try{
            PreparedStatement statement;

            connection.setAutoCommit(false);
            //create new content
            statement = connection.prepareStatement("INSERT INTO content (user_id, subject_id, date, status, type, price, image, title, description) VALUES (?, ?,?,0,1,?,?,?,?)",Statement.RETURN_GENERATED_KEYS );
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("subject"));
            statement.setDate(3, Date.valueOf(currentDate));
            statement.setInt(4, requestObject.getInt("price"));
            statement.setString(5, requestObject.getString("image"));
            statement.setString(6, requestObject.getString("title"));
            statement.setString(7, requestObject.getString("description"));
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
            statement = connection.prepareStatement("INSERT INTO quiz (content_id, quiz_q_number, duration) VALUES (?,20,30)",Statement.RETURN_GENERATED_KEYS );
            statement.setInt(1, generatedKey);
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
                jsonObject.put("isError", false);
                System.out.println("Update ek lssnt una");
            }
            connection.commit();


    }catch (Exception exception){
        System.out.println(exception);
        try {
            connection.rollback();
        }catch (Exception exception1){
            System.out.println("sys: "+exception);
        }

    }
        finally {
        try {
            connection.close();
        }catch (Exception exception){
            System.out.println("sys: "+exception);
        }

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
            System.out.println(jsonObject);
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
            connection.setAutoCommit(false);
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * FROM teacher WHERE user_ID=?");
            statement.setInt(1,id);
            ResultSet rs= statement.executeQuery();

            if(rs.next()){
                Integer teacher_id= rs.getInt("teacher_id");

                statement = connection.prepareStatement("INSERT INTO org_teacher_request (teacher_id, organization_id, status, type) VALUES (?,?,0,1) ON DUPLICATE KEY UPDATE status = 0, type=1;");
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

                    Date date = Date.valueOf(currentDate);
                    Time time = Time.valueOf(currentTime);
                    String message = "send request to join your organization";
                    statement = connection.prepareStatement("INSERT INTO notification (date, time, type, message,user_id_receiver,user_id_sender,status) values (?,?,1,?,?,?,0)");
                    statement.setDate(1, date);
                    statement.setTime(2, time);
                    statement.setString(3, message);
                    statement.setInt(4, organization_userid);
                    statement.setInt(5,id);
                    Integer num2 = statement.executeUpdate();

                    connection.commit();

                    System.out.println("notification eka damma");
                    JSONObject notificationObject = new JSONObject();
                    jsonObject.put("date", date);
                    jsonObject.put("time", time);
                    jsonObject.put("message", message);
                    jsonObject.put("user_id_sender", this.userID);
                    jsonObject.put("user_id_receiver", organization_userid);
                    jsonObject.put("type", type);
                    System.out.println(jsonObject.toString());
                    System.out.println(organization_userid);
                    System.out.println(this.userID);

                    connection.commit();

                    if (!ServerData.users.containsKey(organization_userid)){
                        System.out.println("receiver is offline");
                    }
                    else{
                        User receiver = (User) ServerData.users.get(organization_userid);
                        receiver.notificationSession.getAsyncRemote().sendText(jsonObject.toString());
                    }

                    jsonObject.put("message", "Send request successfully");
                    return jsonObject;
                }

            }



        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }
        jsonObject.put("message", "Send request successfully");
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


    public JSONArray answer_questions(Integer id, JSONObject requestObject){
        System.out.println("meka thma call une");
        JSONArray jsonArray = new JSONArray();
        try {
            System.out.println("view purchase questions answer");
            System.out.println(this.answeringQuestions == null);
            System.out.println("view purchase questions answer");

            if (this.answeringQuestions == null){
                return jsonArray;
            }

            Iterator<Map.Entry<Integer, Question>> iterator = answeringQuestions.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<Integer, Question> entry = iterator.next();
                Question value = entry.getValue();
                JSONObject object = value.data;
                object.put("messages", value.messages);
                System.out.println("json ek: "+ object.toString());
                jsonArray.put(object);
            }

        }catch (Exception exception){
            System.out.println(exception);
        }

        System.out.println(jsonArray.length());
        return jsonArray;

    }


    public JSONArray viewmyorganization(Integer id, JSONObject requestObject){
        System.out.println("sssaaa");
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{

            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT concat(f_name, \" \", l_name) as name, organization.organization_id as organization_id, organization.address as address, user.pro_pic as img_src FROM org_has_teacher INNER JOIN teacher ON org_has_teacher.teacher_id= teacher.teacher_id INNER JOIN organization ON org_has_teacher.organization_id=organization.organization_id INNER JOIN user ON organization.user_id=user.user_id WHERE teacher.user_ID=? and org_has_teacher.status = 0;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            jsonArray = JsonHandler.createJSONArray(rs, "name", "address", "img_src", "organization_id");
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }



    public JSONObject leave_from_org(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("Update org_has_teacher inner join teacher on org_has_teacher.teacher_id= teacher.teacher_id set status=1 where org_has_teacher.organization_id=? && teacher.user_ID=?");
            statement.setInt(1,requestObject.getInt("organization_id"));
            statement.setInt(2,id);
            Integer res_id = statement.executeUpdate();
            System.out.println("meka wada");

            statement = connection.prepareStatement("SELECT * FROM teacher WHERE teacher.user_id=?;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                Integer teacher_id= rs.getInt("teacher_id");
                statement = connection.prepareStatement("UPDATE org_teacher_request SET status=1 WHERE organization_id=? && teacher_id=?;");
                statement.setInt(1,requestObject.getInt("organization_id"));
                statement.setInt(2,teacher_id);
                Integer res_id2 = statement.executeUpdate();
                System.out.println("meka wada");

//                notification part
                statement= connection.prepareStatement("Select * from  teacher where teacher_id=?");
                statement.setInt(1, teacher_id);
                ResultSet rs2= statement.executeQuery();

                if(rs2.next()){
                    Integer teacher_userid= rs2.getInt("user_ID");
                    System.out.println("mekath wada");
                    statement= connection.prepareStatement("insert INTO notification (date, time, message, type, user_id_sender, user_id_receiver,status) VALUES (?,?,'leave from org', 15,?,?,0);");
                    statement.setDate(1, Date.valueOf(currentDate));
                    statement.setTime(2, Time.valueOf(currentTime));
                    statement.setInt(3,this.userID);
                    statement.setInt(4,teacher_userid);
                    Integer num2 = statement.executeUpdate();
                    System.out.println("notification eka yuwa");
                }
            }

            if(res_id==1){
                jsonObject.put("message","Leave successfully");
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


    public JSONObject createCourse(Integer id, JSONObject requestObject){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isError", true);
        Connection connection = Driver.getConnection();
        try{
            System.out.println("create course ekt awa");
            System.out.println(requestObject.toString());




            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement("insert into content (user_id, subject_id, status, type, title, description, image, price) values (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, this.userID);
            statement.setInt(2, requestObject.getInt("courseSubject"));
            statement.setInt(3, 0);
            statement.setInt(4, 0);
            statement.setString(5, requestObject.getString("courseTitle"));
            statement.setString(6, requestObject.getString("courseDescription"));
            statement.setString(7, requestObject.getString("thumbnailPath"));
            statement.setString(8, requestObject.getString("coursePrice"));
            Integer result = statement.executeUpdate();



            Integer generatedKey = -100;
            if (result == 1){
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()){
                    generatedKey = resultSet.getInt(1);
                    System.out.println("key is : "+ generatedKey);
                }
            }


           if (generatedKey<0){
               System.out.println("could not insert to content  table");
               return jsonObject;
           }


            PreparedStatement statement2 = connection.prepareStatement("insert into course (content_id) values (?)", Statement.RETURN_GENERATED_KEYS);
            statement2.setInt(1, generatedKey);


            result = statement2.executeUpdate();


            generatedKey = -100;
            if (result == 1){
                ResultSet resultSet = statement2.getGeneratedKeys();
                if(resultSet.next()){
                    generatedKey = resultSet.getInt(1);
                    System.out.println("key is : "+ generatedKey);
                }
            }



            JSONArray videoTitles = requestObject.getJSONArray("videoTitles");

            System.out.println("video json hri");
            JSONArray videoDescriptions = requestObject.getJSONArray("videoDescriptions");
            System.out.println("description json hri");
            JSONArray videoPaths = requestObject.getJSONArray("videoPaths");

            statement = connection.prepareStatement("insert into course_media (course_id, media, meida_title, media_description) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

            for (int i = 0;i<videoTitles.length();i++){
                statement.setInt(1, generatedKey);
                statement.setString(2, ((JSONObject)videoPaths.get(i)).getString("path"));
                statement.setString(3, videoTitles.getString(i));
                statement.setString(4, videoDescriptions.getString(i));
                statement.addBatch();
            }

            int[] numberOdUpdates = statement.executeBatch();

            if (numberOdUpdates.length == videoTitles.length() &&
                    numberOdUpdates.length == videoDescriptions.length() &&
                    numberOdUpdates.length == videoPaths.length()
            ){
                connection.commit();
                System.out.println("Update ek lssnt una");
                jsonObject.put("isError", "false");
            }




        }catch (Exception exception){
            System.out.println(exception);
            try {
                connection.rollback();
            }catch (Exception exception1){
                System.out.println("sys: "+exception);
            }

        }
        finally {
            try {
                connection.close();
            }catch (Exception exception){
                System.out.println("sys: "+exception);
            }

        }

        return jsonObject;
    }




    public JSONObject add_publish(Integer id, JSONObject requestObject){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isError", true);

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        Connection connection = Driver.getConnection();
        try{
            System.out.println("create course ekt awa");
            System.out.println(requestObject.toString());

            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO advertisment (ad_media, date, time, organization_id, teacher_id) VALUES (?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, requestObject.getString("add"));
            statement.setDate(2, Date.valueOf(currentDate));
            statement.setTime(3, Time.valueOf(currentTime));
            statement.setInt(4, requestObject.getInt("organization_id"));
            statement.setInt(5, requestObject.getInt("userId"));
            Integer result = statement.executeUpdate();
            System.out.println("meka wada klaaa");



            Integer generatedKey = -100;
            if (result == 1){
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()){
                    generatedKey = resultSet.getInt(1);
                    System.out.println("key is : "+ generatedKey);
                }
            }


            if (generatedKey<0){
                System.out.println("could not insert to content  table");
                return jsonObject;
            }



            connection.commit();
            jsonObject.put("isError", false);



        }catch (Exception exception){
            System.out.println(exception);
            try {
                connection.rollback();
            }catch (Exception exception1){
                exception1.printStackTrace();
            }

        }
        finally {
            try {
                connection.close();
            }catch (Exception exception){
                System.out.println("sys: "+exception);
                exception.printStackTrace();
            }

        }

        return jsonObject;
    }



    public JSONObject answer_question(Integer id, JSONObject requestObject){
        System.out.println("wedaaaaaaaaaaaaa");
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();


        try{
            connection.setAutoCommit(false);
            PreparedStatement statement;


            //insert data to question table
            statement = connection.prepareStatement("INSERT INTO question_media (question_id, media) VALUES (?,?);" );
            statement.setInt(1,requestObject.getInt("question_id"));
            statement.setString(2,requestObject.getString("image"));
            Integer result = statement.executeUpdate();
            System.out.println("Hri meka wada");


            connection.commit();
        }


        catch (Exception exception){
            System.out.println(exception);
            try {
                connection.rollback();
            }catch (Exception exception1){
                System.out.println("sys: "+exception);
            }

        }
        finally {
            try {
                connection.close();
            }catch (Exception exception){
                System.out.println("sys: "+exception);
            }

        }

        return jsonObject;
    }




    public JSONObject accept_session(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println(requestObject);

        JSONObject jsonObject= new JSONObject();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        try{
            PreparedStatement statement;
            statement= connection.prepareStatement("Select * from teacher where user_ID=?");
            statement.setInt(1,this.userID);
            ResultSet rs= statement.executeQuery();

            System.out.println("weda klaaa1");

            if(rs.next()){
                Integer teacher_id= rs.getInt("teacher_id");
                statement= connection.prepareStatement("UPDATE question SET status=1, accept_teacher_id= ? WHERE question_id=?");
                statement.setInt(1,teacher_id);
                statement.setInt(2,requestObject.getInt("question_id"));
                Integer num= statement.executeUpdate();

                System.out.println("weda klaaa2");


            }
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }
}
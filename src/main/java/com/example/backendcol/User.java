package com.example.backendcol;

import jakarta.websocket.Session;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Iterator;
import java.util.Map;


import jakarta.servlet.http.Cookie;


public class User extends ApiHandler {

    public Session session;

    public Session notificationSession;

    public Session commentSession;
    public Integer userID ;
    public String name ;
    public String phone;

    public String address;

    public String city;

    public String country;

    public String profilePicture;
    public String email;

    public Integer type;

    public Cart cart;

    public HashMap<Integer,Content> purchasedContent;

    public HashMap<Integer,Question> questions;



    public User(){
        System.out.println("default User called");
    }

    public User(Integer userID, String name, String email, String proPic, String phone, String address, String city, String country){
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.profilePicture = proPic;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.country = country;
        this.cart = new Cart(userID);
        this.purchasedContent = new HashMap<>();
        this.questions = new HashMap<>();



        Connection connection = Driver.getConnection();
        JSONArray jsonArray= new JSONArray();
        try{
            System.out.println("methnd");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT *  from purchase INNER JOIN content on purchase.content_id= content.content_id INNER JOIN course ON content.content_id= course.content_id INNER JOIN user ON content.user_id= user.user_id inner join teacher on content.user_id = teacher.user_ID where purchase.user_id=?;");
            preparedStatement.setInt(1, this.userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("mkkd oi me");
            jsonArray = JsonHandler.createJSONArray(resultSet, "title", "f_name", "l_name", "description","qulification_level","content_id" , "pro_pic","image", "course_id", "rate_count");
            for (int i = 0; i<jsonArray.length() ; i++){
                Course newCourse = new Course(jsonArray.getJSONObject(i));
                PreparedStatement preparedStatement1 = connection.prepareStatement("select * from comments inner join user on user.user_id = comments.user_id where content_id = ?");
                preparedStatement1.setInt(1, jsonArray.getJSONObject(i).getInt("content_id"));
                ResultSet resultSet1 = preparedStatement1.executeQuery();
                JSONArray comments = JsonHandler.createJSONArray(resultSet1, "comments.comment_id", "comments.message", "comments.date", "comments.user_id", "user.f_name", "user.l_name", "user.pro_pic");
                newCourse.data.put("comments", comments);
                purchasedContent.put(newCourse.data.getInt("content_id"),newCourse);
                System.out.println("nidimthai");

            }



            //for quizzes
            preparedStatement = connection.prepareStatement("SELECT *  from purchase INNER JOIN content on purchase.content_id= content.content_id INNER JOIN quiz ON content.content_id= quiz.content_id INNER JOIN user ON content.user_id= user.user_id inner join teacher on content.user_id = teacher.user_ID where purchase.user_id=?;");
            preparedStatement.setInt(1, this.userID);
            resultSet = preparedStatement.executeQuery();
            System.out.println("mkkd oi me quiz");
            jsonArray = JsonHandler.createJSONArray(resultSet, "title", "f_name", "l_name", "description","qulification_level","content_id" , "pro_pic","image", "quiz_id", "rate_count");
            for (int i = 0; i<jsonArray.length() ; i++){
                Quiz newQuiz = new Quiz(jsonArray.getJSONObject(i));
                PreparedStatement preparedStatement1 = connection.prepareStatement("select * from comments inner join user on user.user_id = comments.user_id where content_id = ?");
                preparedStatement1.setInt(1, jsonArray.getJSONObject(i).getInt("content_id"));
                ResultSet resultSet1 = preparedStatement1.executeQuery();
                JSONArray comments = JsonHandler.createJSONArray(resultSet1, "comments.comment_id", "comments.message", "comments.date", "comments.user_id", "user.f_name", "user.l_name", "user.pro_pic");
                System.out.println("comment size ek: " + comments.length());
                newQuiz.data.put("comments", comments);
                purchasedContent.put(newQuiz.data.getInt("content_id"),newQuiz);
                System.out.println("nidimthai");

            }

            //for questions

            preparedStatement = connection.prepareStatement(" SELECT * FROM accept RIGHT JOIN question ON question.question_id=accept.question_id LEFT JOIN question_media ON question.question_id= question_media.question_id LEFT JOIN teacher on question.accept_teacher_id= teacher.teacher_id LEFT JOIN user on teacher.user_ID= user.user_id WHERE question.user_id=?;");
            preparedStatement.setInt(1, this.userID);
            resultSet = preparedStatement.executeQuery();
            System.out.println("mkkd oi me questions");
            jsonArray = JsonHandler.createJSONArray(resultSet,  "question.question_id", "question_img","question_title","question_description", "f_name" , "l_name", "question_media.media", "qulification_level","pro_pic","question.user_id","question.accept_teacher_id","chat_id","status", "teacher.user_ID");
            for (int i = 0; i<jsonArray.length() ; i++){
                Question newQuestion = new Question(jsonArray.getJSONObject(i));
                questions.put( jsonArray.getJSONObject(i).getInt("question.question_id"),newQuestion);
                System.out.println("nidimthai q");
            }



            System.out.println(questions.size());

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

    }





    public JSONArray viewcart(Integer id, JSONObject requestObject){
        JSONArray jsonArray = new JSONArray();
       try {
           System.out.println("viewcart called");
           System.out.println(this.userID);
           jsonArray = cart.getCartDetails(this.userID);

       }
       catch (Exception exception){
           System.out.println(exception);
       }
        return jsonArray;
    }








    public JSONObject viewprofile(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println("DB connectionqq");

        JSONObject jsonObject= new JSONObject();
        try{
            System.out.println("DB connectiontt");
            PreparedStatement statement;
            statement = connection.prepareStatement("Select CONCAT(user.f_name,' ', user.l_name) as name, user.pro_pic as img_src, user.verification_status as veri, user.date_joined as date, student.education_level as level, student.gender as gender from user inner join student on user.user_id=student.user_id where user.user_id=?");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            jsonObject = JsonHandler.createJSONObject(rs, "name", "img_src", "date", "level", "gender","veri");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }




//    public JSONObject viewOrganizationprofile(Integer id, JSONObject requestObject){
//        Connection connection = Driver.getConnection();
//        System.out.println("DB connectionqq");
//
//        JSONObject jsonObject= new JSONObject();
//        try{
//            System.out.println("DB connectiontt");
//            Statement st= connection.createStatement();
//            ResultSet rs= st.executeQuery("SELECT user.pro_pic as img_src, CONCAT(user.f_name, user.l_name) as name, organization.address as address, organization.organization_id as organization_id, organization.tel_no as tel_num from organization INNER JOIN user on organization.user_id= user.user_id where organization.user_id=5;");
//
//            jsonObject = JsonHandler.createJSONObject(rs, "name", "img_src", "address", "tel_num", "organization_id");
//
//        }
//
//        catch(SQLException sqlException){
//            System.out.println(sqlException);
//        }
//
//        return jsonObject;
//    }
//
    public JSONObject view_featured_cont(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println("DB connectionqq");

        JSONObject jsonObject= new JSONObject();

        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT content.title as title, content.image as img_src, subject.name as subject, content.price as price, content.description as description, content.rate_count as rates, content.content_id as content_id, concat(user.f_name,' ', user.l_name) as author, content.date as date from course inner join content on course.content_id= content.content_id INNER join user on content.user_id= user.user_id INNER JOIN subject on content.subject_id= subject.subject_id where course.content_id=?;");
            statement.setInt(1,20);
            ResultSet rs = statement.executeQuery();

            jsonObject = JsonHandler.createJSONObject(rs, "title", "img_src", "price", "description", "content_id", "author", "date", "subject","rates");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }


    public JSONObject delete_cart(Integer id, JSONObject requestObject){
        System.out.println("enne ndd halo");

        JSONObject jsonObject  = new JSONObject();
        jsonObject.put("isError", true);
        try {
            System.out.println("remove cart called");
            System.out.println(this.userID);
            jsonObject = cart.removeItem(this.userID, requestObject);
            jsonObject.put("isError", false);

        }
        catch (Exception exception){
            System.out.println(exception);
        }
        return jsonObject;
    }


    public JSONObject addtocart(Integer id, JSONObject requestObject){
        JSONObject jsonObject  = new JSONObject();
        jsonObject.put("isError", true);
        try {
            System.out.println("add to cart called");
            System.out.println(this.userID);
            jsonObject = cart.addItem(this.userID, requestObject);

        }
        catch (Exception exception){
            System.out.println(exception);
            jsonObject.put("isError", false);
        }
        return jsonObject;
    }


    public JSONArray vieworganization(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT concat(user.f_name,' ', user.l_name) as name, organization.address as address, user.pro_pic as img_src, organization.organization_id as organization_id from user INNER JOIN organization on organization.user_id= user.user_id where user.status=0;");
            ResultSet rs = statement.executeQuery();

            jsonArray = JsonHandler.createJSONArray(rs, "name", "address", "img_src", "organization_id");
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }

    public JSONObject vieworganizationprofile(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT concat(user.f_name, user.l_name) as name, organization.address as address, user.pro_pic as img_src, organization.organization_id as organization_id, organization.tel_no as tel_num from user INNER JOIN organization on organization.user_id= user.user_id where organization.organization_id=?;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            jsonObject = JsonHandler.createJSONObject(rs, "name", "address", "img_src", "organization_id", "tel_num");
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }


    public JSONArray ViewCont_list(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT content.title as title, content.image as img_src, content.rate_count as rate_count, content.price as price,content.content_id as content_id, concat(user.f_name,' ', user.l_name) as author from course inner join content on course.content_id= content.content_id INNER join user on content.user_id= user.user_id where content.status=0;");
            ResultSet rs = statement.executeQuery();

            jsonArray = JsonHandler.createJSONArray(rs, "img_src", "title" , "price", "author", "content_id","rate_count");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }



    public JSONArray Vieworg_teacher(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("select concat(user.f_name,' ', user.l_name) as name, teacher.qulification_level as quli, user.user_id as user_id, teacher.teacher_id as teacher_id, user.pro_pic as img_src from teacher INNER JOIN org_has_teacher on org_has_teacher.teacher_id= teacher.teacher_id INNER JOIN user on teacher.user_ID= user.user_id where org_has_teacher.status=0 && org_has_teacher.organization_id=? && user.status=0;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "name", "quli", "user_id", "teacher_id", "img_src");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }


    public JSONArray teacher_course(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("select content.title as title, concat(user.f_name,' ',user.l_name) as author, content.type as type, content.rate_count as rate_count, content.image as img_src, content.price as price , content.content_id as content_id from teacher INNER JOIN user on teacher.user_ID= user.user_id INNER JOIN content on teacher.user_ID= content.user_id INNER JOIN course on content.content_id=course.content_id where teacher.user_ID=? && content.status=0;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "author", "title", "price", "img_src", "content_id","rate_count", "type");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }




    //meka hari
    public JSONObject editProfile(Integer id, JSONObject requestObject){


        JSONObject jsonObject = new JSONObject();
        Connection connection = Driver.getConnection();
        try{

            //JDBC part
            PreparedStatement statement = connection.prepareStatement("UPDATE user SET f_name = ?, l_name = ? WHERE user_id = ?");
            PreparedStatement statement1 = connection.prepareStatement("UPDATE student SET education_level = ?, gender = ? WHERE user_id = ?");
            statement.setString(1,requestObject.getString("fName"));
            statement.setString(2,requestObject.getString("lName"));
            statement1.setString(1,requestObject.getString("edu"));
            statement1.setString(2,requestObject.getString("gender"));
            System.out.println(requestObject.getString("edu"));
            System.out.println(requestObject.getString("gender"));

            statement1.setInt(3,id);
            statement.setInt(3,id);
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

            this.name = requestObject.getString("fName") + requestObject.getString("lName");
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



    //meka omkay
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
                System.out.println("wedoooo");
                return jsonObject;
            }



        }catch (SQLException sqlException){
            System.out.println(sqlException);
            jsonObject.put("isError", 1);
            jsonObject.put("message", "Invalid user");
            return jsonObject;
        }


    }






    //mek omkay
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
                System.out.println("hari");
                System.out.println(requestObject.getString("newEmail1"));
                if (requestObject.getString("currEmail1").equals(resultSet.getString("email"))){
                    System.out.println("ai bn");
                    PreparedStatement statement = connection.prepareStatement("UPDATE user SET email = ? WHERE user_id = ?");
                    statement.setString(1,requestObject.getString("newEmail1"));
                    statement.setInt(2,id);
                    int resultset = statement.executeUpdate();
                    jsonObject.put("message", "email successfully Updated!");
                    System.out.println("email updated bosa");
                    this.email = requestObject.getString("newEmail1");
                    this.email = requestObject.getString("newEmail1");
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




    //mek omkay
    public JSONArray myQuestions(Integer id, JSONObject requestObject){
        JSONArray jsonArray = new JSONArray();
        try {
            System.out.println("view purchase questions");
            System.out.println(this.questions == null);
            System.out.println("view purchase questions");


            Iterator<Map.Entry<Integer, Question>> iterator = questions.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<Integer, Question> entry = iterator.next();
                Integer key = entry.getKey();
                Question value = entry.getValue();
                JSONObject object = new JSONObject();
                object = value.data;
                object.put("messages", value.messages);
               jsonArray.put(object);
            }

        }catch (Exception exception){
            System.out.println(exception);
        }

        System.out.println(jsonArray.length());
        return jsonArray;
    }





    //mek omkay
    public JSONArray mychats(Integer id, JSONObject requestObject){
        System.out.println("my chatss");
        JSONArray jsonArray = new JSONArray();
        try {
            System.out.println("hellow this is chats");
            System.out.println("view purchase questions");
            System.out.println(this.questions == null);
            System.out.println("view purchase questions");


            Iterator<Map.Entry<Integer, Question>> iterator = questions.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<Integer, Question> entry = iterator.next();
                Integer key = entry.getKey();
                Question value = entry.getValue();
                jsonArray.put(value.data);
            }

        }catch (Exception exception){
            System.out.println(exception);
        }

        System.out.println(jsonArray.length());
        return jsonArray;
    }




    // meka omkay
    public JSONArray myQuizes(Integer id, JSONObject requestObject){
        JSONArray jsonArray = new JSONArray();
        try {
            System.out.println("view purchase quiz");
            System.out.println(this.purchasedContent == null);
            System.out.println("view purchase quiz");
            for (Map.Entry<Integer, Content> entry : purchasedContent.entrySet()) {

                Content content = entry.getValue();
                System.out.println("mkkd aula quiz eke");
                System.out.println(content.getClass().getName());
                System.out.println(content.data.getJSONArray("comments"));
                if (content instanceof Quiz){
                    jsonArray.put(content.data);
                }

            }

//            for (Content content : purchasedContent){
//                System.out.println("mkkd aula quiz eke");
//                System.out.println(content.getClass().getName());
//                System.out.println(content.data.getJSONArray("comments"));
//                if (content instanceof Quiz){
//                    jsonArray.put(content.data);
//                }
//            }

        }catch (Exception exception){
            System.out.println(exception);
        }

        System.out.println(jsonArray.length());
        return jsonArray;
    }




        public JSONArray search_main(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            var name =requestObject.getString("name");
            PreparedStatement statement;
            System.out.println(name);
            System.out.println("pre search");
            statement = connection.prepareStatement("SELECT t.type, t.name, t.img_src, t.quli, t.id, t.course_title, t.quiz_title, t.content_id, t.status, CONCAT(u.f_name, ' ', u.l_name) AS creator,\n" +
                    "CASE WHEN t.type = 'course' THEN c.introduction_media ELSE NULL END AS intro_media,\n" +
                    "CASE WHEN t.type = 'quiz' THEN q.image ELSE NULL END AS quiz_img,\n" +
                    "CASE WHEN t.type = 'teacher' THEN teacher.teacher_id ELSE NULL END AS teacher_id,\n" +
                    "CASE WHEN t.type = 'organization' THEN organization.organization_id ELSE NULL END AS organization_id\n" +
                    "FROM (\n" +
                    "  SELECT 'teacher' AS type, CONCAT(user.f_name, ' ', user.l_name, ' (', teacher.teacher_id, ')') AS name, user.pro_pic AS img_src, teacher.qulification_level AS quli, teacher.teacher_id AS id, NULL AS course_title, NULL AS quiz_title, NULL AS content_id, NULL AS status, teacher.user_ID AS user_id, NULL AS organization_id\n" +
                    "  FROM user \n" +
                    "  INNER JOIN teacher ON teacher.user_ID = user.user_id\n" +
                    "  WHERE CONCAT(user.f_name, user.l_name) LIKE ? \n" +
                    "  UNION ALL\n" +
                    "  SELECT 'course' AS type, NULL AS name, NULL AS img_src, NULL AS quli, course.course_id AS id, course.course_title, NULL AS quiz_title, content.content_id, content.status, content.user_id, NULL AS organization_id\n" +
                    "  FROM course \n" +
                    "  INNER JOIN content ON course.content_id = content.content_id \n" +
                    "  WHERE course.course_title LIKE ? AND content.status = 0\n" +
                    "  UNION ALL\n" +
                    "  SELECT 'quiz' AS type, NULL AS name, NULL AS img_src, NULL AS quli, quiz.quiz_id AS id, NULL AS course_title, quiz.quiz_title, content.content_id, content.status, content.user_id, NULL AS organization_id\n" +
                    "  FROM quiz \n" +
                    "  INNER JOIN content ON quiz.content_id = content.content_id \n" +
                    "  WHERE quiz.quiz_title LIKE ? AND content.status = 0\n" +
                    "  UNION ALL\n" +
                    "  SELECT 'organization' AS type, CONCAT(user.f_name, ' ', user.l_name, ' (', organization.organization_id, ')') AS name, user.pro_pic AS img_src, NULL AS quli, organization.organization_id AS id, NULL AS course_title, NULL AS quiz_title, NULL AS content_id, NULL AS status, organization.user_ID AS user_id, organization.organization_id AS organization_id\n" +
                    "  FROM user \n" +
                    "  INNER JOIN organization ON organization.user_ID = user.user_id\n" +
                    "  WHERE CONCAT(user.f_name, user.l_name) LIKE ? \n" +
                    ") AS t\n" +
                    "LEFT JOIN user AS u ON t.user_id = u.user_id\n" +
                    "LEFT JOIN course AS c ON t.id = c.course_id\n" +
                    "LEFT JOIN quiz AS q ON t.id = q.quiz_id\n" +
                    "LEFT JOIN teacher ON t.id = teacher.teacher_id\n" +
                    "LEFT JOIN organization ON t.id = organization.organization_id;");
            statement.setString(1, "%"+ name +"%");
            statement.setString(2, "%"+ name +"%");
            statement.setString(3, "%"+ name +"%");
            statement.setString(4, "%"+ name +"%");

            ResultSet rs = statement.executeQuery();
            System.out.println(rs);
            jsonArray = JsonHandler.createJSONArray(rs, "type", "name", "img_src", "quli" ,"id", "course_title", "quiz_title", "content_id", "status", "creator", "intro_media", "quiz_img", "teacher_id");
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }





    public JSONObject small_card_open(Integer id, JSONObject requestObject){
        System.out.println("small card eka oprn klaa");
        JSONObject jasonobject = new JSONObject();

        try {
            Connection connection = Driver.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * from course INNER JOIN content on course.content_id=content.content_id INNER JOIN user ON content.user_id=user.user_id inner join teacher on content.user_id = teacher.user_id where content.content_id=?;");
            statement.setInt(1,id);
            ResultSet resultSet = statement.executeQuery();
            jasonobject = JsonHandler.createJSONObject(resultSet, "title", "image","purchase_count", "f_name", "l_name", "description" , "content_id", "price","date","rate_count","content.type", "quiz_time_duration", "quiz_q_Number");
        }catch (Exception exception){
            System.out.println(exception);
        }
        return jasonobject;
    }


    public JSONArray small_card_open_comment(Integer id, JSONObject requestObject){
        JSONArray jasonarray = new JSONArray();

        try {
            Connection connection = Driver.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * from comments inner join user on comments.user_id = user.user_id WHERE comments.content_id = ?;");
            statement.setInt(1,id);
            ResultSet resultSet = statement.executeQuery();
            jasonarray = JsonHandler.createJSONArray(resultSet, "message", "user_id", "f_name", "l_name", "pro_pic" , "date");
        }catch (Exception exception){
            System.out.println(exception);
        }


        return jasonarray;
    }




    public JSONObject search_quiz_open(Integer id, JSONObject requestObject){
        JSONObject jasonobject = new JSONObject();

        try {
            Connection connection = Driver.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * from quiz INNER JOIN content on quiz.content_id=content.content_id INNER JOIN user ON content.user_id=user.user_id inner join teacher on content.user_id = teacher.user_id where content.content_id=?;");
            statement.setInt(1,id);
            ResultSet resultSet = statement.executeQuery();
            jasonobject = JsonHandler.createJSONObject(resultSet, "quiz_title", "image", "f_name", "l_name", "description" , "content_id","date");
        }catch (Exception exception){
            System.out.println(exception);
        }


        return jasonobject;
    }






//    public JSONArray show_notifications(Integer id, JSONObject requestObject){
//        Connection connection = Driver.getConnection();
//
//        JSONArray jsonArray= new JSONArray();
//        try{
//            PreparedStatement statement;
//            statement = connection.prepareStatement("SELECT n.notification_id, n.title, n.description, n.date, n.time, n.type, n.status, \n" +
//                    "    CONCAT(u.f_name, ' ', u.l_name) AS sender_name, 'user' AS sender_type\n" +
//                    "FROM notification n\n" +
//                    "JOIN user u ON n.user_id_sender = u.user_id\n" +
//                    "WHERE n.user_id_receiver = ? OR n.mod_id_receiver = ? \n" +
//                    "UNION ALL\n" +
//                    "SELECT n.notification_id, n.title, n.description, n.date, n.time, n.type, n.status, \n" +
//                    "    CONCAT(m.f_name, ' (Moderator)') AS sender_name, 'moderator' AS sender_type\n" +
//                    "FROM notification n\n" +
//                    "JOIN moderator m ON n.mod_id_sender = m.moderator_id\n" +
//                    "WHERE n.user_id_receiver = ? OR n.mod_id_receiver = ? ;\n");
//            statement.setInt(1, id);
//            statement.setInt(2, id);
//            statement.setInt(3, id);
//            statement.setInt(4, id);
//
//
//            ResultSet rs = statement.executeQuery();
//            System.out.println(rs);
//            jsonArray = JsonHandler.createJSONArray(rs, "notification_id", "sender_name", "sender_type", "date" ,"time", "title", "description", "type", "status");
//        }
//
//        catch(SQLException sqlException){
//            System.out.println(sqlException);
//        }
//
//        return jsonArray;
//    }






    public JSONObject upgrade_to_teacher(Integer id, JSONObject requestObject){


        JSONObject jsonObject = new JSONObject();
        Connection connection = Driver.getConnection();
        try{

            //JDBC part
            PreparedStatement statement = connection.prepareStatement("UPDATE user SET f_name = ?, l_name = ? WHERE user_id = ?");
            PreparedStatement statement1 = connection.prepareStatement("UPDATE student SET education_level = ?, gender = ? WHERE user_id = ?");
            statement.setString(1,requestObject.getString("fName"));
            statement.setString(2,requestObject.getString("lName"));
            statement1.setString(1,requestObject.getString("edu"));
            statement1.setString(2,requestObject.getString("gender"));
            System.out.println(requestObject.getString("edu"));
            System.out.println(requestObject.getString("gender"));

            statement1.setInt(3,id);
            statement.setInt(3,id);
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







    public JSONArray answer_questions(Integer id, JSONObject requestObject){
        System.out.println(id);
        JSONArray jasonarray = new JSONArray();
        Connection connection = Driver.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM question INNER JOIN teacher ON question.accept_teacher_id= teacher.teacher_id INNER JOIN user ON question.user_id= user.user_id INNER join question_media on question.question_id = question_media.question_id WHERE (question.status=1 OR question.status=2) AND teacher.user_ID=?;");
            System.out.println("yesss");
            statement.setInt(1,id);
            ResultSet resultSet = statement.executeQuery();

            jasonarray = JsonHandler.createJSONArray(resultSet,  "question.question_id", "question_img","question_title","question_description","media", "f_name" , "l_name","pro_pic","question.user_id","question.status");
        }catch (Exception exception){
            System.out.println(exception);
        }


        return jasonarray;
    }









    public JSONArray getQuestions(Integer id, JSONObject requestObject){
        JSONArray jsonArray = new JSONArray();
        Connection connection = Driver.getConnection();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM quiz_question where quiz_id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(resultSet, "question", "op1", "op2", "op3", "op4", "answer", "quiz_qid");
        }catch(Exception exception){
            System.out.println(exception);
        }
        return jsonArray;
    }

    public JSONObject saveAnswer(Integer id, JSONObject requestObject){
        JSONObject jsonObject = new JSONObject();
        Connection connection = Driver.getConnection();
        try{
            PreparedStatement statement = connection.prepareStatement("insert into user_answers_questions values (?,?,?) on duplicate key update given_answer = ?");
            statement.setInt(1, requestObject.getInt("quiz_qid"));
            statement.setInt(2, requestObject.getInt("user_id"));
            statement.setString(3, requestObject.getString("answer"));
            statement.setString(4, requestObject.getString("answer"));
            statement.executeUpdate();

        }catch(Exception exception){
            System.out.println(exception);
        }
        return jsonObject;
    }







    //meka omkay
    public JSONArray myCources(Integer id, JSONObject requestObject){
        JSONArray jsonArray = new JSONArray();
        try {
            System.out.println("view purchase course");
            System.out.println(this.purchasedContent == null);
            System.out.println("view purchase course");
            for (Map.Entry<Integer, Content> entry : purchasedContent.entrySet()) {

                Content content = entry.getValue();
                System.out.println("mkkd aula course eke");
                System.out.println(content.getClass().getName());
                System.out.println(content.data.getJSONArray("comments"));
                if (content instanceof Course){
                    jsonArray.put(content.data);
                }

            }
//            for (Content content : purchasedContent){
//                System.out.println("mkkd aula");
//                System.out.println(content.getClass().getName());
//                if (content instanceof Course){
//                    jsonArray.put(content.data);
//                }
//            }

        }catch (Exception exception){
            System.out.println(exception);
        }

        System.out.println(jsonArray.length());
        return jsonArray;
    }


    public JSONObject addrates(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();

        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * from rates where user_id=? && content_id=?");
            statement.setInt(1,id);
            statement.setInt(2, requestObject.getInt("content_id"));
            ResultSet rs = statement.executeQuery();
            Integer rate= requestObject.getInt("rate_value");

            if(rs.next()){
                statement = connection.prepareStatement("update rates set rate_value=? where user_id=? && content_id=?");
                statement.setInt(2,id);
                statement.setInt(3, requestObject.getInt("content_id"));
                statement.setInt(1, requestObject.getInt("rate_value"));
                Integer num = statement.executeUpdate();
                jsonObject.put("message", String.format("Updated you ratings!", rate));
            }
            else{
                statement = connection.prepareStatement("INSERT into rates values (?,?,?);");
                statement.setInt(1,id);
                statement.setInt(2, requestObject.getInt("content_id"));
                statement.setInt(3, requestObject.getInt("rate_value"));
                Integer num = statement.executeUpdate();
                jsonObject.put("message", String.format("Thank you for you ratings!", rate));
            }


        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }



    public JSONObject report_course(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();

        JSONObject jsonObject2= new JSONObject();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();


        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("INSERT INTO report_course (user_id, course_id, reason, date) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE reason = ?, date=?;");
            statement.setInt(1,id);
            statement.setInt(2, requestObject.getInt("course_id"));
            statement.setString(3, requestObject.getString("reason"));
            statement.setDate(4, Date.valueOf(currentDate));
            statement.setString(5, requestObject.getString("reason"));
            statement.setDate(6, Date.valueOf(currentDate));

            Integer num = statement.executeUpdate();
            jsonObject.put("message", "Report added");



            PreparedStatement statement2;
            statement2= connection.prepareStatement("Select content.user_id, content.title from content inner join course on course.content_id=content.content_id where course_id=?");
            statement2.setInt(1,requestObject.getInt("course_id"));
            ResultSet rs2= statement2.executeQuery();
            Integer content_userid= rs2.getInt("content.user_id");
            String title= rs2.getString("course.title");
            System.out.println("sew");

            Date date = Date.valueOf(currentDate);
            Time time = Time.valueOf(currentTime);
            String message ="report your course " + title;
            statement = connection.prepareStatement("INSERT INTO notification (date,time,message,type, user_id_sender, user_id_receiver) VALUES (?,?,?,11,?,?);");
            statement.setDate(1, date);
            statement.setTime(2, time);
            statement.setString(3, message);
            statement.setInt(4, id);
            statement.setInt(5,content_userid);
            Integer num2 = statement.executeUpdate();
            JSONObject notificationObject = new JSONObject();
            jsonObject.put("date", date);
            jsonObject.put("time", time);
            jsonObject.put("message", message);
            jsonObject.put("user_id_sender", this.userID);
            jsonObject.put("user_id_receiver", content_userid);
            jsonObject.put("type", type);
            System.out.println(jsonObject.toString());
            System.out.println("reciver " + content_userid);
            System.out.println("sender " +this.userID);

            if (!ServerData.users.containsKey(content_userid)){
                System.out.println("receiver is offline");
            }
            else{
                User receiver = (User) ServerData.users.get(content_userid);
                receiver.notificationSession.getAsyncRemote().sendText(jsonObject.toString());
            }

            jsonObject.put("message", "report your course " + title);
            return jsonObject;

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }





    public JSONObject report_comment(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();

        JSONObject jsonObject2= new JSONObject();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("INSERT INTO report_user (user_id, reported_userid, reason, date) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE reason = ?, date=?;");
            statement.setInt(2,id);
            statement.setInt(1, requestObject.getInt("user_id"));
            statement.setString(3, requestObject.getString("reason"));
            statement.setDate(4, Date.valueOf(currentDate));
            statement.setString(5, requestObject.getString("reason"));
            statement.setDate(6, Date.valueOf(currentDate));

            Integer num = statement.executeUpdate();
            jsonObject.put("message", "Report added");


            //notification part
            Date date = Date.valueOf(currentDate);
            Time time = Time.valueOf(currentTime);
            String message ="report your account";
            statement = connection.prepareStatement("INSERT INTO notification (date,time,message,type, user_id_sender, user_id_receiver, status) VALUES (?,?,?,9,?,?,0);");
            statement.setDate(1, date);
            statement.setTime(2, time);
            statement.setInt(3, id);
            statement.setInt(4,requestObject.getInt("user_id"));
            Integer num2 = statement.executeUpdate();
            System.out.println("notifiaction eka giyaa");
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

            jsonObject.put("message", "report your account");
            return jsonObject;

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }






    public JSONArray continue_course(Integer id, JSONObject requestObject){
        System.out.println("cont course1");
        JSONArray jsonArray= new JSONArray();
        Course newCourse = new Course();
        try{
            System.out.println("cont course2");
            for(int i =0 ; i < purchasedContent.size() ;i ++){
                System.out.println("cont course3");
                if (purchasedContent.get(i) instanceof Course && purchasedContent.get(i).data.getInt("content_id") == id){
                    newCourse = new Course((JSONObject) purchasedContent.get(i).data);
                    System.out.println(newCourse.courseMedia.size());
                }
            }
            System.out.println("cont course4");
            if (newCourse.data == null){
                System.out.println("no course media");
                return jsonArray;
            }

            for (int i=0 ; i < newCourse.courseMedia.size() ; i++){
                JSONObject newJsonObject = new JSONObject();
                newJsonObject.put("f_name", newCourse.data.getString("f_name"));
                newJsonObject.put("l_name", newCourse.data.getString("l_name"));
                newJsonObject.put("qulification_level", newCourse.data.getString("qulification_level"));
                newJsonObject.put("pro_pic", newCourse.data.getString("pro_pic"));
                newJsonObject.put("course_media_id",  newCourse.courseMedia.get(i).data);
                jsonArray.put(newJsonObject);
                System.out.println(jsonArray);
            }

        }

        catch(Exception exception){
            System.out.println(exception);
        }

        return jsonArray;
    }








//    public JSONObject editProfileOrg(Integer id, JSONObject requestObject){
//
//
//        JSONObject jsonObject = new JSONObject();
//        Connection connection = Driver.getConnection();
//        try{
//
//            //JDBC part
//            PreparedStatement statement = connection.prepareStatement("UPDATE user SET f_name = ?, l_name = ? WHERE user_id = ?");
//            PreparedStatement statement1 = connection.prepareStatement("UPDATE organization SET address = ?, tel_no = ? WHERE user_id = ?");
//            statement.setString(1,requestObject.getString("fName"));
//            statement.setString(2,requestObject.getString("lName"));
//            statement1.setString(1,requestObject.getString("address"));
//            statement1.setString(2,requestObject.getString("telnum"));
//
//
//            statement1.setInt(3,id);
//            statement.setInt(3,id);
//            int resultSet = statement.executeUpdate();
//            int resultSet1 = statement1.executeUpdate();
//            System.out.println(resultSet);
//            System.out.println(resultSet1);
//
//            if(resultSet1==0 || resultSet == 0){
//                jsonObject.put("message", "Inavlid User!");
//                jsonObject.put("isError", 1);
//                return jsonObject;
//            }
//            System.out.printf("Methnta enkn wed");
//            jsonObject.put("message", "Profile successfully Updated!");
//            jsonObject.put("isError", 0);
//            return jsonObject;
//
//
//        }catch (SQLException sqlException){
//            System.out.println(sqlException);
//            jsonObject.put("message", "Database error!");
//            jsonObject.put("isError", 1);
//            return jsonObject;
//        }
//
//
//    }


//    public JSONObject editProfileTeacher(Integer id, JSONObject requestObject){
//
//
//        JSONObject jsonObject = new JSONObject();
//        Connection connection = Driver.getConnection();
//        try{
//
//            //JDBC part
//            PreparedStatement statement = connection.prepareStatement("UPDATE user SET f_name = ?, l_name = ?, DOB= ? WHERE user_id = ?");
//            PreparedStatement statement1 = connection.prepareStatement("UPDATE teacher SET gender = ? WHERE user_id = ?");
//            statement.setString(1,requestObject.getString("fName"));
//            statement.setString(2,requestObject.getString("lName"));
//            statement.setString(3,requestObject.getString("dob"));
//            statement1.setString(1,requestObject.getString("gender"));
//
//
//
//            statement1.setInt(2,id);
//            statement.setInt(4,id);
//            int resultSet = statement.executeUpdate();
//            int resultSet1 = statement1.executeUpdate();
//            System.out.println(resultSet);
//            System.out.println(resultSet1);
//
//            if(resultSet1==0 || resultSet == 0){
//                jsonObject.put("message", "Inavlid User!");
//                jsonObject.put("isError", 1);
//                return jsonObject;
//            }
//            System.out.printf("Methnta enkn wed");
//            jsonObject.put("message", "Profile successfully Updated!");
//            jsonObject.put("isError", 0);
//            return jsonObject;
//
//
//        }catch (SQLException sqlException){
//            System.out.println(sqlException);
//            jsonObject.put("message", "Database error!");
//            jsonObject.put("isError", 1);
//            return jsonObject;
//        }
//
//
//    }

//    public JSONObject viewteacherprofile(Integer id, JSONObject requestObject){
//        Connection connection = Driver.getConnection();
//
//        JSONObject jsonObject= new JSONObject();
//        try{
//            System.out.println("DB connectiontt");
//            PreparedStatement statement;
//            statement = connection.prepareStatement("SELECT user.pro_pic as img_src, CONCAT(user.f_name,' ', user.l_name) as name, teacher.tag as tag, teacher.qulification_level as quli, teacher.gender as gender, user.user_id as user_id from user INNER JOIN teacher on teacher.user_ID= user.user_id where teacher.user_ID=?;");
//            statement.setInt(1,id);
//            ResultSet rs = statement.executeQuery();
//
//            jsonObject = JsonHandler.createJSONObject(rs, "name", "img_src", "quli", "gender", "user_id","tag");
//
//        }
//
//        catch(SQLException sqlException){
//            System.out.println(sqlException);
//        }
//
//        return jsonObject;
//    }




    public JSONArray teacher_quiz(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("select content.title as title, content.rate_count as rate_count, concat(user.f_name,' ',user.l_name) as author, content.type as type, content.image as img_src, content.price as price , content.content_id as content_id from teacher INNER JOIN user on teacher.user_ID= user.user_id INNER JOIN content on teacher.user_ID= content.user_id INNER JOIN quiz on quiz.content_id=content.content_id where teacher.user_ID=? && content.status=0;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "author", "title", "price", "img_src","content_id","rate_count","type");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }






//    public JSONObject check_user_verification(Integer id, JSONObject requestObject){
//        Connection connection = Driver.getConnection();
//
//        JSONObject jsonObject= new JSONObject();
//        try{
//            PreparedStatement statement;
//
//            statement = connection.prepareStatement("SELECT * from user where user_id=? && verification_status=1");
//            statement.setInt(1,id);
//            ResultSet rs = statement.executeQuery();
//
//            if(rs.next()){
//                jsonObject.put("message", "Can't upgrade. Because pending upgrade to teacher");
//
//            }
//
//            else{
//                jsonObject.put("message", "Can upgrade");
//            }
//
//
//
//
//        }
//
//        catch(SQLException sqlException){
//            System.out.println(sqlException);
//        }
//
//        return jsonObject;
//    }


    public JSONObject publish_question(Integer id, JSONObject requestObject){
        System.out.println("wedaaaaaaaaaaaaa");
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime= LocalTime.now();

        Integer generatedKey = -100;

        try{
            connection.setAutoCommit(false);
            PreparedStatement statement;


            //insert data to question table
            statement = connection.prepareStatement("INSERT INTO question (subject_id, question_title, question_img, date, time, question_description, user_id, status) VALUES (?,?,?,?,?,?,?,0)",Statement.RETURN_GENERATED_KEYS );
            statement.setInt(1,requestObject.getInt("subject"));
            statement.setString(2,requestObject.getString("title"));
            statement.setString(3,requestObject.getString("image"));
            statement.setDate(4, Date.valueOf(currentDate));
            statement.setTime(5, Time.valueOf(currentTime));
            statement.setString(6,requestObject.getString("description"));
            statement.setInt(7,id);
            Integer result = statement.executeUpdate();
            System.out.println("Hri meka wada");

            if (result == 1){
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()){
                    generatedKey = resultSet.getInt(1);
                    System.out.println("key is : "+ generatedKey);
                }
            }


//            //insert data to student send question
//            statement = connection.prepareStatement("INSERT INTO student_send_question (user_id, teacher_id, question_id) VALUES (?,?,?)" );
//            statement.setInt(1,id);
//
//            statement.setInt(3, generatedKey);
//            Integer num = statement.executeUpdate();
//
//            if (num == 1){
//                ResultSet resultSet2 = statement.getGeneratedKeys();
//                if(resultSet2.next()){
//                    generatedKey2 = resultSet2.getInt(1);
//                    System.out.println("key is : "+ generatedKey2);
//                }
//            }


            //get questions array in the request object
            JSONArray teachers = requestObject.getJSONArray("teachers");
            System.out.println("teachers thiyna array eka gaththa");
            System.out.println(teachers.length());



            for (int i = 0;i<teachers.length();i++){
                //get teacher_id relevernt to tag
                statement = connection.prepareStatement("SELECT * FROM teacher where tag=?;" );
                statement.setInt(1, teachers.getJSONObject(i).getInt("tag"));
                ResultSet rs= statement.executeQuery();
                System.out.println("tag ekta adla teachers row eka gaththa");

                if(rs.next()){
                    System.out.println("wedaaaaa2");
                    //insert data to question table
                    Integer teacher_id= rs.getInt("teacher_id");
                    Integer teacher_userid= rs.getInt("user_ID");
                    System.out.println(teacher_id);
                    statement = connection.prepareStatement("INSERT INTO student_send_question (user_id, teacher_id, question_id) VALUES (?,?,?)" );
                    statement.setInt(1, id);
                    statement.setInt(2, teacher_id);
                    statement.setInt(3, generatedKey);
                    Integer num2= statement.executeUpdate();
                    System.out.println("student_send_question table ekata data dmma");



                    //notification part
                    Date date = Date.valueOf(currentDate);
                    Time time = Time.valueOf(currentTime);
                    String message = "send_question request";
                    statement= connection.prepareStatement("INSERT INTO notification (date, time, type, message, user_id_receiver,user_id_sender,status) values (?,?,6,?,?,?,0)");
                    statement.setDate(1, Date.valueOf(currentDate));
                    statement.setTime(2, Time.valueOf(currentTime));
                    statement.setString(3, message);
                    statement.setInt(4,id);
                    statement.setInt(5, teacher_userid);
                    Integer num3 = statement.executeUpdate();
                    System.out.println("notification eka damma");

                    connection.commit();
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

                    jsonObject.put("message","send_question request");

                }
                else{
                    System.out.println("Tag is invalid");
                }
            }

            //live update ek
            System.out.println(this.questions.get(60).data.toString());
            JSONObject newQuestionData = new JSONObject();
            newQuestionData.put("question_media.media", "");
            newQuestionData.put("question.question_id", generatedKey);
            newQuestionData.put("question.user_id", this.userID);
            newQuestionData.put("question_img",requestObject.getString("image"));
            newQuestionData.put("question_description", requestObject.getString("description"));
            newQuestionData.put("messages", new JSONArray());
            newQuestionData.put("question_title",requestObject.getString("title"));
            newQuestionData.put("status", 0);

            System.out.println(newQuestionData.toString());
            Question newQuestion = new Question(newQuestionData);
            this.questions.put(newQuestionData.getInt("question.question_id"), newQuestion);


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





    public JSONObject session_request(Integer id, JSONObject requestObject){

        System.out.println("awaaaaaaaaaaaaaaaaaaaaa");
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime= LocalTime.now();

        try{


            PreparedStatement statement;
            statement = connection.prepareStatement("INSERT IGNORE INTO user_req_session (user_id, teacher_id, question_id) VALUES (?,?,?);");
            statement.setInt(1,id);
            statement.setInt(2, requestObject.getInt("teacher_id"));
            statement.setInt(3, requestObject.getInt("question_id"));
            Integer num = statement.executeUpdate();
            jsonObject.put("message", "Request send successfully");
            System.out.println("inser eka unaa");




            statement = connection.prepareStatement("select * from teacher where teacher_id=?");
            statement.setInt(1, requestObject.getInt("teacher_id"));
            ResultSet rs= statement.executeQuery();

            if(rs.next()){
                Integer userid= rs.getInt("user_ID");
                System.out.println("teacherge user id eka gaththa");

                String message = this.name + " has requested a session";
                //notification part
                PreparedStatement statement3 = connection.prepareStatement("INSERT INTO notification (date, time, type, user_id_receiver, user_id_sender, status, message) VALUES (?,?,5,?,?,0, ?);");
                Date date = Date.valueOf(currentDate);
                Time time = Time.valueOf(currentTime);
                statement3.setDate(1, date);
                statement3.setTime(2, time);
                statement3.setInt(3,userid);
                statement3.setInt(4,this.userID);
                statement3.setString(5,message);
                Integer num2 = statement3.executeUpdate();
                System.out.println("notification eka yuwa");
                JSONObject notificationObject = new JSONObject();
                jsonObject.put("date", date);
                jsonObject.put("time", time);
                jsonObject.put("type", type);
                jsonObject.put("user_id_receiver", userid);
                jsonObject.put("user_id_sender", this.userID);
                jsonObject.put("message", message);
                jsonObject.put("img_src", this.profilePicture);
                System.out.println(jsonObject.toString());
                System.out.println(userid);
                System.out.println(this.userID);

                if (!ServerData.users.containsKey(userid)){
                    System.out.println("receiver is offline");
                }
                else{
                    User receiver = (User) ServerData.users.get(userid);
                    receiver.notificationSession.getAsyncRemote().sendText(jsonObject.toString());
                }



                jsonObject.put("message", "Request send successfully");
                return jsonObject;
            }


        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
            return jsonObject;
        }

        return jsonObject;
    }




    public JSONArray read_notification(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();

        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * FROM notification INNER JOIN user ON notification.user_id_sender= user.user_id WHERE notification.user_id_receiver=? && notification.status=0;");
            statement.setInt(1,id);
            ResultSet rs= statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "date", "time", "f_name","l_name","type","user_id_sender", "pro_pic", "message","notification_id");
            System.out.println("data dunna");
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }



    public JSONArray ad_view(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            System.out.println("ane deiyo");
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * from advertisement WHERE advertisement.organization_id = ? and advertisement.status=1;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            jsonArray = JsonHandler.createJSONArray(rs, "ad_media");
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }


    public JSONArray get_content_media(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            System.out.println("ane deiyo");
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * FROM course_media INNER JOIN course ON course_media.course_id= course.course_id WHERE course.content_id=?");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            jsonArray = JsonHandler.createJSONArray(rs, "media","meida_title","media_description","course_media_id");
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }


    public JSONObject getOrderData(Integer id, JSONObject requestObject){
        System.out.println("get order hash");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isError", true);
        Connection connection = Driver.getConnection();

        try {
            connection.setAutoCommit(false);
            JSONArray content = requestObject.getJSONArray("content");
            if (content.length()==0){
                jsonObject.put("errorMessage", "No content were added to purchase");
                return jsonObject;
            }

            String quaryParameter = "content_id = " + content.getJSONObject(0).getInt("content_id");
            for (int i = 1; i<content.length() ; i++){
                quaryParameter += " or content_id = " + content.getJSONObject(i).getInt("content_id");
            }

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT price from content where "+ quaryParameter);
            System.out.println("SELECT price from content where "  + quaryParameter);

            ResultSet resultSet = preparedStatement.executeQuery();
            Integer totalAmount = 0;
            while(resultSet.next()){
                System.out.println("price : " + resultSet.getInt("price"));
                totalAmount += resultSet.getInt("price");
            }
            System.out.println("iwrai");

            long currentTimeMillis = System.currentTimeMillis();
            System.out.println(currentTimeMillis);

            PreparedStatement preparedStatement1 = connection.prepareStatement("Insert into content_order (user_id, content_id, order_id) values (?, ?, ?)");

            for (int i = 0;i<content.length();i++){
                System.out.println(i);
                preparedStatement1.setInt(1, this.userID);
                preparedStatement1.setInt(2, content.getJSONObject(i).getInt("content_id"));
                preparedStatement1.setLong(3, currentTimeMillis);
                preparedStatement1.addBatch();
            }






            String merahantID     = "1223119";
            String merchantSecret = "Mjc3OTAzNDIzMjM2OTQ1ODc1MzI2NDgzNDY3ODgxMzkwNjgwNDQw";
            double amount         = totalAmount;
            String currency       = "LKR";
            String orderID        = Long.toString(currentTimeMillis);
            String hash =  DigestUtils.md5Hex(
                    (merahantID +
                            orderID +
                            String.format("%.2f", amount) +
                            currency +
                            StringUtils.upperCase(DigestUtils.md5Hex(merchantSecret)))
            ).toUpperCase();

            System.out.println("Generated Hash: " + hash);
            jsonObject.put("hash", hash);
            jsonObject.put("merahantID", merahantID);
            jsonObject.put("amount", amount);
            jsonObject.put("currency", currency);
            jsonObject.put("orderID", orderID);
            jsonObject.put("f_name", this.name.split(" ")[0]);
            jsonObject.put("l_name", this.name.split(" ")[1]);
            System.out.println("phone" + this.phone);
            System.out.println("city" + this.city);
            System.out.println("address" + this.address);
            System.out.println("country" + this.country);
            jsonObject.put("phone", this.phone);
            jsonObject.put("address", this.address);
            jsonObject.put("city", this.city);
            jsonObject.put("country", this.country);
            System.out.println(jsonObject.toString());



            //insert to purchase
            PreparedStatement preparedStatement2 = connection.prepareStatement("insert into purchase (content_id, user_id, date, time) values (?,?,?,?)");

            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime= LocalTime.now();
            Date date = Date.valueOf(currentDate);
            Time time = Time.valueOf(currentTime);
            for (int i = 0;i<content.length();i++){
                System.out.println(i);
                preparedStatement2.setInt(1, content.getJSONObject(i).getInt("content_id"));
                preparedStatement2.setInt(2, this.userID);
                preparedStatement2.setDate(3, date);
                preparedStatement2.setTime(4, time);
                preparedStatement2.addBatch();
            }


            int[] result1 = preparedStatement1.executeBatch();
            int[] result2 = preparedStatement2.executeBatch();

            connection.commit();




            if (result1.length!= content.length()){
                jsonObject.put("errorMessage", "Unknown error occurred");
                return jsonObject;
            }


            if (result2.length!= content.length()){
                jsonObject.put("errorMessage", "Unknown error occurred");
                return jsonObject;
            }
            connection.commit();
            jsonObject.put("isError", false);
            jsonObject.put("errorMessage", "Item purchased successfully");

            for (int i = 0; i<content.length(); i++){
                System.out.println("content ek add kr");
                Content newContent = new Content(content.getJSONObject(i).getInt("content_id"));
                purchasedContent.put(content.getJSONObject(i).getInt("content_id"), newContent);
            }
            return jsonObject;

        }

        catch(BatchUpdateException exception){
            System.out.println(exception);
            jsonObject.put("errorMessage", "You have already purchased this item");
            return jsonObject;
        }

        catch (Exception exception){
            System.out.println(exception);
        }
        finally {
           try {
               connection.close();
           }catch (Exception exception){
               System.out.println("could not close the database connection");
           }
        }

        return jsonObject;




//
//
//        String merahantID     = "1223119";
//        String merchantSecret = "Mjc3OTAzNDIzMjM2OTQ1ODc1MzI2NDgzNDY3ODgxMzkwNjgwNDQw";
//        String orderID        = "12345";
//        double amount         = 1000.00;
//        String currency       = "LKR";
//        String hash =  DigestUtils.md5Hex(
//                (merahantID +
//                        orderID +
//                        String.format("%.2f", amount) +
//                        currency +
//                        StringUtils.upperCase(DigestUtils.md5Hex(merchantSecret)))
//        ).toUpperCase();
//
//        System.out.println("Generated Hash: " + hash);
//        jsonObject.put("hash", hash);
//        return jsonObject;
    }





    public JSONArray get_content_media_quiz(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            System.out.println("ane deiyo");
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * FROM quiz_media INNER JOIN quiz ON quiz_media.quiz_id= quiz.quiz_id WHERE quiz.content_id=?");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            jsonArray = JsonHandler.createJSONArray(rs, "media","meida_title","media_description","course_media_id");
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }

















    public JSONObject upload_pro_pic(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            System.out.println("ane deiyo");
            PreparedStatement statement;
            statement = connection.prepareStatement("UPDATE user SET pro_pic=? WHERE user_id=?;");
            statement.setString(1,requestObject.getString("image"));
            statement.setInt(2,id);
            Integer num= statement.executeUpdate();

            if (num==1){
                jsonObject.put("isError",1);
            }
            else{
                jsonObject.put("isError",2);
            }
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }

    public JSONObject addComments(Integer id, JSONObject requestObject){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isError", true);
        System.out.println(requestObject.toString());
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("insert into comments (message, date, content_id, user_id) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, requestObject.getString("message"));
            LocalDate currentDate = LocalDate.now();
            Date date = Date.valueOf(currentDate);
            preparedStatement.setDate(2, date);
            preparedStatement.setInt(3, requestObject.getInt("content_id"));
            preparedStatement.setInt(4, this.userID);

            JSONObject comment = new JSONObject();
            comment.put("name", this.name);
            comment.put("date", date);
            comment.put("pro_pic", this.profilePicture);
            comment.put("message", requestObject.getString("message"));
            comment.put("content_id", requestObject.getInt("content_id"));
            System.out.println(this.profilePicture);
            System.out.println(comment.toString());

            int result = preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            Integer generatedId = null;
            if (rs.next()) {
                generatedId = rs.getInt(1);

            }

            if (result>0 && generatedId != null){

                for (Map.Entry<Integer, Object> entry : ServerData.users.entrySet()) {

                    User user = (User) entry.getValue();
                    user.commentSession.getAsyncRemote().sendText(comment.toString());
                    JSONObject newComment = new JSONObject();
                    newComment.put("comments.date", date);
                    newComment.put("comments.message", requestObject.getString("message"));
                    newComment.put("comments.user_id", this.userID);
                    newComment.put("user.pro_pic", this.profilePicture);
                    newComment.put("user.f_name", this.name.split(" ")[0]);
                    newComment.put("user.l_name", this.name.split(" ")[1]);
                    newComment.put("comments.comment_id", generatedId);
                    this.purchasedContent.get(requestObject.getInt("content_id")).
                            data.getJSONArray("comments").put(newComment);
                }

                jsonObject.put("isError", false);

            }
            else{
                jsonObject.put("errorMessage", "Unknown error occurred");
            }

        }catch (Exception exception){
            System.out.println(exception);
        }
        return jsonObject;
    }



}





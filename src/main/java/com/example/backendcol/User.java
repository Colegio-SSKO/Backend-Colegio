package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.servlet.http.Cookie;


public class User extends ApiHandler {
    public Integer userID ;
    public String name ;
    public String email;


    public JSONArray viewpurchaseCourse(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();


        JSONArray jsonArray= new JSONArray();
        try{
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

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("Select course.decription as description, course.introduction_media as img_src , content.content_id as content_id, course.course_title as title, course.price as price, user.date_joined as description2, CONCAT(user.f_name, user.l_name) as author from content inner join cart on cart.content_id= content.content_id inner join course on content.content_id= course.content_id inner join user on content.user_id= user.user_id where cart.user_id=? and cart.status = 0;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            jsonArray = JsonHandler.createJSONArray(rs, "img_src", "description", "title" , "price", "description2", "author", "content_id");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }












//    public JSONArray viewquession(Integer id, JSONObject requestObject){
//        Connection connection = Driver.getConnection();
//        System.out.println("DB connectionqq");
//
//        JSONArray jsonArray= new JSONArray();
//        try{
//            System.out.println("DB connectiontt");
//            Statement st= connection.createStatement();
//            ResultSet rs= st.executeQuery("Select question.question_description as description, question.question_title as img_src , course.course_title as title, question_question_image as price, user.date_joined as description2, CONCAT(user.f_name, user.l_name) as author from content inner join cart on cart.content_id= content.content_id inner join course on content.content_id= course.content_id inner join user on content.user_id= user.user_id where cart.user_id=4;");
//
//            jsonArray = JsonHandler.createJSONArray(rs, "img_src", "description", "title" , "price", "description2", "author");
//
//        }
//
//        catch(SQLException sqlException){
//            System.out.println(sqlException);
//        }
//
//        return jsonArray;
//    }


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




    public JSONObject viewOrganizationprofile(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println("DB connectionqq");

        JSONObject jsonObject= new JSONObject();
        try{
            System.out.println("DB connectiontt");
            Statement st= connection.createStatement();
            ResultSet rs= st.executeQuery("SELECT user.pro_pic as img_src, CONCAT(user.f_name, user.l_name) as name, organization.address as address, organization.organization_id as organization_id, organization.tel_no as tel_num from organization INNER JOIN user on organization.user_id= user.user_id where organization.user_id=5;");

            jsonObject = JsonHandler.createJSONObject(rs, "name", "img_src", "address", "tel_num", "organization_id");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }

    public JSONObject view_featured_cont(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println("DB connectionqq");

        JSONObject jsonObject= new JSONObject();

        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT course.course_title as title, course.introduction_media as img_src, subject.name as subject, content.price as price, course.decription as description, content.rate_count as rates, content.content_id as content_id, concat(user.f_name,' ', user.l_name) as author, content.date as date from course inner join content on course.content_id= content.content_id INNER join user on content.user_id= user.user_id INNER JOIN subject on content.subject_id= subject.subject_id where course.content_id=?;");
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
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("Update cart set status=1 where user_id=? && content_id=?");
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("content_id"));
            Integer res_id = statement.executeUpdate();


        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }


    public JSONObject addtocart(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * from cart where user_id=? && content_id=? && status=0");
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("content_id"));
            ResultSet rs= statement.executeQuery();

            if(rs.next()){
                jsonObject.put("message","You already added this content");
            }

            else{
                statement = connection.prepareStatement("SELECT * from cart where user_id=? && content_id=? && status=1");
                statement.setInt(1,id);
                statement.setInt(2,requestObject.getInt("content_id"));
                ResultSet rs2= statement.executeQuery();

                if(rs2.next()){
                    jsonObject.put("message","Added to cart");
                    statement = connection.prepareStatement("Update cart set status=0 where user_id=? && content_id=?");
                    statement.setInt(1,id);
                    statement.setInt(2,requestObject.getInt("content_id"));
                    Integer num= statement.executeUpdate();
                }
                else{
                    jsonObject.put("message","Added to cart");
                    PreparedStatement statement2;
                    statement2 = connection.prepareStatement("INSERT INTO cart (status, user_id, content_id) values (0,?,?)");
                    statement2.setInt(1,id);
                    statement2.setInt(2,requestObject.getInt("content_id"));
                    Integer res_id = statement2.executeUpdate();
                }

            }
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
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
            statement = connection.prepareStatement("SELECT course.course_title as title, course.introduction_media as img_src, content.rate_count as rate_count, content.price as price,content.content_id as content_id, concat(user.f_name,' ', user.l_name) as author from course inner join content on course.content_id= content.content_id INNER join user on content.user_id= user.user_id where content.status=0;");
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
            statement = connection.prepareStatement("select course.course_title as title, concat(user.f_name,' ',user.l_name) as author, course.introduction_media as img_src, course.price as price , content.content_id as content_id from teacher INNER JOIN user on teacher.user_ID= user.user_id INNER JOIN content on teacher.user_ID= content.user_id INNER JOIN course on content.content_id=course.content_id where teacher.user_ID=? && content.status=0;;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "author", "title", "price", "img_src", "content_id");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }







//    public JSONArray search_teacher(Integer id, JSONObject requestObject){
//        Connection connection = Driver.getConnection();
//
//        JSONArray jsonArray= new JSONArray();
//        try{
//            var name =requestObject.getString("teacher_name");
//            PreparedStatement statement;
//            statement = connection.prepareStatement("SELECT CONCAT(user.f_name, user.l_name) as name, user.pro_pic as img_src, teacher.qulification_level as quli, teacher.teacher_id as teacher_id FROM user INNER JOIN teacher WHERE CONCAT(user.f_name, user.l_name) like ? && teacher.user_ID= user.user_id;");
//            statement.setString(1, "%" + name + "%");
//            ResultSet rs = statement.executeQuery();
//            jsonArray = JsonHandler.createJSONArray(rs, "img_src", "name", "quli", "teacher_id");
//
//        }
//
//        catch(SQLException sqlException){
//            System.out.println(sqlException);
//        }
//
//        return jsonArray;
//    }






//    public JSONObject org_accept_teacher(Integer id, JSONObject requestObject){
//        Connection connection = Driver.getConnection();
//
//        JSONObject jsonObject= new JSONObject();
//        try{
//            PreparedStatement statement;
//            statement = connection.prepareStatement("INSERT INTO org_has_teacher (organization_id, teacher_id, status) VALUES (?, ?, 0);");
//            statement.setInt(1,id);
//            statement.setInt(2,requestObject.getInt("teacher_id"));
//            Integer res_id = statement.executeUpdate();
//
//            statement = connection.prepareStatement("UPDATE org_teacher_request SET status=1 WHERE teacher_id=? && organization_id=?; ");
//            statement.setInt(2,id);
//            statement.setInt(1,requestObject.getInt("teacher_id"));
//            res_id = statement.executeUpdate();
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


//    public JSONObject org_send_request(Integer id, JSONObject requestObject){
//        Connection connection = Driver.getConnection();
//
//        JSONObject jsonObject= new JSONObject();
//        try{
//            PreparedStatement statement;
//            statement = connection.prepareStatement("INSERT into org_req_teacher (teacher_id, organization_id, status) values (?,?,0)");
//            statement.setInt(1,requestObject.getInt("teacher_id"));
//            statement.setInt(2,id);
//            Integer res_id = statement.executeUpdate();
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





//    public JSONObject teacher_send_req(Integer id, JSONObject requestObject){
//        Connection connection = Driver.getConnection();
//
//        JSONObject jsonObject= new JSONObject();
//        jsonObject.put("message","send request successfully");
//        try{
//            PreparedStatement statement;
//            statement = connection.prepareStatement("SELECT * from teacher_req_org where teacher_id=? && organization_id=? && status=2");
//            statement.setInt(1,id);
//            statement.setInt(2,requestObject.getInt("organization_id"));
//            ResultSet rs= statement.executeQuery();
//
//            if(rs.next()){
//                statement = connection.prepareStatement("UPDATE teacher_req_org set status=0 where teacher_id=? && organization_id=?");
//                statement.setInt(1,id);
//                statement.setInt(2,requestObject.getInt("organization_id"));
//                Integer res_id = statement.executeUpdate();
//            }
//
//            else{
//                statement = connection.prepareStatement("SELECT * from teacher_req_org where teacher_id=? && organization_id=? && status=0");
//                statement.setInt(1,id);
//                statement.setInt(2,requestObject.getInt("organization_id"));
//                ResultSet rs2= statement.executeQuery();
//
//                if(rs2.next()){
//                    jsonObject.put("message","You already send request");
//                }
//
//                else{
//                    statement = connection.prepareStatement("SELECT * from org_has_teacher where teacher_id=? && organization_id=? && status=0");
//                    statement.setInt(1,id);
//                    statement.setInt(2,requestObject.getInt("organization_id"));
//                    ResultSet rs3= statement.executeQuery();
//
//                    if(rs3.next()){
//                        jsonObject.put("message","You already a teacher of this organization");
//                    }
//
//                    else{
//                        statement = connection.prepareStatement("INSERT INTO teacher_req_org (status, teacher_id, organization_id) values (0,?,?)");
//                        statement.setInt(1,id);
//                        statement.setInt(2,requestObject.getInt("organization_id"));
//                        Integer res_id = statement.executeUpdate();
//                    }
//
//                }
//            }
//        }
//
//        catch(SQLException sqlException){
//            System.out.println(sqlException);
//        }
//
//        return jsonObject;
//    }



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




    public JSONArray myQuestions(Integer id, JSONObject requestObject){
        System.out.println(id);
        JSONArray jasonarray = new JSONArray();
        Connection connection = Driver.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(" SELECT * FROM accept RIGHT JOIN question ON question.question_id=accept.question_id LEFT JOIN question_media ON question.question_id= question_media.question_id LEFT JOIN teacher on question.accept_teacher_id= teacher.teacher_id LEFT JOIN user on teacher.user_ID= user.user_id WHERE question.user_id=?;");
            System.out.println("aaaa");
            statement.setInt(1,id);
            ResultSet resultSet = statement.executeQuery();

            jasonarray = JsonHandler.createJSONArray(resultSet,  "question.question_id", "question_img","question_title","question_description", "f_name" , "l_name", "question_media.media", "qulification_level","pro_pic","question.user_id","question.accept_teacher_id","chat_id","status");
        }catch (Exception exception){
            System.out.println(exception);
        }


        return jasonarray;
    }




    public JSONArray myQuizes(Integer id, JSONObject requestObject){
        System.out.println("methnt enw");

        JSONArray jasonarray = new JSONArray();

        try {
            Connection connection = Driver.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * from content INNER JOIN purchase on purchase.content_id=content.content_id INNER JOIN quiz ON purchase.content_id=quiz.content_id INNER JOIN user on content.user_id= user.user_id inner join teacher on content.user_id = teacher.user_id where purchase.user_id=?");
            statement.setInt(1,id);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("methnt enw2");
            jasonarray = JsonHandler.createJSONArray(resultSet, "quiz_title", "f_name", "l_name", "description" ,"qulification_level", "content_id", "quiz_id");
        }catch (Exception exception){
            System.out.println(exception);
        }


        return jasonarray;
    }




        public JSONArray search_main(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            var name =requestObject.getString("name");
            PreparedStatement statement;
            System.out.println(name);

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
        JSONObject jasonobject = new JSONObject();

        try {
            Connection connection = Driver.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * from course INNER JOIN content on course.content_id=content.content_id INNER JOIN user ON content.user_id=user.user_id inner join teacher on content.user_id = teacher.user_id where content.content_id=?;");
            statement.setInt(1,id);
            ResultSet resultSet = statement.executeQuery();
            jasonobject = JsonHandler.createJSONObject(resultSet, "course_title", "introduction_media", "f_name", "l_name", "decription" , "content_id", "price","date","rate_count");
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






    public JSONArray show_notifications(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT n.notification_id, n.title, n.description, n.date, n.time, n.type, n.status, \n" +
                    "    CONCAT(u.f_name, ' ', u.l_name) AS sender_name, 'user' AS sender_type\n" +
                    "FROM notification n\n" +
                    "JOIN user u ON n.user_id_sender = u.user_id\n" +
                    "WHERE n.user_id_receiver = ? OR n.mod_id_receiver = ? \n" +
                    "UNION ALL\n" +
                    "SELECT n.notification_id, n.title, n.description, n.date, n.time, n.type, n.status, \n" +
                    "    CONCAT(m.f_name, ' (Moderator)') AS sender_name, 'moderator' AS sender_type\n" +
                    "FROM notification n\n" +
                    "JOIN moderator m ON n.mod_id_sender = m.moderator_id\n" +
                    "WHERE n.user_id_receiver = ? OR n.mod_id_receiver = ? ;\n");
            statement.setInt(1, id);
            statement.setInt(2, id);
            statement.setInt(3, id);
            statement.setInt(4, id);


            ResultSet rs = statement.executeQuery();
            System.out.println(rs);
            jsonArray = JsonHandler.createJSONArray(rs, "notification_id", "sender_name", "sender_type", "date" ,"time", "title", "description", "type", "status");
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }






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







    public JSONArray myCources(Integer id, JSONObject requestObject){
        System.out.println("myssss");
        JSONArray jasonarray = new JSONArray();
        Connection connection = Driver.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * from purchase INNER JOIN content on purchase.content_id= content.content_id INNER JOIN course ON content.content_id= course.content_id INNER JOIN user ON content.user_id= user.user_id inner join teacher on content.user_id = teacher.user_ID where purchase.user_id=?;");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            jasonarray = JsonHandler.createJSONArray(resultSet, "course_title", "f_name", "l_name", "decription","qulification_level","content_id" , "pro_pic","introduction_media", "course_id", "rate_count");
        }catch (Exception exception){
            System.out.println(exception);
        }


        return jasonarray;
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
            statement = connection.prepareStatement("SELECT * from report_course where user_id=? && course_id=?");
            statement.setInt(1,id);
            statement.setInt(2, requestObject.getInt("course_id"));
            ResultSet rs = statement.executeQuery();



            if(rs.next()){
                statement = connection.prepareStatement("update report_course set reason=?, date=? where user_id=? && course_id=?");
                statement.setInt(3,id);
                statement.setDate(2, Date.valueOf(currentDate));
                statement.setInt(4, requestObject.getInt("course_id"));
                statement.setString(1, requestObject.getString("reason"));
                Integer num = statement.executeUpdate();
                jsonObject.put("message","added report successfuly");
            }
            else{
                statement = connection.prepareStatement("INSERT into report_course values (?,?,?,?);");
                statement.setInt(1,id);
                statement.setInt(2, requestObject.getInt("course_id"));
                statement.setString(3, requestObject.getString("reason"));
                statement.setDate(4, Date.valueOf(currentDate));
                Integer num = statement.executeUpdate();
                jsonObject.put("message","added report successfuly");

                PreparedStatement statement2;
                statement2= connection.prepareStatement("Select content.user_id, content.content_id from content inner join course on course.content_id=content.content_id where course_id=?");
                statement2.setInt(1,requestObject.getInt("course_id"));
                ResultSet rs2= statement2.executeQuery();
                jsonObject2 = JsonHandler.createJSONObject(rs2, "user_id", "content_id");
                System.out.println(jsonObject2.getInt("user_id"));
                System.out.println("sew");


                statement = connection.prepareStatement("INSERT INTO notification (title, description, date, time, type, user_id_receiver, user_id_sender, content_id) VALUES (\"Report your content\", \"Purchased user report your published content\", ?, ?,0, ?,?,?);");
                statement.setDate(1, Date.valueOf(currentDate));
                statement.setTime(2, Time.valueOf(currentTime));
                statement.setInt(3, jsonObject2.getInt("user_id"));
                statement.setInt(4,id);
                statement.setInt(5,jsonObject2.getInt("content_id"));
                Integer num2 = statement.executeUpdate();
            }

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }






    public JSONArray continue_course(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();


        JSONArray jsonArray= new JSONArray();
        try{

            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * FROM course_media INNER JOIN course ON course_media.course_id= course.course_id INNER JOIN content ON course.content_id=content.content_id INNER JOIN user ON content.user_id= user.user_id INNER JOIN teacher on teacher.user_ID= user.user_id WHERE course_media.course_id=?;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            jsonArray = JsonHandler.createJSONArray(rs, "meida_title", "media_description", "course_media_id", "f_name", "l_name", "qulification_level","media", "pro_pic");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
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




    public JSONObject editProfileOrg(Integer id, JSONObject requestObject){


        JSONObject jsonObject = new JSONObject();
        Connection connection = Driver.getConnection();
        try{

            //JDBC part
            PreparedStatement statement = connection.prepareStatement("UPDATE user SET f_name = ?, l_name = ? WHERE user_id = ?");
            PreparedStatement statement1 = connection.prepareStatement("UPDATE organization SET address = ?, tel_no = ? WHERE user_id = ?");
            statement.setString(1,requestObject.getString("fName"));
            statement.setString(2,requestObject.getString("lName"));
            statement1.setString(1,requestObject.getString("address"));
            statement1.setString(2,requestObject.getString("telnum"));


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


    public JSONObject editProfileTeacher(Integer id, JSONObject requestObject){


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

    public JSONObject viewteacherprofile(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            System.out.println("DB connectiontt");
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT user.pro_pic as img_src, CONCAT(user.f_name, user.l_name) as name, teacher.qulification_level as quli, teacher.gender as gender, user.user_id as user_id from user INNER JOIN teacher on teacher.user_ID= user.user_id where teacher.user_ID=?;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            jsonObject = JsonHandler.createJSONObject(rs, "name", "img_src", "quli", "gender", "user_id");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }




    public JSONArray teacher_quiz(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("select quiz.quiz_title as title, concat(user.f_name,' ',user.l_name) as author, quiz.image as img_src, content.price as price , content.content_id as content_id from teacher INNER JOIN user on teacher.user_ID= user.user_id INNER JOIN content on teacher.user_ID= content.user_id INNER JOIN quiz on quiz.content_id=content.content_id where teacher.user_ID=? && content.status=0;;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "author", "title", "price", "img_src","content_id");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }



    public JSONObject upgrade_to_organization(Integer id, JSONObject requestObject){
        System.out.println("655555555555555555555555555555555555555");
        Connection connection = Driver.getConnection();
        System.out.println("okkkkkkkkkkkkkkkkkkkkk");

        JSONObject jsonObject= new JSONObject();

        try{
            PreparedStatement statement;

            statement = connection.prepareStatement("Update student SET status=1 WHERE user_id=?;");
            statement.setInt(1,id);
            Integer num= statement.executeUpdate();
            System.out.println("sew");

            statement = connection.prepareStatement("Insert into publisher values (?)");
            statement.setInt(1,id);
            Integer num2= statement.executeUpdate();
            System.out.println("sew1");

            statement = connection.prepareStatement("INSERT INTO organization (user_id, address, tel_no) VALUES (?,?,?);");
            statement.setInt(1,id);
            statement.setString(2,requestObject.getString("address"));
            statement.setInt(3,requestObject.getInt("telnum"));
            Integer num3= statement.executeUpdate();
            System.out.println("sew2");

            statement = connection.prepareStatement("update user set f_name=? ,l_name=?, verification_status=4 WHERE user_id=?; ");
            statement.setString(1,requestObject.getString("fName"));
            statement.setString(2,requestObject.getString("lName"));
            statement.setInt(3,id);
            Integer num4= statement.executeUpdate();
            System.out.println("sew3");

            jsonObject.put("message", "Upgrade account successfully");
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }


    public JSONObject check_user_verification(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;

            statement = connection.prepareStatement("SELECT * from user where user_id=? && verification_status=1");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                jsonObject.put("message", "Can't upgrade. Because pending upgrade to teacher");

            }

            else{
                jsonObject.put("message", "Can upgrade");
            }




        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }


    public JSONObject publish_question(Integer id, JSONObject requestObject){
        System.out.println("wedaaaaaaaaaaaaa");
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        JSONObject jsonObject2= new JSONObject();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime= LocalTime.now();

        Integer generatedKey = -100;




        try{
            PreparedStatement statement;


            //insert data to question table
            statement = connection.prepareStatement("INSERT INTO question (subject_id, question_title, date, time, question_description, user_id, status) VALUES (?,?,?,?,?,?,0)",Statement.RETURN_GENERATED_KEYS );
            statement.setInt(1,requestObject.getInt("subject"));
            statement.setString(2,requestObject.getString("title"));
            statement.setDate(3, Date.valueOf(currentDate));
            statement.setTime(4, Time.valueOf(currentTime));
            statement.setString(5,requestObject.getString("description"));
            statement.setInt(6,id);
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
                    System.out.println(teacher_id);
                    statement = connection.prepareStatement("INSERT INTO student_send_question (user_id, teacher_id, question_id) VALUES (?,?,?)" );
                    statement.setInt(1, id);
                    statement.setInt(2, teacher_id);
                    statement.setInt(3, generatedKey);
                    Integer num2= statement.executeUpdate();
                    System.out.println("student_send_question table ekata data dmma");
                }
                else{
                    System.out.println("Tag is invalid");
                }
            }

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }




}





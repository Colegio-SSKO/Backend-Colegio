package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;




public class User extends ApiHandler {
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
            statement.setInt(1,1);
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
            statement = connection.prepareStatement("Select CONCAT(user.f_name,' ', user.l_name) as name, user.pro_pic as img_src, user.date_joined as date, student.education_level as level, student.gender as gender from user inner join student on user.user_id=student.user_id where user.user_id=?");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            jsonObject = JsonHandler.createJSONObject(rs, "name", "img_src", "date", "level", "gender");

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
            statement = connection.prepareStatement("SELECT course.course_title as title, course.introduction_media as img_src, subject.name as subject, course.price as price, course.decription as description, content.content_id as content_id, concat(user.f_name, user.l_name) as author, content.date as date from course inner join content on course.content_id= content.content_id INNER join user on content.user_id= user.user_id INNER JOIN subject on content.subject_id= subject.subject_id where course.content_id=?;");
            statement.setInt(1,20);
            ResultSet rs = statement.executeQuery();

            jsonObject = JsonHandler.createJSONObject(rs, "title", "img_src", "price", "description", "content_id", "author", "date", "subject");

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
            statement = connection.prepareStatement("SELECT concat(user.f_name,' ', user.l_name) as name, organization.address as address, user.pro_pic as img_src, organization.organization_id as organization_id from user INNER JOIN organization on organization.user_id= user.user_id;");
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
            statement = connection.prepareStatement("SELECT course.course_title as title, course.introduction_media as img_src, course.price as price,content.content_id as content_id, concat(user.f_name, user.l_name) as author from course inner join content on course.content_id= content.content_id INNER join user on content.user_id= user.user_id;");
            ResultSet rs = statement.executeQuery();

            jsonArray = JsonHandler.createJSONArray(rs, "img_src", "title" , "price", "author", "content_id");

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
            statement = connection.prepareStatement("select concat(user.f_name,' ', user.l_name) as name, teacher.qulification_level as quli, user.user_id as user_id, teacher.teacher_id as teacher_id, user.pro_pic as img_src from teacher INNER JOIN org_has_teacher on org_has_teacher.teacher_id= teacher.teacher_id INNER JOIN user on teacher.user_ID= user.user_id where org_has_teacher.status=0 && org_has_teacher.organization_id=?;");
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
            statement = connection.prepareStatement("select course.course_title as title, concat(user.f_name,' ',user.l_name) as author, course.introduction_media as img_src, course.price as price from teacher INNER JOIN user on teacher.user_ID= user.user_id INNER JOIN content on teacher.user_ID= user.user_id INNER JOIN course on content.content_id=course.content_id where teacher.user_ID=?;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "author", "title", "price", "img_src");

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
            PreparedStatement statement = connection.prepareStatement("SELECT * from question INNER join teacher on question.user_id = teacher.user_id INNER join user on user.user_id = teacher.user_ID INNER join question_media on question.question_id = question_media.question_id WHERE question.user_id = ?;");
            System.out.println("aaaa");
            statement.setInt(1,id);
            ResultSet resultSet = statement.executeQuery();

            jasonarray = JsonHandler.createJSONArray(resultSet,  "question_Id", "question_img","question_title","question_description", "f_name" , "l_name","status", "media", "qulification_level","pro_pic", "teacher.user_id");
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
            PreparedStatement statement = connection.prepareStatement("SELECT * from content INNER JOIN purchase on purchase.content_id=content.content_id INNER JOIN quiz ON purchase.content_id=quiz.content_id INNER JOIN user on content.user_id= user.user_id inner join teacher on content.user_id = teacher.user_id where purchase.user_id=? AND quiz.status=0;");
            statement.setInt(1,id);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("methnt enw2");
            jasonarray = JsonHandler.createJSONArray(resultSet, "quiz_title", "f_name", "l_name", "description" ,"qulification_level", "content_id", "quiz_id");
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
            PreparedStatement statement = connection.prepareStatement("SELECT * from purchase INNER JOIN content on purchase.content_id= content.content_id INNER JOIN course ON content.content_id= course.content_id INNER JOIN user ON content.user_id= user.user_id inner JOIN course_media on course.course_id = course_media.course_id inner join teacher on content.user_id = teacher.user_ID where purchase.user_id=?;");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            jasonarray = JsonHandler.createJSONArray(resultSet, "course_title", "f_name", "l_name", "decription", "media" ,"qulification_level","content_id" , "pro_pic");
        }catch (Exception exception){
            System.out.println(exception);
        }


        return jasonarray;
    }





}





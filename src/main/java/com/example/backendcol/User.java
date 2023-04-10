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
            Statement st= connection.createStatement();
            ResultSet rs= st.executeQuery("Select CONCAT(user.f_name, user.l_name) as name, user.pro_pic as img_src, user.date_joined as date, student.education_level as level, student.gender as gender from user inner join student on user.user_id=student.user_id where user.user_id=4");

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
            statement = connection.prepareStatement("SELECT * from cart where user_id=? && content_id=?");
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("content_id"));
            ResultSet rs= statement.executeQuery();

            if(rs.next()){
                jsonObject.put("message","You already added this content");
            }

            else{
                PreparedStatement statement2;
                statement2 = connection.prepareStatement("INSERT INTO cart (status, user_id, content_id) values (0,?,?)");
                statement2.setInt(1,id);
                statement2.setInt(2,requestObject.getInt("content_id"));
                Integer res_id = statement2.executeUpdate();
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




    public JSONObject remove_teacher(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("Update org_has_teacher set status=1 where teacher_id=? && organization_id=?");
            statement.setInt(1,requestObject.getInt("teacher_id"));
            statement.setInt(2,id);
            Integer res_id = statement.executeUpdate();


        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
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


    public JSONArray teacher_org_noti(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("select concat(user.f_name,' ', user.l_name) as name, user.pro_pic as img_src, teacher.teacher_id as teacher_id from teacher INNER JOIN org_teacher_request on org_teacher_request.teacher_id= teacher.teacher_id INNER JOIN user on teacher.user_ID= user.user_id WHERE org_teacher_request.organization_id=? && org_teacher_request.status=0;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "name", "img_src", "teacher_id");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }



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



}





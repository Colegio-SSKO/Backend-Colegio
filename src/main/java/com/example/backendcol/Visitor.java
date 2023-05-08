package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Visitor extends ApiHandler{



    public JSONObject view_featured_cont(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println("DB connectionqq");

        JSONObject jsonObject= new JSONObject();

        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT content.title as title, content.image as img_src, subject.name as subject, content.price as price, content.description as description, content.purchase_count as count, content.rate_count as rates, content.content_id as content_id, concat(user.f_name,' ', user.l_name) as author, content.date as date from course inner join content on course.content_id= content.content_id INNER join user on content.user_id= user.user_id INNER JOIN subject on content.subject_id= subject.subject_id ORDER BY content.purchase_count DESC LIMIT 1;");


            ResultSet rs = statement.executeQuery();

            jsonObject = JsonHandler.createJSONObject(rs, "title", "img_src", "price", "description", "content_id", "author", "date", "subject","rates");

            statement.close();
            connection.close();
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }


    public JSONArray search_main(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            var name =requestObject.getString("name");
            PreparedStatement statement;
            System.out.println(name);
            System.out.println("pre search aa");
            statement = connection.prepareStatement("SELECT\n" +
                    "  t.type,\n" +
                    "  t.name,\n" +
                    "  t.img_src,\n" +
                    "  t.quli,\n" +
                    "  t.id,\n" +
                    "  t.course_title,\n" +
                    "  t.quiz_title,\n" +
                    "  t.content_id,\n" +
                    "  t.status,\n" +
                    "  CONCAT(u.f_name, ' ', u.l_name) AS creator,\n" +
                    "  c.image AS intro_media, -- Retrieve the image from the \"content\" table for courses\n" +
                    "  q.image AS quiz_img -- Retrieve the image from the \"content\" table for quizzes\n" +
                    "FROM (\n" +
                    "  SELECT\n" +
                    "    'teacher' AS type,\n" +
                    "    CONCAT(user.f_name, ' ', user.l_name, ' (', teacher.teacher_id, ')') AS name,\n" +
                    "    user.pro_pic AS img_src,\n" +
                    "    teacher.qulification_level AS quli,\n" +
                    "    teacher.teacher_id AS id,\n" +
                    "    NULL AS course_title,\n" +
                    "    NULL AS quiz_title,\n" +
                    "    NULL AS content_id,\n" +
                    "    NULL AS status,\n" +
                    "    teacher.user_ID AS user_id,\n" +
                    "    NULL AS organization_id\n" +
                    "  FROM user\n" +
                    "  INNER JOIN teacher ON teacher.user_ID = user.user_id\n" +
                    "  WHERE CONCAT(user.f_name, user.l_name) LIKE ? \n" +
                    "  \n" +
                    "  UNION ALL\n" +
                    "  \n" +
                    "  SELECT\n" +
                    "    'course' AS type,\n" +
                    "    NULL AS name,\n" +
                    "    content.image AS img_src,\n" +
                    "    NULL AS quli,\n" +
                    "    course.course_id AS id,\n" +
                    "    content.title AS course_title,\n" +
                    "    NULL AS quiz_title,\n" +
                    "    content.content_id,\n" +
                    "    content.status,\n" +
                    "    content.user_id,\n" +
                    "    NULL AS organization_id\n" +
                    "  FROM course\n" +
                    "  INNER JOIN content ON course.content_id = content.content_id\n" +
                    "  WHERE content.title LIKE ? AND content.status = 0\n" +
                    "  \n" +
                    "  UNION ALL\n" +
                    "  \n" +
                    "  SELECT\n" +
                    "    'quiz' AS type,\n" +
                    "    NULL AS name,\n" +
                    "    content.image AS img_src,\n" +
                    "    NULL AS quli,\n" +
                    "    quiz.quiz_id AS id,\n" +
                    "    NULL AS course_title,\n" +
                    "    content.title AS quiz_title,\n" +
                    "    content.content_id,\n" +
                    "    content.status,\n" +
                    "    content.user_id,\n" +
                    "    NULL AS organization_id\n" +
                    "  FROM quiz\n" +
                    "  INNER JOIN content ON quiz.content_id = content.content_id\n" +
                    "  WHERE content.title LIKE ? AND content.status = 0\n" +
                    "  \n" +
                    "  UNION ALL\n" +
                    "  \n" +
                    "  SELECT\n" +
                    "    'organization' AS type,\n" +
                    "    CONCAT(user.f_name, ' ', user.l_name, ' (', organization.organization_id, ')') AS name,\n" +
                    "    user.pro_pic AS img_src,\n" +
                    "    NULL AS quli,\n" +
                    "    organization.organization_id AS id,\n" +
                    "    NULL AS course_title,\n" +
                    "    NULL AS quiz_title,\n" +
                    "    NULL AS content_id,\n" +
                    "    NULL AS status,\n" +
                    "    organization.user_ID AS user_id,\n" +
                    "    organization.organization_id AS organization_id\n" +
                    "  FROM user\n" +
                    "  INNER JOIN organization ON organization.user_ID = user.user_id\n" +
                    "  WHERE CONCAT(user.f_name, user.l_name) LIKE ?\n" +
                    ") AS t\n" +
                    "LEFT JOIN user AS u ON t.user_id = u.user_id\n" +
                    "LEFT JOIN content AS c ON t.id = c.content_id AND t.type = 'course' -- Join the \"content\" table for courses\n" +
                    "LEFT JOIN content AS q ON t.id = q.content_id AND t.type = 'quiz' -- Join the \"content\" table for quizzes\n" +
                    "LEFT JOIN teacher ON t.id = teacher.teacher_id\n" +
                    "LEFT JOIN organization ON t.id = organization.organization_id;");
            statement.setString(1, "%"+ name +"%");
            statement.setString(2, "%"+ name +"%");
            statement.setString(3, "%"+ name +"%");
            statement.setString(4, "%"+ name +"%");

            ResultSet rs = statement.executeQuery();
            System.out.println(rs);
            jsonArray = JsonHandler.createJSONArray(rs, "type", "name", "img_src", "quli" ,"id", "course_title", "quiz_title", "content_id", "status", "creator", "intro_media", "quiz_img");
        }

        catch(Exception exception){
            System.out.println("ad00");
            exception.printStackTrace();

        }

        return jsonArray;
    }


    public JSONArray ViewCont_list(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT content.title as title, content.image as img_src, content.rate_count as rate_count, content.price as price,content.content_id as content_id, concat(user.f_name,' ', user.l_name) as author from course inner join content on course.content_id= content.content_id INNER join user on content.user_id= user.user_id where content.status=0;");
            ResultSet rs = statement.executeQuery();

            jsonArray = JsonHandler.createJSONArray(rs, "img_src", "title" , "price", "author", "content_id","rate_count");

            statement.close();
        }catch (Exception exception){
            System.out.println(exception);
        }
        finally {
            try
            {
                connection.close();
            }
            catch (Exception exception){
                System.out.println(exception);
            }
        }

        return jsonArray;
    }
}

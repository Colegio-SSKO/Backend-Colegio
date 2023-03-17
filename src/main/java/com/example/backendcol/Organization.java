package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;




public class Organization extends ApiHandler {

    public JSONObject org_send_request(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("INSERT into org_req_teacher (teacher_id, organization_id, status) values (?,?,0)");
            statement.setInt(1,requestObject.getInt("teacher_id"));
            statement.setInt(2,id);
            Integer res_id = statement.executeUpdate();


        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }




    public JSONObject org_accept_teacher(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("INSERT INTO org_has_teacher (organization_id, teacher_id, status) VALUES (?, ?, 0);");
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("teacher_id"));
            Integer res_id = statement.executeUpdate();

            statement = connection.prepareStatement("UPDATE org_teacher_request SET status=1 WHERE teacher_id=? && organization_id=?; ");
            statement.setInt(2,id);
            statement.setInt(1,requestObject.getInt("teacher_id"));
            res_id = statement.executeUpdate();


        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }




    public JSONArray search_teacher(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            var name =requestObject.getString("teacher_name");
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT CONCAT(user.f_name, user.l_name) as name, user.pro_pic as img_src, teacher.qulification_level as quli, teacher.teacher_id as teacher_id FROM user INNER JOIN teacher WHERE CONCAT(user.f_name, user.l_name) like ? && teacher.user_ID= user.user_id;");
            statement.setString(1, "%" + name + "%");
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "img_src", "name", "quli", "teacher_id");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }



}

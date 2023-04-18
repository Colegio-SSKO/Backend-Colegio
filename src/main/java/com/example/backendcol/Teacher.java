package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;




public class Teacher extends ApiHandler {

    public JSONObject teacher_send_req(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        jsonObject.put("message","send request successfully");
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * from teacher_req_org where teacher_id=? && organization_id=? && status=2");
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("organization_id"));
            ResultSet rs= statement.executeQuery();

            if(rs.next()){
                statement = connection.prepareStatement("UPDATE teacher_req_org set status=0 where teacher_id=? && organization_id=?");
                statement.setInt(1,id);
                statement.setInt(2,requestObject.getInt("organization_id"));
                Integer res_id = statement.executeUpdate();
            }

            else{
                statement = connection.prepareStatement("SELECT * from teacher_req_org where teacher_id=? && organization_id=? && status=0");
                statement.setInt(1,id);
                statement.setInt(2,requestObject.getInt("organization_id"));
                ResultSet rs2= statement.executeQuery();

                if(rs2.next()){
                    jsonObject.put("message","You already send request");
                }

                else{
                    statement = connection.prepareStatement("SELECT * from org_has_teacher where teacher_id=? && organization_id=? && status=0");
                    statement.setInt(1,id);
                    statement.setInt(2,requestObject.getInt("organization_id"));
                    ResultSet rs3= statement.executeQuery();

                    if(rs3.next()){
                        jsonObject.put("message","You already a teacher of this organization");
                    }

                    else{
                        jsonObject.put("message","Send request successfully");
                        statement = connection.prepareStatement("INSERT INTO teacher_req_org (status, teacher_id, organization_id) values (0,?,?)");
                        statement.setInt(1,id);
                        statement.setInt(2,requestObject.getInt("organization_id"));
                        Integer res_id = statement.executeUpdate();
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
}





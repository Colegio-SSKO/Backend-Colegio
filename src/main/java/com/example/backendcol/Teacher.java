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



}





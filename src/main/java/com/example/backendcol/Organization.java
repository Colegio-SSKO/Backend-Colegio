package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;


public class Organization extends ApiHandler {

    public JSONObject org_send_request(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();

        JSONObject jsonObject2= new JSONObject();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * FROM teacher_req_org where teacher_req_org.teacher_id=? && teacher_req_org.organization_id=? && status=0");
            statement.setInt(1,requestObject.getInt("teacher_id"));
            statement.setInt(2,id);
            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                jsonObject.put("message","You already have notification from this teacher");
            }
            else{
                statement = connection.prepareStatement("SELECT * FROM org_has_teacher where organization_id=? && teacher_id=? && status=0;");
                statement.setInt(2,requestObject.getInt("teacher_id"));
                statement.setInt(1,id);
                ResultSet rs2 = statement.executeQuery();

                if(rs2.next()){
                    jsonObject.put("message","Already a teacher in your organization");
                }
                else{
                    statement = connection.prepareStatement("SELECT * from org_teacher_request WHERE teacher_id=? && organization_id=? && status=0;");
                    statement.setInt(1,requestObject.getInt("teacher_id"));
                    statement.setInt(2,id);
                    ResultSet rs3 = statement.executeQuery();

                    if(rs3.next()){
                        jsonObject.put("message","You already send a request");
                    }
                    else{
                        statement = connection.prepareStatement("SELECT * from org_teacher_request WHERE teacher_id=? && organization_id=? && status=2;");
                        statement.setInt(1,requestObject.getInt("teacher_id"));
                        statement.setInt(2,id);
                        ResultSet rs4 = statement.executeQuery();

                        if(rs4.next()){
                            jsonObject.put("message","Send request successfully");
                            statement = connection.prepareStatement("UPDATE org_teacher_request SET status=0 where organization_id=? && teacher_id=?;");
                            statement.setInt(2,requestObject.getInt("teacher_id"));
                            statement.setInt(1,id);
                            Integer num= statement.executeUpdate();
                        }
                        else{
                            jsonObject.put("message","Send request successfully");
                            statement = connection.prepareStatement("INSERT INTO org_teacher_request values(?,?,0)");
                            statement.setInt(2,requestObject.getInt("teacher_id"));
                            statement.setInt(1,id);
                            Integer num= statement.executeUpdate();

                            //notification part
                            PreparedStatement statement2;
                            statement2= connection.prepareStatement("Select user_id from teacher where teacher_id=?");
                            statement2.setInt(1,requestObject.getInt("teacher_id"));
                            ResultSet rs5= statement2.executeQuery();
                            jsonObject2 = JsonHandler.createJSONObject(rs5, "user_id");
                            System.out.println(jsonObject2.getInt("user_id"));
                            System.out.println("sew");


                            statement = connection.prepareStatement("INSERT INTO notification (title, description, date, time, type, user_id_receiver, user_id_sender) VALUES (\"Teacher Request\", \"You have a teacher request\", ?, ?,1, ?,?);");
                            statement.setDate(1, Date.valueOf(currentDate));
                            statement.setTime(2, Time.valueOf(currentTime));
                            statement.setInt(3, jsonObject2.getInt("user_id"));
                            statement.setInt(4,id);
                            Integer num2 = statement.executeUpdate();
                        }
                    }
                }
            }
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
            statement = connection.prepareStatement("SELECT * from org_has_teacher where organization_id=? && teacher_id=?");
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("teacher_id"));
            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                statement = connection.prepareStatement("UPDATE org_has_teacher SET status=0 WHERE organization_id=? && teacher_id=?");
                statement.setInt(1,id);
                statement.setInt(2,requestObject.getInt("teacher_id"));
                Integer res_id = statement.executeUpdate();
            }
            else{
                statement = connection.prepareStatement("INSERT INTO org_has_teacher (organization_id, teacher_id, status) VALUES (?, ?, 0);");
                statement.setInt(1,id);
                statement.setInt(2,requestObject.getInt("teacher_id"));
                Integer res_id = statement.executeUpdate();
            }


            statement = connection.prepareStatement("UPDATE teacher_req_org SET status=1 WHERE teacher_id=? && organization_id=?; ");
            statement.setInt(2,id);
            statement.setInt(1,requestObject.getInt("teacher_id"));
            Integer res_id2 = statement.executeUpdate();

            if(res_id2==1){
                jsonObject.put("message","Accept teacher successfully");
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


    public JSONObject org_remove_teacher_req(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("UPDATE teacher_req_org SET status=2 WHERE teacher_id=? && organization_id=?");
            statement.setInt(2,id);
            statement.setInt(1,requestObject.getInt("teacher_id"));
            Integer res_id = statement.executeUpdate();

            if(res_id==1){
                jsonObject.put("message","Remove teacher request successfully");
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




    public JSONArray search_teacher(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            var name =requestObject.getString("teacher_name");
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT CONCAT(user.f_name,' ', user.l_name) as name, user.pro_pic as img_src, teacher.qulification_level as quli, teacher.teacher_id as teacher_id FROM user INNER JOIN teacher WHERE CONCAT(user.f_name, user.l_name) like ? && teacher.user_ID= user.user_id;");
            statement.setString(1, "%" + name + "%");
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "img_src", "name", "quli", "teacher_id");

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

            statement = connection.prepareStatement("SELECT * FROM teacher_req_org where teacher_id=? && organization_id=?");
            statement.setInt(1,requestObject.getInt("teacher_id"));
            statement.setInt(2,id);
            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                statement = connection.prepareStatement("Update teacher_req_org set status=2 where teacher_id=? && organization_id=?");
                statement.setInt(1,requestObject.getInt("teacher_id"));
                statement.setInt(2,id);
                Integer res_id2 = statement.executeUpdate();
            }
            else{
                statement = connection.prepareStatement("Update org_teacher_request set status=2 where teacher_id=? && organization_id=?");
                statement.setInt(1,requestObject.getInt("teacher_id"));
                statement.setInt(2,id);
                Integer res_id2 = statement.executeUpdate();
            }

            if(res_id==1){
                jsonObject.put("message","Remove teacher successfully");
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


    public JSONArray org_view_teacher_req(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("select concat(user.f_name,' ', user.l_name) as name, user.pro_pic as img_src, teacher.teacher_id as teacher_id, teacher.qulification_level as quli from teacher INNER JOIN teacher_req_org on teacher_req_org.teacher_id= teacher.teacher_id INNER JOIN user on teacher.user_ID= user.user_id WHERE teacher_req_org.organization_id=? && teacher_req_org.status=0;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "name", "img_src", "teacher_id","quli");

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonArray;
    }



}
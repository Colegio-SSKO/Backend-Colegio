package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;


public class Organization extends User {

    public static Organization parseOrganization(User user){
        Organization organization = new Organization();
        organization.userID = user.userID;
        organization.name = user.name;
        organization.email = user.email;
        organization.cart = user.cart;
        organization.type  = user.type;
        organization.purchasedContent = user.purchasedContent;
        organization.questions = user.questions;
        return organization;
    }

//    public JSONObject org_send_request(Integer id, JSONObject requestObject){
//        Connection connection = Driver.getConnection();
//
//        JSONObject jsonObject= new JSONObject();
//
//        JSONObject jsonObject2= new JSONObject();
//        LocalDate currentDate = LocalDate.now();
//        LocalTime currentTime = LocalTime.now();
//
//        try{
//            PreparedStatement statement;
//            statement = connection.prepareStatement("SELECT * FROM teacher_req_org where teacher_req_org.teacher_id=? && teacher_req_org.organization_id=? && status=0");
//            statement.setInt(1,requestObject.getInt("teacher_id"));
//            statement.setInt(2,id);
//            ResultSet rs = statement.executeQuery();
//
//            if(rs.next()){
//                jsonObject.put("message","You already have notification from this teacher");
//            }
//            else{
//                statement = connection.prepareStatement("SELECT * FROM org_has_teacher where organization_id=? && teacher_id=? && status=0;");
//                statement.setInt(2,requestObject.getInt("teacher_id"));
//                statement.setInt(1,id);
//                ResultSet rs2 = statement.executeQuery();
//
//                if(rs2.next()){
//                    jsonObject.put("message","Already a teacher in your organization");
//                }
//                else{
//                    statement = connection.prepareStatement("SELECT * from org_teacher_request WHERE teacher_id=? && organization_id=? && status=0;");
//                    statement.setInt(1,requestObject.getInt("teacher_id"));
//                    statement.setInt(2,id);
//                    ResultSet rs3 = statement.executeQuery();
//
//                    if(rs3.next()){
//                        jsonObject.put("message","You already send a request");
//                    }
//                    else{
//                        statement = connection.prepareStatement("SELECT * from org_teacher_request WHERE teacher_id=? && organization_id=? && status=2;");
//                        statement.setInt(1,requestObject.getInt("teacher_id"));
//                        statement.setInt(2,id);
//                        ResultSet rs4 = statement.executeQuery();
//
//                        if(rs4.next()){
//                            jsonObject.put("message","Send request successfully");
//                            statement = connection.prepareStatement("UPDATE org_teacher_request SET status=0 where organization_id=? && teacher_id=?;");
//                            statement.setInt(2,requestObject.getInt("teacher_id"));
//                            statement.setInt(1,id);
//                            Integer num= statement.executeUpdate();
//                        }
//                        else{
//                            jsonObject.put("message","Send request successfully");
//                            statement = connection.prepareStatement("INSERT INTO org_teacher_request values(?,?,0)");
//                            statement.setInt(2,requestObject.getInt("teacher_id"));
//                            statement.setInt(1,id);
//                            Integer num= statement.executeUpdate();
//
//                            //notification part
//                            PreparedStatement statement2;
//                            statement2= connection.prepareStatement("Select user_id from teacher where teacher_id=?");
//                            statement2.setInt(1,requestObject.getInt("teacher_id"));
//                            ResultSet rs5= statement2.executeQuery();
//                            jsonObject2 = JsonHandler.createJSONObject(rs5, "user_id");
//                            System.out.println(jsonObject2.getInt("user_id"));
//                            System.out.println("sew");
//
//
//                            statement = connection.prepareStatement("INSERT INTO notification (title, description, date, time, type, user_id_receiver, user_id_sender) VALUES (\"Teacher Request\", \"You have a teacher request\", ?, ?,1, ?,?);");
//                            statement.setDate(1, Date.valueOf(currentDate));
//                            statement.setTime(2, Time.valueOf(currentTime));
//                            statement.setInt(3, jsonObject2.getInt("user_id"));
//                            statement.setInt(4,id);
//                            Integer num2 = statement.executeUpdate();
//                        }
//                    }
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




    public JSONObject org_accept_teacher(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println(requestObject);
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement= connection.prepareStatement("Select * from teacher where user_ID=?");
            statement.setInt(1,requestObject.getInt("sender_userid"));
            ResultSet rs= statement.executeQuery();

            System.out.println("weda klaaa1");

            if(rs.next()){
                Integer teacher_id= rs.getInt("teacher_id");
                statement= connection.prepareStatement("Select * from organization where user_id=?");
                statement.setInt(1,id);
                ResultSet rs2= statement.executeQuery();

                System.out.println("weda klaaa2");

                if(rs2.next()){
                    Integer organization_id= rs2.getInt("organization_id");
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

                    statement = connection.prepareStatement("INSERT INTO notification (date, time, message, user_id_sender, user_id_receiver, status, type) VALUES (?,?,'accept your request to join with their organization', ?,?,0,4);");
                    statement.setDate(1, Date.valueOf(currentDate));
                    statement.setTime(2, Time.valueOf(currentTime));
                    statement.setInt(3,id);
                    statement.setInt(4,requestObject.getInt("sender_userid"));
                    Integer num4= statement.executeUpdate();
                    System.out.println("weda klaaa67");
                }
            }
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }




    public JSONObject org_send_request(Integer id, JSONObject requestObject){
        System.out.println("athulata awa");
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * FROM organization WHERE user_id=?");
            statement.setInt(1,id);
            ResultSet rs= statement.executeQuery();

            if(rs.next()){
                Integer organization_id= rs.getInt("organization_id");

                statement = connection.prepareStatement("INSERT INTO org_teacher_request (teacher_id, organization_id, status, type) VALUES (?,?,0,0) ON DUPLICATE KEY UPDATE status = 0, type=0;");
                statement.setInt(1,requestObject.getInt("teacher_id"));
                statement.setInt(2,organization_id);
                Integer num = statement.executeUpdate();
                System.out.println("request table eka update una");




                //Notification part

                PreparedStatement statement2;
                statement2= connection.prepareStatement("SELECT * FROM teacher WHERE teacher_id=?");
                statement2.setInt(1,requestObject.getInt("teacher_id"));
                ResultSet rs2= statement2.executeQuery();

                if(rs2.next()){
                    Integer teacher_userid= rs2.getInt("user_ID");
                    System.out.println("user id eka gaththa");


                    statement = connection.prepareStatement("INSERT INTO notification (date, time, type, message,user_id_receiver,user_id_sender,status) values (?,?,3,'organization send request to join with their organization',?,?,0)");
                    statement.setDate(1, Date.valueOf(currentDate));
                    statement.setTime(2, Time.valueOf(currentTime));
                    statement.setInt(3, teacher_userid);
                    statement.setInt(4,id);
                    Integer num2 = statement.executeUpdate();
                    System.out.println("notification eka damma");

                    jsonObject.put("message", "organization send request to join with their organization");
                }

            }



        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }





    public JSONArray search_teacher(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        Integer value;
        JSONArray jsonArray= new JSONArray();
        try{
            var name =requestObject.getString("teacher_name");
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT CONCAT(user.f_name,' ', user.l_name) as name, user.pro_pic as img_src, teacher.qulification_level as quli, teacher.teacher_id as teacher_id FROM user INNER JOIN teacher WHERE CONCAT(user.f_name, user.l_name) like ? && teacher.user_ID= user.user_id;");
            statement.setString(1, "%" + name + "%");
            ResultSet rs = statement.executeQuery();
            jsonArray = JsonHandler.createJSONArray(rs, "img_src", "name", "quli", "teacher_id");


            PreparedStatement statement2;
            statement2= connection.prepareStatement("SELECT * FROM org_teacher_request INNER JOIN organization ON org_teacher_request.organization_id= organization.organization_id WHERE organization.user_id=? && org_teacher_request.teacher_id=? && (org_teacher_request.status=0 || org_teacher_request.status=2);");

            for (int i = 0;i<jsonArray.length();i++){
                Integer teacher_id= jsonArray.getJSONObject(i).getInt("teacher_id");
                statement2.setInt(1,id);
                statement2.setInt(2,teacher_id);
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




    public JSONObject remove_teacher(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("Update org_has_teacher inner join organization on org_has_teacher.organization_id= organization.organization_id set status=1 where org_has_teacher.teacher_id=? && organization.user_id=?");
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


//    public JSONArray org_view_teacher_req(Integer id, JSONObject requestObject){
//        Connection connection = Driver.getConnection();
//
//        JSONArray jsonArray= new JSONArray();
//        try{
//            PreparedStatement statement;
//            statement = connection.prepareStatement("select concat(user.f_name,' ', user.l_name) as name, user.pro_pic as img_src, teacher.teacher_id as teacher_id, teacher.qulification_level as quli from teacher INNER JOIN teacher_req_org on teacher_req_org.teacher_id= teacher.teacher_id INNER JOIN user on teacher.user_ID= user.user_id WHERE teacher_req_org.organization_id=? && teacher_req_org.status=0;");
//            statement.setInt(1,id);
//            ResultSet rs = statement.executeQuery();
//            jsonArray = JsonHandler.createJSONArray(rs, "name", "img_src", "teacher_id","quli");
//
//        }
//
//        catch(SQLException sqlException){
//            System.out.println(sqlException);
//        }
//
//        return jsonArray;
//    }



    @Override
    public JSONObject viewprofile(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT concat(user.f_name, user.l_name) as name, organization.address as address, user.pro_pic as img_src, organization.organization_id as organization_id, organization.tel_no as tel_num from user INNER JOIN organization on organization.user_id= user.user_id where user.user_id=?;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            jsonObject = JsonHandler.createJSONObject(rs, "name", "address", "img_src", "organization_id", "tel_num");
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }



    @Override
    public JSONObject editProfile(Integer id, JSONObject requestObject){


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




    public JSONObject org_delete_teacher_request(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();
        System.out.println(requestObject);

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement= connection.prepareStatement("Select * from teacher where user_ID=?");
            statement.setInt(1,requestObject.getInt("sender_userid"));
            ResultSet rs= statement.executeQuery();

            System.out.println("weda klaaa1");

            if(rs.next()){
                Integer teacher_id= rs.getInt("teacher_id");
                statement= connection.prepareStatement("Select * from organization where user_id=?");
                statement.setInt(1,id);
                ResultSet rs2= statement.executeQuery();

                System.out.println("weda klaaa2");

                if(rs2.next()){
                    Integer organization_id= rs2.getInt("organization_id");
                    statement = connection.prepareStatement("UPDATE org_teacher_request SET status=1 where organization_id=? && teacher_id=?");
                    statement.setInt(1,organization_id);
                    statement.setInt(2,teacher_id);
                    Integer num2= statement.executeUpdate();
                    System.out.println("weda klaaa4");

                    statement = connection.prepareStatement("UPDATE notification SET status=1 WHERE notification_id=?");
                    statement.setInt(1,requestObject.getInt("notification_id"));
                    Integer num3= statement.executeUpdate();
                    System.out.println("weda klaaa6");

                    statement = connection.prepareStatement("INSERT INTO notification (date, time, message, user_id_sender, user_id_receiver, status, type) VALUES (?,?,'accept your request to join with their organization', ?,?,0,4);");
                    statement.setDate(1, Date.valueOf(currentDate));
                    statement.setTime(2, Time.valueOf(currentTime));
                    statement.setInt(3,id);
                    statement.setInt(4,requestObject.getInt("sender_userid"));
                    Integer num4= statement.executeUpdate();
                    System.out.println("weda klaaa67");
                }
            }
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }





}
package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class Student extends User{
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



    public JSONObject upgrade_to_teacher(Integer id, JSONObject requestObject){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isError", true);
        Connection connection = Driver.getConnection();
        try{
            System.out.println("create course ekt awa");
            System.out.println(requestObject.toString());

            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO upgrade_to_teacher (user_id, education_level, certificate,refers) VALUES (?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, requestObject.getInt("userId"));
            statement.setString(2, requestObject.getString("education_level"));
            statement.setString(3, requestObject.getString("certificate"));
            statement.setString(4, requestObject.getString("references"));
            Integer result = statement.executeUpdate();
            System.out.println("meka wada klaaa");



            Integer generatedKey = -100;
            if (result == 1){
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()){
                    generatedKey = resultSet.getInt(1);
                    System.out.println("key is : "+ generatedKey);
                }
            }


            if (generatedKey<0){
                System.out.println("could not insert to content  table");
                return jsonObject;
            }


            PreparedStatement statement2 = connection.prepareStatement("UPDATE user SET verification_status=1 WHERE user_id=?;");
            statement2.setInt(1, requestObject.getInt("userId"));


            result = statement2.executeUpdate();

            connection.commit();
            jsonObject.put("message","Send upgrade to teacher details successfully");



        }catch (Exception exception){
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



}

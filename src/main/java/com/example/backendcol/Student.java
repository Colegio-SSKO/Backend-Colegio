package com.example.backendcol;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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



}

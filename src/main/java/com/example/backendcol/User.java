package com.example.backendcol;

import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class User extends ApiHandler {

    public JSONObject editProfile(Integer id, JSONObject requestObject){
        //delete this
        System.out.println("handle database");
        System.out.println(requestObject.getString("lName"));

        Connection connection = Driver.getConnection();
        try{

            //JDBC part
            PreparedStatement statement = connection.prepareStatement("SELECT user_ID from user where email = ?");


        }catch (SQLException sqlException){
            System.out.println(sqlException);
        }

        //handle database;

        return new JSONObject();
    }



}

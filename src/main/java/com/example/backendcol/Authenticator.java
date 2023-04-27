package com.example.backendcol;

import com.example.backendcol.api.JWT;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.http.Cookie;

public class Authenticator extends ApiHandler{

    public JSONObject signin(Integer id, JSONObject requestObject){
        JSONObject jsonObject = new JSONObject();
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM User WHERE email = ?");
            statement.setString(1, requestObject.getString("email"));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                String enteredPassword = requestObject.getString("password");
                String savedPassword = resultSet.getString("password");
                if(enteredPassword.equals(savedPassword)){
                    jsonObject.put("isError", false);
                    jsonObject.put("message", "Authentication successful");

                    //
                    JWT jwt = new JWT();

                    //setting the header and the payload
                    jwt.putPayload("sub" , resultSet.getInt("user_id"));
                    jwt.putPayload("name" , resultSet.getString("f_name") + " " +resultSet.getString("l_name"));
                    jwt.putPayload("proPic" , resultSet.getString("pro_pic"));
                    jwt.putHeader("alg" , "HS256");
                    jwt.putHeader("typ" , "JWT");


                    //creating the unsigned token
                    jwt.createToken();
                    //signing the token
                    jwt.sign();
                    Cookie cookie = new Cookie("jwtToken", jwt.token);
                    cookie.setHttpOnly(true);
                    cookie.setPath("/");
                    cookie.setMaxAge(18000);
                    response.addCookie(cookie);


                }
                else{
                    jsonObject.put("isError", true);
                    jsonObject.put("message", "Authentication failed. Check the Email and the Password");
                    jsonObject.put("userName", "");
                    jsonObject.put("userID", "");
                    jsonObject.put("userID", "");
                }
            }
            else {
                jsonObject.put("isError", true);
                jsonObject.put("message", "Authentication failed. Not a registered user");
                jsonObject.put("userName", "");
                jsonObject.put("userID", "");
                jsonObject.put("userID", "");
            }



        }catch (Exception exception){
            System.out.println(exception);
        }
        return jsonObject;

    }

}

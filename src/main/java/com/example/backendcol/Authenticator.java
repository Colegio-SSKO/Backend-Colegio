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
            PreparedStatement statement = connection.prepareStatement("select * from user LEFT JOIN teacher on user.user_id = teacher.user_ID LEFT JOIN organization on user.user_id = organization.user_id  WHERE user.email = ? ");
            statement.setString(1, requestObject.getString("email"));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                String enteredPassword = requestObject.getString("password");
                String savedPassword = resultSet.getString("password");
                if(enteredPassword.equals(savedPassword)){
                    jsonObject.put("isError", false);
                    jsonObject.put("message", "Authentication successful");

                    Integer teacherId = resultSet.getInt("teacher.teacher_id");
                    Integer userType;
                    if(resultSet.wasNull()){
                        System.out.println("not a teacher");
                        Integer organizationID = resultSet.getInt("organization.organization_id");

                        if (resultSet.wasNull()){
                            System.out.println("student knk");
                            userType = 1;
                        }
                        else {
                            System.out.println("organization ekak");
                            userType = 3;
                        }
                    }
                    else{
                        System.out.println("teacher knk");
                        userType = 2;
                    }


                    //
                    JWT jwt = new JWT();

                    //setting the header and the payload
                    jwt.putPayload("sub" , resultSet.getInt("user_id"));
                    jwt.putPayload("name" , resultSet.getString("f_name") + " " +resultSet.getString("l_name"));
                    jwt.putPayload("proPic" , resultSet.getString("pro_pic"));
                    jwt.putPayload("acType", userType);
                    jwt.putHeader("alg" , "HS256");
                    jwt.putHeader("typ" , "JWT");


                    //creating the unsigned token
                    jwt.createToken();
                    //signing the token
                    jwt.sign();
                    Cookie cookie = new Cookie("jwtToken", jwt.token);
                    cookie.setHttpOnly(true);
                    cookie.setMaxAge(1800);
                    cookie.setDomain("localhost");
                    cookie.setPath("/");
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


    public JSONObject getUserData(Integer id, JSONObject requestObject){
        JSONObject jsonObject = new JSONObject();
        System.out.println("getData ekt awwa");
        String token = "";
        try {

            Cookie[] cookies = request.getCookies();
            if(cookies == null){
                System.out.println("No cookies");
            }
            else{

                for (Cookie cookie: cookies){
                    if(cookie.getName().equals("jwtToken")){

                        token = cookie.getValue();
                        System.out.println(token);
                        break;
                    }
                }
            }

        }catch (Exception exception){
            System.out.println(exception);
        }
        JWT jwt = new JWT();
        System.out.println("This is the token: " + token);
        jwt.decodeJWT(token);
        jwt.createToken();
        jwt.sign();
        if (!jwt.validate()){
            System.out.println("Invalid token");
        }
        else{
            System.out.println(jwt.payload.getInt("sub"));
            System.out.println(jwt.payload.getString("name"));
            System.out.println(jwt.payload.getString("proPic"));
            System.out.println(jwt.payload.getInt("acType"));
            jsonObject.put("userID",jwt.payload.getInt("sub") );
            jsonObject.put("userName",jwt.payload.getString("name"));
            jsonObject.put("userProPic",jwt.payload.getString("proPic"));
            jsonObject.put("userType",jwt.payload.getInt("acType"));

        }
        return jsonObject;
    }

}

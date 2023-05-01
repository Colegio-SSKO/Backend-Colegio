package com.example.backendcol;

import com.example.backendcol.api.JWT;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.http.Cookie;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.text.DecimalFormat;


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

                    //creating user object
                    User newUser = new User();
                    newUser.userID = resultSet.getInt("user_id");
                    newUser.email = requestObject.getString("email");
                    newUser.name = resultSet.getString("f_name") + " " +resultSet.getString("l_name");
                    ServerData.users.put(newUser.userID, newUser);
                    System.out.println("length: "+ ServerData.users.size());


                    //
                    JWT jwt = new JWT();

                    //setting the header and the payload
                    jwt.putPayload("sub" , newUser.userID);
                    jwt.putPayload("name" , newUser.name);
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
        Integer isTokenPresent;

        JSONObject jsonObject = new JSONObject();
        System.out.println("getData ekt awwa");
        String token = "";
        token = extractToken(request);
        if (token.equals("")){
            isTokenPresent = 0;
        }
        else {
            isTokenPresent = 1;
            JWT jwt = new JWT();
            System.out.println("This is the token: " + token);
            jwt.decodeJWT(token);
            jwt.createToken();
            jwt.sign();
            if (!jwt.validate()){
                System.out.println("Invalid token");
                isTokenPresent = 0;
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
        }
        jsonObject.put("isTokenPresent", isTokenPresent);
        System.out.println("is token: "+ isTokenPresent);
        return jsonObject;
    }



    public JSONObject getToken (Integer id, JSONObject requestObject){
        System.out.println("gettoken function ekt awa");
        JSONObject jsonObject = new JSONObject();
        String token = "";
        token = extractToken(request);
        jsonObject.put("token" , token);
        return jsonObject;

    }

    public String extractToken (HttpServletRequest req){
        String token = "";
        try {

            Cookie[] cookies = req.getCookies();
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
        return token;
    }

    public JSONObject getOrderHash (Integer id, JSONObject requestObject){

        JSONObject jsonObject = new JSONObject();
        String merchantId = requestObject.getString("merchant_id");


        String merahantID     = "1222924";
        String merchantSecret = "Mjc1MzAzMDc3MDc5NjUyOTgxNDI4NTc1ODE1OTUxMzgzODY4NTA===";
        String orderID        = "12345";
        double amount         = 1000;
        String currency       = "LKR";
        DecimalFormat df       = new DecimalFormat("0.00");
        String amountFormatted = df.format(amount);
        String hash    = getMd5(merahantID + orderID + amountFormatted + currency + getMd5(merchantSecret));
        System.out.println("Generated Hash: " + hash);
        jsonObject.put("hash", hash);
        return jsonObject;
    }

    public static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext.toUpperCase();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject logout (Integer id, JSONObject requestObject){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isSuccess", false);
        System.out.println("logout ekt awa");
        try {

            Cookie[] cookies = request.getCookies();
            if(cookies == null){
                System.out.println("No cookies");
            }
            else{


                for (Cookie cookie: cookies){
                    if(cookie.getName().equals("jwtToken")){
                        //removing the user object
                        String token = extractToken(request);
                        JWT jwt = new JWT();
                        jwt.decodeJWT(token);
                        jwt.createToken();
                        jwt.sign();

                        if (!jwt.validate()){
                            System.out.println("invalidToken");
                            return jsonObject;
                        }
                        User removedUser = ServerData.users.remove(jwt.payload.getInt("sub"));
                        System.out.println("Removed user : " + removedUser.userID);
                        Cookie newCookie = new Cookie(cookie.getName(), "");
                        newCookie.setPath("/");
                        newCookie.setMaxAge(-1);
                        newCookie.setHttpOnly(true);

                        newCookie.setDomain("localhost");
                        System.out.println("have cookies");
                        response.addCookie(newCookie);

                        jsonObject.put("isSuccess", true);
                        break;
                    }
                }
            }

        }catch (Exception exception){
            System.out.println(exception);
        }
        return jsonObject;
    }


}

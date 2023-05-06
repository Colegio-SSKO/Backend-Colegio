package com.example.backendcol;

import com.example.backendcol.api.JWT;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Properties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.http.Cookie;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.logging.SocketHandler;


public class Authenticator extends ApiHandler{

    public String hashPassword(String plainTextPassword) throws NoSuchAlgorithmException {
        String password = plainTextPassword;
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public JSONObject signup(Integer id, JSONObject requestObject){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isError", true);
        jsonObject.put("message", "" );
        System.out.println("signup");
        try {


            String hashedPassword = hashPassword(requestObject.getString("password"));
            Connection connection = Driver.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT into user (f_name, l_name, email, password, DOB, city, tel_no, address) values (?, ?, ?, ?, ?,?,?,?)");
            preparedStatement.setString(1, requestObject.getString("fname"));
            preparedStatement.setString(2, requestObject.getString("lname"));
            preparedStatement.setString(3, requestObject.getString("email"));
            preparedStatement.setString(4, hashedPassword);
            preparedStatement.setString(5, "2020-02-01");
            preparedStatement.setString(6, requestObject.getString("city"));
            preparedStatement.setInt(7, requestObject.getInt("tel_num"));
            preparedStatement.setString(8, requestObject.getString("address"));

            int result = preparedStatement.executeUpdate();
            if (result == 0){
                System.out.println("save une ne yako");
                jsonObject.put("message" , "Unknown error happened");
            }
            else {
                System.out.println("save una yako");
                jsonObject.put("isError", false);
                jsonObject.put("message" , "Congratulations! You are in.");
            }


        }
        catch (SQLIntegrityConstraintViolationException exception){
            System.out.println("This email is already taken");
            jsonObject.put("message", "This email is already taken");
        }

        catch (Exception exception){
            System.out.println(exception);
            jsonObject.put("message", "Internal server error");
        }



        return jsonObject;
    }


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


                    Integer userType;

                    System.out.println("onna login ek wed krnna gnna hdnne");
                    Integer teacherId = resultSet.getInt("teacher.teacher_id");
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
                        System.out.println(teacherId);
                        System.out.println("teacher knk");
                        userType = 2;
                    }
                    User newUser = new User(resultSet.getInt("user_id"),
                            resultSet.getString("f_name") + " " +resultSet.getString("l_name"),
                            requestObject.getString("email")
                    );

                    newUser.type = userType;


                    if (userType==2){
                        System.out.println(newUser.userID);
                        Teacher newTeacher = Teacher.parseTeacher(newUser);
                        ServerData.users.put(newUser.userID, newTeacher);

                    }
                    else if(userType == 3){
                        Organization newOrganization = Organization.parseOrganization(newUser);
                        ServerData.users.put(newUser.userID, newOrganization);
                    }
                    else if (userType == 1){
                        ServerData.users.put(newUser.userID, newUser);
                    }

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
                        User removedUser = (User) ServerData.users.remove(jwt.payload.getInt("sub"));
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


    public JSONObject sendOTP(Integer id, JSONObject requestObject){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isError", true);
        jsonObject.put("message", "");
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement statement = connection.prepareStatement("select * from user WHERE user.email = ? ");
            statement.setString(1, requestObject.getString("email"));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                System.out.println("recover user id: " + resultSet.getInt("user_id"));
                Integer userID = resultSet.getInt("user_id");

                //generating OTP
                ServerData.otpSessions.put(userID, new OTP());

                //sending male
                String from = "colegioacc55@gmail.com";
                String password = "dwlysekboczdwmfr";
                String to = "senithkarunarathneu@gmail.com";

                Properties properties = System.getProperties();
                properties.setProperty("mail.smtp.host", "smtp.gmail.com");
                properties.setProperty("mail.smtp.port", "465");
                properties.setProperty("mail.smtp.auth", "true");
                properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

                Session session = Session.getInstance(properties,
                        new jakarta.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(from, password);
                            }
                        });
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                message.setSubject("Reset Password");
                message.setText("Use this OTP to recover your Colegio password. OTP is " + ServerData.otpSessions.get(userID).getOtp() + " You will be asked to enter this.");
                Transport.send(message);
                System.out.println("Sent message successfully....");
                jsonObject.put("isError", false);
                jsonObject.put("message", "OTP sent");
                return jsonObject;

            }

        }
        catch (Exception exception){
            System.out.println(exception);
            jsonObject.put("message", "Unknown error ocured in the server");
        }
        return jsonObject;
    }


    public JSONObject verify(Integer id, JSONObject requestObject){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isError", true);
        jsonObject.put("message", "");
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement statement = connection.prepareStatement("select * from user WHERE user.email = ? ");
            statement.setString(1, requestObject.getString("email"));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                System.out.println("recover user id: " + resultSet.getInt("user_id"));
                Integer userID = resultSet.getInt("user_id");
                OTP otp = ServerData.otpSessions.get(userID);
                if (otp.isValid()){
                    if (otp.verify(requestObject.getString("otp"))){
                        jsonObject.put("isError", false);
                        jsonObject.put("message", "verified!");
                        return jsonObject;
                    }
                    else{
                        jsonObject.put("message", "Incorrect!");
                        return jsonObject;
                    }

                }
                else{
                    jsonObject.put("message", "One time password is expired");
                    return jsonObject;
                }


            }

        }
        catch (Exception exception){
            System.out.println(exception);
            jsonObject.put("message", "Unknown server error");
        }
        return jsonObject;
    }

    public JSONObject changePassword(Integer id, JSONObject requestObject){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isError", true);
        jsonObject.put("message", "");
        try{
            String newPassword = requestObject.getString("password");
            String email = requestObject.getString("email");
            System.out.println("methnt enw");
            String userOTP = requestObject.getString("otp");
            System.out.println("methntath enw");
            Connection connection = Driver.getConnection();
            PreparedStatement statement = connection.prepareStatement("select * from user WHERE user.email = ? ");
            statement.setString(1, requestObject.getString("email"));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                Integer userID = resultSet.getInt("user_id");
                OTP otp = ServerData.otpSessions.get(userID);
                if (otp.authorizeToProceed(userOTP)){
//                  statement.close();
                    statement = connection.prepareStatement("UPDATE user SET password = ? WHERE user.email = ? ");
                    statement.setString(1, newPassword);
                    statement.setString(2, email);

                    Integer result = statement.executeUpdate();
                    if (result>0){
                        jsonObject.put("isError", false);
                        jsonObject.put("message", "Password Changed successfully");
                        return jsonObject;
                    }
                }
                else {
                    jsonObject.put("message", "Unauthorized change password request");
                    System.out.println("invalid request");
                    return jsonObject;
                }

            }
            else{
                jsonObject.put("message", "Unauthorized change password request");
                System.out.println("invalid request");
                return jsonObject;
            }


        }catch (Exception exception){
            System.out.println(exception);
            jsonObject.put("message","unknown error occurred in the server");
        }

        return jsonObject;
    }

}

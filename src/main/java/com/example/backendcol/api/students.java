package com.example.backendcol.api;

import com.example.backendcol.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "students", value = "/api/students/*")
public class students extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        JWT jwt = new JWT();
//        Authenticator authenticator = new Authenticator();
//        System.out.println("user eke inne");
//        String token = authenticator.extractToken(request);
//        jwt.decodeJWT(token);
//        jwt.createToken();
//        jwt.sign();
//        if (!jwt.validate()){
//            System.out.println("Unauthorized resource request");
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("error", "Unauthorized resource request");
//            ApiHandler apiHandler = new ApiHandler();
//            apiHandler.sendResponse(response, jsonObject);
//        }
        Student student = new Student();
        System.out.println("userID ek thmai: " + student.userID);
        RequestsParameters requestsParameters = student.handleRequest(request, response); //save the id and the function
        System.out.println("heloooooooooooooo");
        Object res = student.executeFunction(requestsParameters.getFunction(), requestsParameters.getID(),new JSONObject());        //save the object which return from the calling function

        student.sendResponse(response, res);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Student student = new Student();
        RequestsParameters requestsParameters = student.handleRequest(request, response);    //save the id and the function
        JSONObject jsonObject = JsonHandler.getJSONObject(request);       //save the details which comming from the frontend to the jason object
        Object res = student.executeFunction(requestsParameters.getFunction(), requestsParameters.getID(), jsonObject );        //save the object which return from the calling function
        student.sendResponse(response, res);
    }


}

package com.example.backendcol.api;

import com.example.backendcol.JsonHandler;
import com.example.backendcol.RequestsParameters;
import com.example.backendcol.Student;
import com.example.backendcol.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "users", value = "/api/users/*")
public class users extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        User user = new User();
        RequestsParameters requestsParameters = user.handleRequest(request, response);
        JSONObject jsonObject = JsonHandler.getJSONObject(request);
        Object res = user.executeFunction(requestsParameters.getFunction(), requestsParameters.getID(), jsonObject );
        user.sendResponse(response, res);

    }

}

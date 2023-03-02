package com.example.backendcol.api;

import com.example.backendcol.JsonHandler;
import com.example.backendcol.RequestsParameters;
import com.example.backendcol.Student;
import com.example.backendcol.Teacher;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet(name = "students", value = "/api/students/*")
public class students extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        Student student = new Student();
        RequestsParameters requestsParameters = student.handleRequest(request, response);
        JSONObject jsonObject = JsonHandler.getJSONObject(request);
        Object res = student.executeFunction(requestsParameters.getFunction(), requestsParameters.getID(), jsonObject );
        student.sendResponse(response, res);

//        Teacher teacher = new Teacher();
//        RequestsParameters requestsParameters = teacher.handleRequest(request, response);
//        Object res = teacher.executeFunction(requestsParameters.getFunction(), requestsParameters.getID(), new JSONObject());
//        teacher.sendResponse(response, res);
    }
}

package com.example.backendcol.api;

import com.example.backendcol.ApiHandler;
import com.example.backendcol.RequestsParameters;
import com.example.backendcol.Teacher;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.io.File;




@MultipartConfig(
        location = "C:\\Users\\uni\\Videos\\AnyDesk",
        fileSizeThreshold = 1024 * 1024*1000,
        maxFileSize = 1024 * 1024 * 2*1000,
        maxRequestSize = 1024 * 1024 * 4*1000
)
@WebServlet(name = "teachers", value = "/api/teachers/*")
public class teachers extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("servlet called");


        Teacher teacher = new Teacher();
        RequestsParameters requestsParameters = teacher.handleRequest(request, response);
        Object res = teacher.executeFunction(requestsParameters.getFunction(), requestsParameters.getID(), new JSONObject());
        teacher.sendResponse(response, res);
    }



}

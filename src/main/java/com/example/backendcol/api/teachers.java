package com.example.backendcol.api;

import com.example.backendcol.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet(name = "teachers", value = "/api/teachers/*")
public class teachers extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Teacher teacher = new Teacher();
        RequestsParameters requestsParameters = teacher.handleRequest(request, response); //save the id and the function
        System.out.println("heloooooooooooooo");
        Object res = teacher.executeFunction(requestsParameters.getFunction(), requestsParameters.getID(),new JSONObject());        //save the object which return from the calling function

        teacher.sendResponse(response, res);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Teacher teacher = new Teacher();
        RequestsParameters requestsParameters = teacher.handleRequest(request, response);    //save the id and the function
        JSONObject jsonObject = JsonHandler.getJSONObject(request);       //save the details which comming from the frontend to the jason object
        Object res = teacher.executeFunction(requestsParameters.getFunction(), requestsParameters.getID(), jsonObject );        //save the object which return from the calling function
        teacher.sendResponse(response, res);
    }


}

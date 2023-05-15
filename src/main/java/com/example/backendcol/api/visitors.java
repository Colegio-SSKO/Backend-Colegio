package com.example.backendcol.api;

import com.example.backendcol.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet(name = "visitors", value = "/api/visitors/*")
public class visitors extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("this is the visitor");
        Visitor visitor = new Visitor();
        RequestsParameters requestsParameters = visitor.handleRequest(request, response); //save the id and the function
        System.out.println("heloooooooooooooo");
        Object res = visitor.executeFunction(requestsParameters.getFunction(), requestsParameters.getID(),new JSONObject());        //save the object which return from the calling function

        visitor.sendResponse(response, res);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("this is the visitor");
        Visitor visitor = new Visitor();
        System.out.println("athult ynna hdnnee");
        RequestsParameters requestsParameters = visitor.handleRequest(request, response);    //save the id and the function
        JSONObject jsonObject = JsonHandler.getJSONObject(request);       //save the details which comming from the frontend to the jason object
        Object res = visitor.executeFunction(requestsParameters.getFunction(), requestsParameters.getID(), jsonObject );        //save the object which return from the calling function
        visitor.sendResponse(response, res);
    }
}

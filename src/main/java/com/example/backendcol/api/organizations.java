package com.example.backendcol.api;

import com.example.backendcol.JsonHandler;
import com.example.backendcol.Organization;
import com.example.backendcol.RequestsParameters;
import com.example.backendcol.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet(name = "organizations", value = "/api/organizations/*")
public class organizations extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Organization organization = new Organization();
        RequestsParameters requestsParameters = organization.handleRequest(request, response); //save the id and the function
        System.out.println("heloooooooooooooo");
        Object res = organization.executeFunction(requestsParameters.getFunction(), requestsParameters.getID(),new JSONObject());        //save the object which return from the calling function

        organization.sendResponse(response, res);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Organization organization = new Organization();
        RequestsParameters requestsParameters = organization.handleRequest(request, response);    //save the id and the function
        JSONObject jsonObject = JsonHandler.getJSONObject(request);       //save the details which comming from the frontend to the jason object
        Object res = organization.executeFunction(requestsParameters.getFunction(), requestsParameters.getID(), jsonObject );        //save the object which return from the calling function
        organization.sendResponse(response, res);
    }


}

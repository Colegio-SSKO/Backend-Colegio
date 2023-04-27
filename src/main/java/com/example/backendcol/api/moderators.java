package com.example.backendcol.api;

import com.example.backendcol.JsonHandler;
import com.example.backendcol.Moderator;
import com.example.backendcol.RequestsParameters;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet(name = "moderators", value = "/api/moderators/*")
public class moderators extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Moderator moderator = new Moderator();
        RequestsParameters requestsParameters = moderator.handleRequest(request, response); //save the id and the function
        System.out.println("heloooooooooooooo");
        Object res = moderator.executeFunction(requestsParameters.getFunction(), requestsParameters.getID(),new JSONObject());        //save the object which return from the calling function

        moderator.sendResponse(response, res);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Moderator moderator = new Moderator();
        RequestsParameters requestsParameters = moderator.handleRequest(request, response);    //save the id and the function
        JSONObject jsonObject = JsonHandler.getJSONObject(request);       //save the details which comming from the frontend to the jason object
        Object res = moderator.executeFunction(requestsParameters.getFunction(), requestsParameters.getID(), jsonObject );        //save the object which return from the calling function
        moderator.sendResponse(response, res);
    }


}

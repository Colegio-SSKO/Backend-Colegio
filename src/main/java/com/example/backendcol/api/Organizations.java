package com.example.backendcol.api;

import com.example.backendcol.ApiHandler;
import com.example.backendcol.Organization;
import com.example.backendcol.RequestsParameters;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet(name = "Organizations", value = "/api/Organizations/*")






public class Organizations extends HttpServlet {




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        Organization organization = new Organization();

        ApiHandler apiHandler = new ApiHandler();
        RequestsParameters requestsParameters = apiHandler.handleRequest(request, response);
        Object res = apiHandler.executeFunction(requestsParameters.getFunction(), requestsParameters.getID(), new JSONObject());
        apiHandler.sendResponse(response, res);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}

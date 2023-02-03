package com.example.backendcol.api;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name = "teacher", value = "/api/teacher/*")
public class teacher extends HttpServlet {



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();

        //checking for white spaces
        String[] whites = path.split(" ");
        if (whites.length>1){

            //handle invalid path
            response.setContentType("text/html");
            response.setStatus(response.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.println("Invalid resource request. Try removing white spaces if there is any");
        }

        String[] params = path.split("/:");

        //checking the number of parameters
        if(params.length>2){
            response.setContentType("text/html");
            response.setStatus(response.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.println("Invalid resource request. multiple parameters found");
        }

        //Searching for the requested resource
        if (Resources.searchResource(params[1])){
            //handle request
        }
        else{
            response.setContentType("text/html");
            response.setStatus(response.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.println("Invalid resource request. Invalid resource");
        }

        System.out.println(params[1]);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }
}

package com.example.backendcol;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ApiHandler {
    //handle the request
    public static RequestsParameters handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //declaring important variables
        PrintWriter out = response.getWriter();
        RequestsParameters parameters = new RequestsParameters();
        String path = request.getPathInfo();

        //if no parameters found
        if (path == null || path.equals("/")){

            //do stuf if necessory
            return parameters;


        }
        else{
            String[] params = path.split("/");
            if(params.length>2){

                if(path.split("/:").length == 2){
                    parameters.setID(Integer.parseInt(path.split("/:")[1]));
                    parameters.setFunction(params[1]);
                    return parameters;
                }

                response.setContentType("text/html");
                response.setStatus(response.SC_BAD_REQUEST);
                out.println("Invalid resource request.");
                return parameters;
            }
            else{
                parameters.setFunction(params[1]);
                return parameters;
            }
        }
    }

    public static void sendResponse(HttpServletResponse response, Object res){
        try {
            PrintWriter out = response.getWriter();
            out.println(res);
        } catch (IOException ioException){
            System.out.println(ioException);
        }

    }

}

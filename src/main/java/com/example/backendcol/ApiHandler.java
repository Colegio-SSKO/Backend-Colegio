package com.example.backendcol;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ApiHandler {

    HttpServletResponse response;
    HttpServletRequest request;




    //handle the request
    public RequestsParameters handleRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.request = req;
        this.response = res;
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


    public void sendResponse(HttpServletResponse response, Object res){
        try {
            response.setContentType("application/json");
            response.addHeader("Access-Control-Expose-Headers", "Authorization");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Origin", "http://localhost:8080");
            response.addHeader("Access-Control-Allow-Methods" , "GET, POST, PUT, DELETE");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.println(res);
        } catch (IOException ioException){
            System.out.println(ioException);
        }

    }




    public Object executeFunction(String function, Integer id, JSONObject responseObject){
        try {
            Method method = this.getClass().getMethod(function, id.getClass(), JSONObject.class);
            return method.invoke(this,  id, responseObject);

        } catch (NoSuchMethodException noSuchMethodException){
            System.out.println(noSuchMethodException);
        } catch (IllegalAccessException illegalAccessException){
            System.out.println(illegalAccessException);
        } catch (InvocationTargetException invocationTargetException){
            System.out.println(invocationTargetException);
        }
        return -1;
    }

}

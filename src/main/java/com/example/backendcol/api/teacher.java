package com.example.backendcol.api;

import jakarta.json.JsonObject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import netscape.javascript.JSObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.*;

import org.json.*;



@WebServlet(name = "teacher", value = "/api/teachers/*")
public class teacher extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        Connection connection = Driver.getConnection();

        //getting print writer object
        PrintWriter out = response.getWriter();

        //if it has no params
        if (path == null || path.equals("/")){
            try {
                Statement statement = connection.createStatement();
                ResultSet rs1 = statement.executeQuery("select * from teacher");

                //preparing the JSON response
                JSONArray resp = new JSONArray();
                while(rs1.next()){
                    JSONObject element = new JSONObject();
                    element.put("teacher_ID", rs1.getInt("teacher_ID"));
                    element.put("gender", rs1.getString("gender"));
                    element.put("qualification_level", rs1.getString("qualification_level"));
                    element.put("User_ID", rs1.getInt("User_ID"));
                    resp.put(element);
                }

                //sending the response
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.println(resp);


            }catch (SQLException sqlException){
                System.out.println(sqlException);
            }

        }
        else {
            String params = path.split("/:")[1];
            if (path.split("/:").length>2){
                response.setContentType("text/html");
                response.setStatus(response.SC_BAD_REQUEST);
                out.println("Invalid resource request. multiple parameters found");
            }
            else if (Character.isDigit(params.charAt(0)) && params.length()==1){

                Integer parameter = Character.getNumericValue(params.charAt(0));
                try {
                    PreparedStatement statement = connection.prepareStatement("select * from teacher where teacher_ID = ?");
                    statement.setInt(1,parameter);

                    //creating the json response
                    ResultSet rs = statement.executeQuery();
                    JSONObject resp = new JSONObject();
                    while (rs.next()){
                        resp.put("teacher_ID", rs.getInt("teacher_ID"));
                        resp.put("gender", rs.getString("gender"));
                        resp.put("qualification_level", rs.getString("qualification_level"));
                        resp.put("User_ID", rs.getInt("User_ID"));
                    }

                    //sending the json response
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    out.println(resp);

                }catch (SQLException sqlException){
                    System.out.println(sqlException);
                }

            }
            else {
                response.setContentType("text/html");
                response.setStatus(response.SC_INTERNAL_SERVER_ERROR);
                out.println("ERROR");
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //getting print writer object
        PrintWriter out = response.getWriter();

        if (request.getPathInfo() != null){
            response.setContentType("text/html");
            response.setStatus(response.SC_BAD_REQUEST);
            out.println("Invalid resource request/parameters.try removing the parameters");
        }

        //get JSON data
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line = null;
        while ((line = br.readLine())!=null){
            sb.append(line);
        }
        JSONObject params = new JSONObject(sb.toString());



        //handle data
        String gender = params.get("gender").toString();
        String qualificationLevel = params.get("qualification_level").toString();
        Integer user_ID = params.getInt("User_ID");


        Connection connection = Driver.getConnection();

        try{
            PreparedStatement statement = connection.prepareStatement("insert into teacher (gender,qualification_level, User_ID) values (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,gender);
            statement.setString(2,qualificationLevel);
            statement.setInt(3,user_ID);


            statement.executeUpdate();
            Integer inserted_teacherID;
            ResultSet key = statement.getGeneratedKeys();
            if (key.next()){


                //creating the JSON response
                JSONObject resp = new JSONObject();
                resp.put("teacher_ID", key.getInt(1));

                //sending the inserted user ID as a json response
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.println(resp);
            }
            else{
                connection.rollback();
            }

        }catch (SQLException sqlException){
            System.out.println(sqlException);
        }


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

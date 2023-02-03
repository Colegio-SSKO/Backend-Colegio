package com.example.backendcol.api;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "users", value = "/api/users/*")
public class users extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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
        String DOB = params.get("DOB").toString();
        Integer verification_status = params.getInt("verification_status");
        String pro_pic = params.get("pro_pic").toString();
        String f_name = params.get("f_name").toString();
        String l_name = params.get("l_name").toString();
        String email = params.get("email").toString();

        Connection connection = Driver.getConnection();

        try{
            PreparedStatement statement = connection.prepareStatement("insert into user (DOB,verification_status, pro_pic,f_name, l_name, email) values (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,DOB);
            statement.setInt(2,verification_status);
            statement.setString(3,pro_pic);
            statement.setString(4,f_name);
            statement.setString(5,l_name);
            statement.setString(6,email);

            statement.executeUpdate();
            Integer inserted_userID;
            ResultSet key = statement.getGeneratedKeys();
            if (key.next()){


                //creating the JSON response
                JSONObject resp = new JSONObject();
                resp.put("user_ID", key.getInt(1));

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

}

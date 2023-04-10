package com.example.backendcol.api;

import com.example.backendcol.ApiHandler;
import com.example.backendcol.JsonHandler;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;

@WebServlet(name = "CreateAccount", value = "/CreateAccount")
public class CreateAccount extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String message = "";
        Integer status = 0;
        Integer insertedID = -1;

        JSONObject enteredData = JsonHandler.getJSONObject(request);
        Connection connection = Driver.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT user_ID from user where email = ?");
            statement.setString(1, enteredData.getString("email"));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                message = "User already exist";
            }
            else {
                statement = connection.prepareStatement("INSERT INTO user (DOB, f_name, l_name, pro_pic, email, password)" +
                        " values (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, enteredData.getString("DOB"));
                statement.setString(2, enteredData.getString("f_name"));
                statement.setString(3, enteredData.getString("l_name"));
                statement.setString(4, "/name");
                statement.setString(5, enteredData.getString("email"));
                statement.setString(6, enteredData.getString("password"));

                Integer rowAffected = statement.executeUpdate();
                if(rowAffected == 0){
                    message = "Error happened when inserting data";
                }
                else {
                    resultSet = statement.getGeneratedKeys();
                    if(resultSet.next()){
                        insertedID = resultSet.getInt(1);

                    }
                    else {
                        message = "Error happened when inserting data";
                    }
                }
            }

        } catch (SQLException sqlException){
            message = String.valueOf(sqlException);
        }


        JSONObject responseJson = new JSONObject();
        responseJson.put("status" , status);
        responseJson.put("message", message);
        responseJson.put("insertedID" , insertedID);
        ApiHandler apiHandler = new ApiHandler();
        apiHandler.sendResponse(response , responseJson);
    }
}

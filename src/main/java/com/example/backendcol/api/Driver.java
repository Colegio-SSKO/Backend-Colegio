package com.example.backendcol.api;

import java.sql.*;

public class Driver {
    public static Connection getConnection(){

        Connection connection = null;
        try {
            String user = "root";
            String password = "";
            String url = "jdbc:mysql://localhost:3306/colegionew";
            String jdbcDriver = "com.mysql.cj.jdbc.Driver";
            Class.forName(jdbcDriver);
            connection = DriverManager.getConnection(url, user, password);



        }catch (SQLException sqlException){
            System.out.println(sqlException);
        }
        catch (ClassNotFoundException classNotFoundException){
            System.out.println(classNotFoundException);
        }

        return connection;

    }

}

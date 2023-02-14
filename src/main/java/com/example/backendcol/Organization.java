package com.example.backendcol;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Organization extends User {
    public JSONObject getOrganization(Integer id) {
        Connection connection = Driver.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("select * from Organization where organizationID = ?");
            statement.setInt(1,id);

            //creating the json response
            ResultSet rs = statement.executeQuery();
            JSONObject jsonObject = JsonHandler.createJSONObject(rs, "organizationID", "location");
            return jsonObject;


        }catch (SQLException sqlException){
            System.out.println(sqlException);
        }

        //if cannot found, return an empty json object
        return new JSONObject();



    }
}

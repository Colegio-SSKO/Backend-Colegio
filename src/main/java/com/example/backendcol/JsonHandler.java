package com.example.backendcol;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JsonHandler {
    public static JSONObject getJSONObject(HttpServletRequest request){

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line = null;
            while ((line = br.readLine())!=null){
                sb.append(line);
            }
            JSONObject req = new JSONObject(sb.toString());
            return req;
        } catch (IOException ioException){
            System.out.println(ioException);

        }
        return new JSONObject();
    }

    public static JSONObject createJSONObject(ResultSet resultSet, String ...attributes){
        JSONObject jsonObject = new JSONObject();
        try {
            while (resultSet.next()){
                for (String attribute : attributes){
                    jsonObject.put(attribute, resultSet.getObject(attribute));
                }
            }
            return jsonObject;
        }catch (SQLException sqlException){
            System.out.println(sqlException);
        }
        return jsonObject;
    }

    public static JSONArray createJSONArray(ResultSet resultSet, String ...attributes){
        JSONArray jsonArray = new JSONArray();
        try {
            while (resultSet.next()){
                JSONObject jsonObject = new JSONObject();
                for (String attribute : attributes){
                    jsonObject.put(attribute, resultSet.getObject(attribute));
                }
                jsonArray.put(jsonObject);
            }
            return jsonArray;
        }catch (SQLException sqlException){
            System.out.println(sqlException);
        }
        return jsonArray;
    }

    public static void sendJSONData(HttpServletResponse response, Object object){
       try {
           PrintWriter out = response.getWriter();
           response.setContentType("application/json");
           response.setCharacterEncoding("UTF-8");
           out.println(object);
       }catch (IOException ioException){
           System.out.println(ioException);
       }
    }

}

package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Cart {

    List<Content> items;


    public Cart(Integer id){

        items = new ArrayList<>();

        Connection connection = Driver.getConnection();
        System.out.println("methnt enw");
        JSONArray jsonArray= new JSONArray();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("Select content.description, content.image as img_src , content.content_id as content_id, content.title as title, content.price as price, user.date_joined as description2, CONCAT(user.f_name, user.l_name) as author from content inner join cart on cart.content_id= content.content_id inner join user on content.user_id= user.user_id where cart.user_id=? and cart.status = 0;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            jsonArray = JsonHandler.createJSONArray(rs, "img_src", "description", "title" , "price", "description2", "author", "content_id");
            for (int i=0; i < jsonArray.length() ; i++){
                Content newContent = new Content(jsonArray.getJSONObject(i));
                items.add(newContent);
            }
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }


    }

    public JSONArray getCartDetails(Integer id){
        JSONArray jsonArray= new JSONArray();
        for (int i = 0; i<items.size() ; i++){
            jsonArray.put(items.get(i).data);
        }

        return jsonArray;
    }

    public JSONObject removeItem(Integer id, JSONObject requestObject){
        //removing from the attributes
        for (int i = 0; i < items.size(); i++){
            if (items.get(i).data.getInt("content_id")==requestObject.getInt("content_id")){
                items.remove(i);
            }
        }

        Connection connection = Driver.getConnection();
        JSONObject jsonObject= new JSONObject();
        try{
            PreparedStatement statement;
            statement = connection.prepareStatement("Update cart set status=1 where user_id=? && content_id=?");
            statement.setInt(1, id);
            statement.setInt(2,requestObject.getInt("content_id"));
            Integer res_id = statement.executeUpdate();

        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }
        return jsonObject;
    }


    public JSONObject addItem(Integer id, JSONObject requestObject){
        Connection connection = Driver.getConnection();

        JSONObject jsonObject= new JSONObject();
        try{


            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * from cart where user_id=? && content_id=? && status=0");
            statement.setInt(1,id);
            statement.setInt(2,requestObject.getInt("content_id"));
            ResultSet rs= statement.executeQuery();

            if(rs.next()){
                jsonObject.put("message","You already added this content");
            }

            else{
//                adding to the items list
                Content newContent = new Content(requestObject.getInt("content_id"));
                items.add(newContent);

                statement = connection.prepareStatement("SELECT * from cart where user_id=? && content_id=? && status=1");
                statement.setInt(1,id);
                statement.setInt(2,requestObject.getInt("content_id"));
                ResultSet rs2= statement.executeQuery();

                if(rs2.next()){
                    jsonObject.put("message","Added to cart");
                    statement = connection.prepareStatement("Update cart set status=0 where user_id=? && content_id=?");
                    statement.setInt(1,id);
                    statement.setInt(2,requestObject.getInt("content_id"));
                    Integer num= statement.executeUpdate();
                }
                else{
                    jsonObject.put("message","Added to cart");
                    PreparedStatement statement2;
                    statement2 = connection.prepareStatement("INSERT INTO cart (status, user_id, content_id) values (0,?,?)");
                    statement2.setInt(1,id);
                    statement2.setInt(2,requestObject.getInt("content_id"));
                    Integer res_id = statement2.executeUpdate();
                }

            }
        }



        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

        return jsonObject;
    }


}

package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Content {

    JSONObject data;

    public Content(){

    }

    public Content(JSONObject jsonObject){
        data = jsonObject;
    }

    public Content(Integer id){
//        Select content.decription, content.thumbnail as img_src , content.content_id as content_id, content.title as title, content.price as price, user.date_joined as description2, CONCAT(user.f_name, user.l_name) as author from content inner join cart on cart.content_id= content.content_id inner join user on content.user_id= user.user_id

        JSONObject jsonObject = new JSONObject();
        try{
            Connection connection = Driver.getConnection();
            PreparedStatement statement;
            statement = connection.prepareStatement("Select content.description, content.rate_count, content.image as img_src , content.content_id as content_id, content.title as title, content.price as price, user.date_joined as description2, CONCAT(user.f_name, user.l_name) as author from content inner join cart on cart.content_id= content.content_id inner join user on content.user_id= user.user_id where content.content_id = ?;");
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();

            jsonObject = JsonHandler.createJSONObject(rs, "img_src", "description", "title" , "price", "description2", "author", "content_id","rate_count");
            data = jsonObject;
            PreparedStatement preparedStatement1 = connection.prepareStatement("select * from comments inner join user on user.user_id = comments.user_id where content_id = ?");
            preparedStatement1.setInt(1, id);

            ResultSet resultSet1 = preparedStatement1.executeQuery();
            JSONArray comments = JsonHandler.createJSONArray(resultSet1, "comments.comment_id", "comments.message", "comments.date", "comments.user_id", "user.f_name", "user.l_name", "user.pro_pic");
            data.put("comments", comments);
        }

        catch(SQLException sqlException){
            System.out.println(sqlException);
        }

    }
}

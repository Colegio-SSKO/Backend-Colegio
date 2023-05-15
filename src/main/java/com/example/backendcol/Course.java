package com.example.backendcol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Course extends Content{

    List<CourseMedia> courseMedia;

    public Course(){

    }

    public Course(JSONObject jsonObject){
        System.out.println("cunstructor called");
        try {
            courseMedia = new ArrayList<>();
            this.data = jsonObject;
            Connection connection = Driver.getConnection();
            System.out.println(jsonObject.getInt("content_id"));
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from course_media inner join course on course_media.course_id = course.course_id WHERE course.content_id = ?");
            preparedStatement.setInt(1,jsonObject.getInt("content_id"));
            ResultSet resultSet = preparedStatement.executeQuery();

            JSONArray jsonArray = JsonHandler.createJSONArray(resultSet, "course_media_id", "media", "meida_title", "media_description");

            System.out.println(jsonArray.length());
            for (int i = 0; i< jsonArray.length() ; i++){
                CourseMedia newCourseMedia = new CourseMedia(jsonArray.getJSONObject(i));
                System.out.println("meka deprk run und");
                courseMedia.add(newCourseMedia);
            }
        }
        catch (Exception exception){
            System.out.println(exception);
        }
    }
}

package com.example.backendcol.api;

import com.example.backendcol.Driver;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@WebServlet(name = "createCourse", value = "/api/createCourse")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 20, // 20MB
        maxFileSize = 1024 * 1024 * 100000, // 100000MB
        maxRequestSize = 1024 * 1024 * 5000000 // 5000000MB
)
public class createCourse extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("post ek weda");

        //generating a random number to add to the file name
        Integer min = 1;
        Integer max = 10000000;

        Random random = new Random();
        Integer randomNumber  = random.nextInt((max-min) + 1) + min;



        Collection<Part> files = request.getParts();

        String contentType = "";
        Integer contentId = -100;
        String thumbnailPath = "";
        List<String> videoPaths = new ArrayList<>();
        JSONObject textJson = null;
        try {
            for (Part file : files) {
                contentType = file.getContentType();

                //handle thumbnail
                if(contentType ==null){
                    // Handle text data
                    String textData;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    textData = sb.toString();
                    System.out.println("mekt awilla nee");
                    textJson = new JSONObject(textData);
                    contentId = handleText(textJson);
                }

                else if (contentType.startsWith("image/")){
                     thumbnailPath = handleThumbnail(file, request, randomNumber);
                }
                else if (contentType.startsWith("video/")){

                    videoPaths.add(handleVideo(file, request, randomNumber));
                }


                if(contentId<0 && textJson == null){
                    //handle error
                }
                else {


                    //inserting to course table
                    Integer generatedKey = -100;
                    try{
                        Connection connection = Driver.getConnection();
                        PreparedStatement statement = connection.prepareStatement("insert into course (course_title, introduction_media, price, decription, content_id) values (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                        statement.setString(1, textJson.getString("courseTitle"));
                        statement.setString(2, thumbnailPath);
                        statement.setString(3, textJson.getString("coursePrice"));
                        statement.setString(4, textJson.getString("courseDescription"));
                        statement.setInt(5, contentId);
                        
                        
                        Integer result = statement.executeUpdate();


                        if (result == 1){
                            ResultSet resultSet = statement.getGeneratedKeys();
                            if(resultSet.next()){
                                generatedKey = resultSet.getInt(1);
                                System.out.println("key is : "+ generatedKey);
                            }
                        }



                        JSONArray videoTitles = textJson.getJSONArray("videoTitles");

                        System.out.println("video json hri");
                        JSONArray videoDescriptions = textJson.getJSONArray("videoDescriptions");
                        System.out.println("description json hri");

                        statement = connection.prepareStatement("insert into course_media (course_id, media, meida_title, media_description) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

                        for (int i = 0;i<videoTitles.length();i++){
                            statement.setInt(1, generatedKey);
                            statement.setString(2, videoPaths.get(i));
                            statement.setString(3, videoTitles.getString(i));
                            statement.setString(4, videoDescriptions.getString(i));
                            statement.addBatch();
                        }

                        int[] numberOdUpdates = statement.executeBatch();

                        if (numberOdUpdates.length == videoTitles.length() &&
                            numberOdUpdates.length == videoDescriptions.length() &&
                                numberOdUpdates.length == videoPaths.size()
                        ){
                            System.out.println("Update ek lssnt una");
                        }


                    }catch(Exception exception){
                        System.out.println(exception);
                    }

                }
            }

        }catch (Exception exception){
            System.out.println(exception);
        }






    }


    public static String handleThumbnail(Part thumbnail, HttpServletRequest request, Integer randomNumber) throws IOException{
        //user id ek session eken gnna dnt hardcode krnw
        Integer userID = 1123;
        String thumbnailFilename = "tbn" + userID + randomNumber +thumbnail.getSubmittedFileName();
        thumbnailFilename = thumbnailFilename.replace(" ", "");


        ServletContext context = request.getServletContext();
        String relativePath = "";
        String thumbnailPath = context.getRealPath(relativePath);

        String saveFilePath = "../../src/main/webapp/src/images/courseThumbnails/"+thumbnailFilename;
        File savedThumbnailFile = new File(thumbnailPath, saveFilePath);
        InputStream thumbnailInputStream = thumbnail.getInputStream();
        OutputStream thumbnailOutputStream = new FileOutputStream(savedThumbnailFile);
        byte[] thumbnailBuffer = new byte[1024];
        int thumbnailBytesRead;
        while ((thumbnailBytesRead = thumbnailInputStream.read(thumbnailBuffer)) != -1) {
            thumbnailOutputStream.write(thumbnailBuffer, 0, thumbnailBytesRead);
        }
        thumbnailOutputStream.flush();
        thumbnailOutputStream.close();
        thumbnailInputStream.close();
        System.out.println("no error for thumbnail");

        return saveFilePath;

    }


    public static String handleVideo(Part video, HttpServletRequest request, Integer randomNumber) throws IOException{
        //user id ek session eken gnna dnt hardcode krnw
        Integer userID = 1123;
        String videoFilename = "tbn" + userID + randomNumber +video.getSubmittedFileName();
        videoFilename = videoFilename.replace(" ", "");


        ServletContext context = request.getServletContext();
        String relativePath = "";
        String videoPath = context.getRealPath(relativePath);


        String saveFilePath = "../../src/main/webapp/src/videos/courseVideos/"+videoFilename;
        File savedVideoFile = new File(videoPath, saveFilePath);
        InputStream videoInputStream = video.getInputStream();
        OutputStream videoOutputStream = new FileOutputStream(savedVideoFile);
        byte[] videoBuffer = new byte[1024];
        int videoBytesRead;
        while ((videoBytesRead = videoInputStream.read(videoBuffer)) != -1) {
            videoOutputStream.write(videoBuffer, 0, videoBytesRead);
        }
        videoOutputStream.flush();
        videoOutputStream.close();
        videoInputStream.close();
        System.out.println("no error for videos");

        return saveFilePath;
    }

    public static Integer handleText(JSONObject jsonData){

        Integer generatedKey = -100;
        try{
            Connection connection = Driver.getConnection();
            PreparedStatement statement = connection.prepareStatement("insert into content (user_id, subject_id, status, type) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, jsonData.getInt("userId"));
            statement.setInt(2, jsonData.getInt("courseSubject"));
            statement.setInt(3, 0);
            statement.setInt(4, 0);
            Integer result = statement.executeUpdate();


            if (result == 1){
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()){
                    generatedKey = resultSet.getInt(1);
                    System.out.println("key is : "+ generatedKey);
                }
            }

        }catch(Exception exception){
            System.out.println(exception);
        }
        return generatedKey;
    }
}

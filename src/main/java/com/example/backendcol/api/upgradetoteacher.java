package com.example.backendcol.api;

import com.example.backendcol.Driver;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@WebServlet(name = "upgradetoteacher", value = "/api/upgradetoteacher")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 20, // 20MB
        maxFileSize = 1024 * 1024 * 100000, // 100000MB
        maxRequestSize = 1024 * 1024 * 5000000 // 5000000MB
)
public class upgradetoteacher extends HttpServlet {

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
        Integer upgradeId = -100;
        String thumbnailPath = "";
//        List<String> videoPaths = new ArrayList<>();
        JSONObject textJson = null;
        try {
            for (Part file : files) {
                contentType = file.getContentType();

                //handle thumbnail
                System.out.println(contentType);
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
//                    upgradeId = handleText(textJson,thumbnailPath);
                }
                else if (contentType.startsWith("image/")){
                     thumbnailPath = handleThumbnail(file, request, randomNumber);
                     System.out.println("mekata awa");
                }



                if(upgradeId<0 && textJson == null){
                    //handle error

                }
                else {
                    System.out.println("valid");

                    //inserting to course table
//                    Integer generatedKey = -100;
//                    try{
//                        Connection connection = Driver.getConnection();
//                        PreparedStatement statement = connection.prepareStatement("INSERT INTO upgrade_to_teacher (user_id, education_level, certificate,refers) VALUES (?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
//                        statement.setString(1, textJson.getString("userId"));
//                        statement.setString(2, textJson.getString("education_level"));
//                        statement.setString(3, thumbnailPath);
//                        statement.setString(4, textJson.getString("references"));
//
//
//                        Integer result = statement.executeUpdate();
//
//
//                        if (result == 1){
//                            ResultSet resultSet = statement.getGeneratedKeys();
//                            if(resultSet.next()){
//                                generatedKey = resultSet.getInt(1);
//                                System.out.println("key is : "+ generatedKey);
//                            }
//                        }
//
//
//                    }catch(Exception exception){
//                        System.out.println(exception);
//                    }

                }
            }

            upgradeId = handleText(textJson,thumbnailPath);
            if(upgradeId<0 || textJson == null){
                //handle error

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

        String saveFilePath = "../../src/"+thumbnailFilename;
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




    public static Integer handleText(JSONObject jsonData, String path){

        Integer generatedKey = -100;
        try{
            Connection connection = Driver.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO upgrade_to_teacher (user_id, education_level, certificate,refers) VALUES (?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, jsonData.getInt("userId"));
            statement.setString(2, jsonData.getString("education_level"));
            statement.setString(3, path);
            statement.setString(4, jsonData.getString("references"));

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

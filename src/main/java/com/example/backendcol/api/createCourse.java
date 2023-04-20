package com.example.backendcol.api;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.*;
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

        System.out.println("length: " + files.size());
        String contentType = "";
        try {
            for (Part file : files) {
                contentType = file.getContentType();

                System.out.println("contentType: " + file.getContentType());
                System.out.println("awa");
                //handle thumbnail
                if (contentType.startsWith("image/")){
                    handleThumbnail(file, request, randomNumber);
                }
                else if (contentType.startsWith("video/")){
                    System.out.println("mektath awa");
                    handleVideo(file, request, randomNumber);
                }
            }

        }catch (Exception exception){
            System.out.println(exception);
        }






    }


    public static void handleThumbnail(Part thumbnail, HttpServletRequest request, Integer randomNumber) throws IOException{
        //user id ek session eken gnna dnt hardcode krnw
        Integer userID = 1123;
        String thumbnailFilename = "tbn" + userID + randomNumber +thumbnail.getSubmittedFileName();
        thumbnailFilename = thumbnailFilename.replace(" ", "");


        ServletContext context = request.getServletContext();
        String relativePath = "";
        String thumbnailPath = context.getRealPath(relativePath);
        System.out.println(thumbnailPath);

        File savedThumbnailFile = new File(thumbnailPath, "../../src/main/webapp/src/images/courseThumbnails/"+thumbnailFilename);
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


    }


    public static void handleVideo(Part video, HttpServletRequest request, Integer randomNumber) throws IOException{
        //user id ek session eken gnna dnt hardcode krnw
        Integer userID = 1123;
        String videoFilename = "tbn" + userID + randomNumber +video.getSubmittedFileName();
        videoFilename = videoFilename.replace(" ", "");


        ServletContext context = request.getServletContext();
        String relativePath = "";
        String videoPath = context.getRealPath(relativePath);
        System.out.println(videoPath);

        File savedVideoFile = new File(videoPath, "../../src/main/webapp/src/videos/courseVideos/"+videoFilename);
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


    }
}

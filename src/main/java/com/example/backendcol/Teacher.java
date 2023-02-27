package com.example.backendcol;

import org.json.JSONObject;
import jakarta.servlet.http.*;
import java.io.File;

import java.util.List;



public class Teacher extends User{



    public JSONObject createCourse(Integer id, JSONObject requestObject){
        try{




            JSONObject jsonObject = new JSONObject(request.getParameter("data"));
            String title = jsonObject.getString("title");
            String category = jsonObject.getString("category");
            String description = jsonObject.getString("description");





            // Get the save location from a servlet context parameter
            String savePath = "C:\\Users\\uni\\Videos\\AnyDesk";

            // Get the uploaded files from the request
            List<Part> parts = (List<Part>) request.getParts();

            // Loop through each uploaded file and save it to the server
            System.out.println(parts);
            for (Part part : parts) {
                String fileName = extractFileName(part);
                if (fileName != null && !fileName.isEmpty()) {

                    String filePath = savePath + File.separator + fileName;
                    part.write(filePath);

                }
            }
        }catch (Exception exception){
            System.out.println(exception);
        }



        System.out.println("Method called");
        return new JSONObject();
    }





    // Extracts the file name from a part header
    private String extractFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] elements = contentDisposition.split(";");
        for (String element : elements) {
            if (element.trim().startsWith("filename")) {
                String fileName = element.substring(element.indexOf('=') + 1).trim().replace("\"", "");
                return fileName;
            }
        }
        return null;
    }
}

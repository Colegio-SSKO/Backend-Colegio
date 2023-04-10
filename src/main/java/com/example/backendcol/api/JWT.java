package com.example.backendcol.api;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class JWT {
    String token = "";

    String signedToken = "";
    JSONObject header = new JSONObject();
    JSONObject payload = new JSONObject();

    String Signature = "";

    public void putHeader(String name, String value){
        header.put(name, value);
    }

    public void putPayload(String name, String value){
        header.put(name, value);
    }

    public void createToken(){

        String encodedHeader = new String(Base64.getEncoder().encodeToString(header.toString().getBytes()));
        String encodedPayload = new String(Base64.getEncoder().encodeToString(payload.toString().getBytes()));
        token = encodedHeader+ '.' + encodedPayload;
    }

    public String getSign(String unsignedToken){
        String signature = "";
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            byte[] result = messageDigest.digest(unsignedToken.getBytes());

            for (int i = 0; i < result.length; i++) {
                signature += Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1);
            }


        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException){
            System.out.println(noSuchAlgorithmException);
        }
        return new String(Base64.getEncoder().encodeToString(signature.getBytes()));
    }

    public void sign(){
        signedToken = token + '.' + getSign(token);
    }

    public boolean validateToken(String token){
        String[] splitted = token.split(".");
        String header = splitted[0];
        String payload = splitted[1];
        String withoutSign = header + '.' + payload;
//        if(getSign(withoutSign).equals())
        return false;
    }


}

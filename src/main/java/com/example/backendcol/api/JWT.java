package com.example.backendcol.api;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class JWT {

    String secret = "This is the secret";

    JSONObject header = new JSONObject();
    JSONObject payload = new JSONObject();

    public String token = "";
    public void putHeader(String name, Object value){header.put(name, value);}
    public void putPayload(String name, Object value){payload.put(name, value);}

    public void createToken(){

        String encodedHeader = new String(Base64.getEncoder().encodeToString(header.toString().getBytes()));
        String encodedPayload = new String(Base64.getEncoder().encodeToString(payload.toString().getBytes()));
        token = encodedHeader+ '.' + encodedPayload;
    }

    public void sign(){
        token += '.' + getSign(token);
    }


    public String getSign(String unsignedToken){
        String sign = "";
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        try{
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);

            byte[] hmacBytes = mac.doFinal(unsignedToken.getBytes());
            sign = Base64.getEncoder().encodeToString(hmacBytes);
        }catch (Exception exception){
            System.out.println(exception);
        }
        return sign;
    }







}

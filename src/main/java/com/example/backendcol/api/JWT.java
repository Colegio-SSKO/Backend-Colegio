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

    public JSONObject header = new JSONObject();
    public JSONObject payload = new JSONObject();

    public String token = "";

    public String signature = "";

    public String providedSign = "";
    public void putHeader(String name, Object value){header.put(name, value);}
    public void putPayload(String name, Object value){payload.put(name, value);}

    public void createToken(){

        String encodedHeader = new String(Base64.getEncoder().encodeToString(header.toString().getBytes()));
        String encodedPayload = new String(Base64.getEncoder().encodeToString(payload.toString().getBytes()));
        token = encodedHeader+ '.' + encodedPayload;
    }

    public void sign(){
        signature = getSign(token);
        token += '.' + signature;
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

    public void decodeJWT(String token){
        String[] parts = token.split("\\.");

        String header = new String(Base64.getUrlDecoder().decode(parts[0]));
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

        this.header = new JSONObject(header);
        this.payload = new JSONObject(payload);
        this.providedSign = parts[2];
        System.out.println("Payload: " + payload);
    }

    public boolean validate(){
        if (providedSign.equals(signature))
            return true;
        else
            return false;
    }







}

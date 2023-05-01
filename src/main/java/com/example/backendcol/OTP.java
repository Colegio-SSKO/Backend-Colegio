package com.example.backendcol;

import java.time.LocalTime;
import java.util.Random;

public class OTP {
    private String otp = "";
    private LocalTime expTime = LocalTime.now().plusMinutes(2);

    public String getOtp(){
        return otp;
    }

    public OTP(){
        Random random = new Random();
        Integer digit;
        for (int i=0; i<5; i++ ){
            digit = random.nextInt(9)+1;
            otp += String.valueOf(digit);
        }
        System.out.println("random number is: " + otp);
    }

    public Boolean isValid(){
        LocalTime verifyTime = LocalTime.now();
        if (expTime.isBefore(verifyTime)){
            return false;
        }
        else{
            return true;
        }
    }

    public Boolean verify(String userEnteredOtp){
        if (otp.equals(userEnteredOtp)){
            return true;
        }
        else{
            return false;
        }
    }
}

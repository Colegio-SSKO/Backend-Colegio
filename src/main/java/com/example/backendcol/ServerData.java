package com.example.backendcol;

import jakarta.websocket.Session;

import java.util.HashMap;

public class ServerData {
    public static final HashMap<Integer, Object> users = new HashMap<>();
    public static final HashMap<Integer, OTP> otpSessions = new HashMap<>();

    public static final HashMap<Integer, Session> userIdToNotifictionSession = new HashMap();
}

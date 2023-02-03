package com.example.backendcol.api;

import java.util.ArrayList;

public final class Resources {
    private static ArrayList<String> resources = new ArrayList<String>();

    public static boolean searchResource(String req){
        for (String res: resources) {
            if(req.equals(res)){
                return true;
            }
        }
        return false;
    }
}

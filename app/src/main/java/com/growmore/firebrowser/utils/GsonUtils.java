package com.growmore.firebrowser.utils;

import com.google.gson.Gson;

public class GsonUtils {
    private static Gson gsonInstance;
    public static Gson getInstance(){
        if(gsonInstance == null)
            gsonInstance = new Gson();
        return gsonInstance;
    }
}

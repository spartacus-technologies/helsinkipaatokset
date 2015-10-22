package com.spartacus.helsinki_paatokset.data_access;

import java.util.LinkedHashMap;

/**
 * Created by Eetu on 22.10.2015.
 * Class for storing and retrieving JSON data queried before
 */
public class APICache {

    private static LinkedHashMap<String, String> storage;

    public static void store(String key, String data){

        if(storage == null){
            storage = new LinkedHashMap<>();
        }
        storage.put(key, data);
    }


    public static String retrieve(String key){

        if(storage == null){
           return null;
        }
        return storage.get(key);
    }

}

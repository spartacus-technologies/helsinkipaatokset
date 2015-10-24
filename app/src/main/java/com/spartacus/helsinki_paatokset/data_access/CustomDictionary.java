package com.spartacus.helsinki_paatokset.data_access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eetu on 22.10.2015.
 * Class for storing data for quick access & search
 */
public class CustomDictionary {

    List<Tuple<Integer, String>> text_data;

    Map container = new HashMap<String, List<Tuple<Integer, String>>>();

    public CustomDictionary(){

        container = new HashMap();
        text_data = new ArrayList<>();
    }

    public void addData(Integer index, String text){

        //Split string
        text = text.replaceAll("/[^A-Za-z0-9 ]/", "");
        String[] arr = text.split(" ");

        for (String key : arr){


        }

    }


    class Tuple<X, Y> {
        public final X x;
        public final Y y;
        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }
}

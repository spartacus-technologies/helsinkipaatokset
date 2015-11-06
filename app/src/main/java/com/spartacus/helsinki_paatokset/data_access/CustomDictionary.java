package com.spartacus.helsinki_paatokset.data_access;

import android.util.Log;

import com.google.gson.Gson;

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

    public Tuple<Integer, String> searchForMeeting(String keyword){

        //Search map for match:
        return (Tuple<Integer, String>) container.get(keyword);
    }

    public void addData(Integer index, String text){

        Map m_data = new Gson().fromJson(text, Map.class);

        List agenda_items = (List) m_data.get("objects");

        //Loop agenda items and add to container
        for(Object agenda_item : agenda_items){

            //Create pair for storing:
            Tuple<Integer, String> data = new Tuple<>(index, ((Map)agenda_item).get("content").toString());

            //Split string
            String index_text = data.y.replaceAll("/[^A-Za-z0-9 ]/", "");
            String[] arr = index_text.split(" ");

            for (String key : arr){

                //Search map for a match:
                try {

                    List<Tuple<Integer, String>> temp = (List<Tuple<Integer, String>>) container.get(key);

                    if(temp == null){

                        temp = new ArrayList<>();
                        container.put(key, temp);
                    }

                    temp.add(data);

                }catch(Exception e){

                    //No existing element found -> add new:
                    Log.e("CustomDictionary", "Exception: " + e.getMessage());
                }
            }
        }
    }


    public class Tuple<X, Y> {
        public final X x;
        public final Y y;
        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }
}

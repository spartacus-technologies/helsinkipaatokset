package com.spartacus.helsinki_paatokset.data_access;

import android.util.Log;

/**
 * Created by Eetu on 1.10.2015.
 */
public class DataAccess {

    final static String API_PATH = "http://dev.hel.fi";

    //Registers listener and executes GET Method. After completion executes listeners callback function.
    public static void requestData(NetworkListener listener, String path, NetworkListener.RequestType type) {

        //Check if whole path is already present -> add if not:
        if(!path.contains(API_PATH)){

            path = API_PATH + path;
        }
        Log.i("DataAccess:requestData", "path=" + path);

        NetworkTask task = new NetworkTask();
        task.setNetworkListener(listener, type);

        task.execute(path);
    }

    //Registers listener and executes GET Method. After completion executes listeners callback function.
    public static void requestImageData(NetworkListener listener, String path, NetworkListener.RequestType type) {

        //Check if whole path is already present -> add if not:
        if(!path.contains(API_PATH)){

            path = API_PATH + path;
        }
        Log.i("DataAccess:requestData", "path=" + path);

        ImageDownloadTask task = new ImageDownloadTask();
        task.setNetworkListener(listener, type);
    }

    //private static List<NetworkListener> listeners;

    public interface NetworkListener{

        enum RequestType{

            MEETING,
            VIDEO,
            AGENDAS,
            VIDEO_PREVIEW,
            AGENDA_ITEM,
            POLICY_MAKERS,
            POLICY_MAKER, IMAGE
        }

        void DataAvailable(String data, RequestType type);
        void BinaryDataAvailable(Object data, RequestType type);
    }
    /*
    //Adds new listener to array
    void registerListener(NetworkListener listener){

        if(listeners == null){

            listeners = new ArrayList<NetworkListener>();
        }
        listeners.add(listener);
    }
    */

    //http://dev.hel.fi/paatokset/v1/agenda_item/
    //Function for testing REST API Connectivity
    public static boolean testConnection(NetworkListener listener){

        NetworkTask task = new NetworkTask();
        task.setNetworkListener(listener, null);

        task.execute("http://dev.hel.fi/paatokset/v1/meeting/");
        //task.execute("http://dev.hel.fi/paatokset/v1/agenda_item/");
        /*
        try{

            String response = HttpURLConnectionHandler.sendGet("http://dev.hel.fi/paatokset/v1/agenda_item/");

            return true;
        }catch (Exception e){


        }
        */
        return false;
    }

}

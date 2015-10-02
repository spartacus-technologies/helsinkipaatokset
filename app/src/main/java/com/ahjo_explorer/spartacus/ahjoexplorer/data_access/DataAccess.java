package com.ahjo_explorer.spartacus.ahjoexplorer.data_access;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eetu on 1.10.2015.
 */
public class DataAccess {

    //private static List<NetworkListener> listeners;

    public interface NetworkListener{

        void DataAvailable(String data);
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
        task.setNetworkListener(listener);

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

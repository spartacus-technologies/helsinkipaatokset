package com.ahjo_explorer.spartacus.ahjoexplorer.data_access;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Eetu on 2.10.2015.
 */
public class NetworkTask extends AsyncTask<String, Void, Void> {

    DataAccess.NetworkListener listener_;

    String response = null;

    public void setNetworkListener(DataAccess.NetworkListener listener){

        listener_ = listener;
    }

    @Override
    protected Void doInBackground(String... urls){

        if(listener_ == null){
            Log.e("NetworkTask", "No listener was set!");
            response = null;
        }

        // params comes from the execute() call: params[0] is the url.
        try {

            response = HttpURLConnectionHandler.sendGet(urls[0]);

        } catch (Exception e) {

            response = null;
        }

        return null;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(Void v) {

        Log.i("NetworkTask", "onPostExecute");
        listener_.DataAvailable(response);
    }

}

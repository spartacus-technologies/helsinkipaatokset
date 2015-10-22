package com.spartacus.helsinki_paatokset.data_access;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Eetu on 2.10.2015.
 */
public class NetworkTask extends AsyncTask<String, Void, Void> {

    DataAccess.NetworkListener listener_;

    String response = null;
    private DataAccess.NetworkListener.RequestType type_;

    public void setNetworkListener(DataAccess.NetworkListener listener, DataAccess.NetworkListener.RequestType type){

        type_ = type;
        listener_ = listener;
    }

    @Override
    protected Void doInBackground(String... urls){

        if(listener_ == null){
            Log.e("NetworkTask", "No listener was set!");
            response = null;
        }

        //first check for existing data in cache:
        String data = APICache.retrieve(urls[0]);

        if(data != null){

            response = data;
            return null;
        }

        // params comes from the execute() call: params[0] is the url.
        try {

            response = HttpURLConnectionHandler.sendGet(urls[0]);
            APICache.store(urls[0], response);

        } catch (Exception e) {

            response = null;
        }

        return null;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(Void v) {

        Log.i("NetworkTask", "onPostExecute");
        listener_.DataAvailable(response, type_);
    }

}

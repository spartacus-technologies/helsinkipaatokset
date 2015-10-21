package com.spartacus.helsinki_paatokset.data_access;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;

/**
 * Created by Eetu on 14.10.2015.
 * source: http://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
 */
public class ImageDownloadTask  extends AsyncTask<String, Void, Bitmap> {

    private DataAccess.NetworkListener networkListener;
    private DataAccess.NetworkListener.RequestType type_;

    @Override
    protected Bitmap doInBackground(String... urls) {

        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        InputStream in;

        try {
            in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
            in.close();

        } catch (Exception e) {

            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {

        networkListener.BinaryDataAvailable(result, type_);
    }

    public void setNetworkListener(DataAccess.NetworkListener networkListener, DataAccess.NetworkListener.RequestType type) {
        this.networkListener = networkListener;
        type_ = type;
    }
}

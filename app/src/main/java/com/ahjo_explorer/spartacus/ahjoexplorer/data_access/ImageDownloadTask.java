package com.ahjo_explorer.spartacus.ahjoexplorer.data_access;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by Eetu on 14.10.2015.
 * source: http://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
 */
public class ImageDownloadTask  extends AsyncTask<String, Void, Bitmap> {

    private DataAccess.NetworkListener networkListener;

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

        networkListener.BinaryDataAvailable(result);
    }

    public void setNetworkListener(DataAccess.NetworkListener networkListener) {
        this.networkListener = networkListener;
    }
}

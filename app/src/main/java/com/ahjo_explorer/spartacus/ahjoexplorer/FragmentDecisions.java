package com.ahjo_explorer.spartacus.ahjoexplorer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.ahjo_explorer.spartacus.ahjoexplorer.data_access.DataAccess;
import com.ahjo_explorer.spartacus.ahjoexplorer.data_access.iFragmentDataExchange;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

/**
 * Created by Eetu on 5.10.2015.
 */
public class FragmentDecisions extends Fragment implements View.OnClickListener, DataAccess.NetworkListener, iFragmentDataExchange {

    String decision_path_ = "";
    private List decisions = null;
    View view_;
    final String EXTRA_POSITION	= "position";
    private String video_path_ogv;
    private String agenda_data_html;
    private String agenda_id;
    private String video_path_pm4;
    private String video_screenshot_url;

    public static FragmentDecisions newInstance() {

        FragmentDecisions fragment = new FragmentDecisions();
        Bundle args = new Bundle();
        args.putString("FragmentName", fragment.getClass().toString());
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);

        //fragment.decision_path_ = decision_path;

        fragment.setArguments(args);
        return fragment;
    }
    /*
    public void setDecision_path_(String path){

        decision_path_ = path;
        getActivity().findViewById(R.id.progressBarContentLoading).setVisibility(View.VISIBLE);
        DataAccess.requestData(this, decision_path_);
    }
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_decision, container, false);
        view_ = view;

        //Hide spinner by default
        //view_.findViewById(R.id.progressBarContentLoadingFragmentDecisions).setVisibility(View.INVISIBLE);

        //Register listeners
        view.findViewById(R.id.buttonPlayVideoMP4).setOnClickListener(this);
        view.findViewById(R.id.buttonPlayVideoOGV).setOnClickListener(this);
        view.findViewById(R.id.buttonBackToUpFragmentDecisions).setOnClickListener(this);

        //Add new listener for webview content loading
        //TODO: workaround implemented
        /*
        final WebView webview =  ((WebView) view_.findViewById(R.id.webViewFragmentDecisions));
        webview.setPictureListener(new WebView.PictureListener() {

            @Override
            public void onNewPicture(WebView view, Picture picture) {

                //Check show scroll to top button
                checkTopButtonState();

                //Invalidate container if content is loaded
                //if (webview.getProgress() == 100)
                    view_.findViewById(R.id.linearLayoutFragmentDecisions).invalidate();
            }
        });
        */

        //Set some default values for webview
        if(agenda_data_html == null || agenda_data_html.length() == 0){

            ((WebView)view_.findViewById(R.id.webViewFragmentDecisions)).loadData("Valitse agenda.", "utf-8", null);
        }

        return view;
    }

    private void checkTopButtonState() {

        //Check if back to top button is needed:
        ScrollView scrollView = (ScrollView) view_.findViewById(R.id.scrollView);
        int childHeight = ((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentDecisions)).getHeight();
        int scrollViewHeight = scrollView.getHeight();
        boolean isScrollable = scrollView.getHeight() < childHeight + scrollView.getPaddingTop() + scrollView.getPaddingBottom();

        //Hide/show scroll to top if needed:
        if(isScrollable){

            view_.findViewById(R.id.buttonBackToUpFragmentDecisions).setVisibility(View.VISIBLE);
        }else{
            view_.findViewById(R.id.buttonBackToUpFragmentDecisions).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void DataAvailable(String data) {

        //Check what data we received:
        try {

            //Fill in agenda content
            Map m_data = new Gson().fromJson(data, Map.class);
            agenda_data_html = ((Map)((List)m_data.get("content")).get(0)).get("text").toString();
            view_.findViewById(R.id.progressBarContentLoadingFragmentDecisions).setVisibility(View.INVISIBLE);
            //TODO: add second spinner for video data

            //All good -> inflate decisions table
            inflateDecisions();

            //Request for video data related to this agenda item's meeting
            String meeting_id = ((Map)m_data.get("meeting")).get("id").toString();
            //DataAccess.requestData(this, meeting_id);

            //Also pass parameters to AttachmentFragment:
            ((MainActivity) getActivity()).exchange(2, agenda_id);

        }
        //Exception is thrown if data was not in correct format or we received something else than expected:
        catch (Exception e){

            try {

                Map m_data = new Gson().fromJson(data, Map.class);
                video_screenshot_url = ((Map)((List)m_data.get("objects")).get(0)).get("screenshot_uri").toString();
                video_path_pm4 = ((Map)((Map)((List)m_data.get("objects")).get(0)).get("local_copies")).get("video/mp4").toString();
                video_path_ogv = ((Map)((Map)((List)m_data.get("objects")).get(0)).get("local_copies")).get("video/ogg").toString();

                if(video_path_pm4 != null){

                    view_.findViewById(R.id.buttonPlayVideoMP4).setEnabled(true);
                }
                if(video_path_ogv != null){

                    view_.findViewById(R.id.buttonPlayVideoOGV).setEnabled(true);
                }
                if(video_screenshot_url != null){

                    DataAccess.requestImageData(this, video_screenshot_url);
                }

                return;

            }catch (NullPointerException e2){

                Log.e("FragmentDecisions", "Error: " + e2.getMessage());
                return;
            }

        }

        return;

        //TODO: just a dummy
        //video_path = "http://dev.hel.fi/paatokset/media/video/valtuusto180112b.ogv";

        //decisions = (List) decision_data.get("objects");

    }

    @Override
    public void BinaryDataAvailable(Object data) {

        //Bitmap received: add to layout
        ImageView img_view = new ImageView(getActivity());
        img_view.setImageBitmap((Bitmap)data);
        ((LinearLayout) view_.findViewById(R.id.imageLayoutVideoPreview)).removeAllViews();
        ((LinearLayout) view_.findViewById(R.id.imageLayoutVideoPreview)).addView(img_view);
    }

    //Shows data
    private void inflateDecisions() {

        if(agenda_data_html == null){

            Log.e("FragmentDecisions", "Error: decision data was empty!");
            return;
        }

        ((WebView)getActivity().findViewById(R.id.webViewFragmentDecisions)).loadDataWithBaseURL(null, agenda_data_html, "text/html", "UTF-8", null);

        //view_.findViewById(R.id.linearLayoutFragmentDecisions).invalidate();
        //view_.findViewById(R.id.scrollViewFragmentDecisions).invalidate();
        view_.invalidate();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.buttonPlayVideoMP4:

                Uri uri = Uri.parse(video_path_pm4);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {

                    Log.w("FragmentDefault", "onClick():ActivityNotFoundException");
                    Toast.makeText(getActivity(), "Warning: video player not found. Consider installing MX Player.", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.buttonPlayVideoOGV:

                uri = Uri.parse(video_path_ogv);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {

                    Log.w("FragmentDefault", "onClick():ActivityNotFoundException");
                    Toast.makeText(getActivity(), "Warning: video player not found. Consider installing MX Player.", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.buttonBackToUpFragmentDecisions:

                ((ScrollView) view_.findViewById(R.id.scrollViewFragmentDecisions)).smoothScrollTo(0, 0);
                break;


            default:
                Log.w("FragmentDecisions", "Unknown button");
                break;
        }

    }

    @Override
    public void exchange(int target, Object data) {

        getActivity().findViewById(R.id.progressBarContentLoadingFragmentDecisions).setVisibility(View.VISIBLE);
        agenda_id = data.toString();

        //DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/" + data.toString());
        Log.d("FragmentDecisions", "exchange: " + data.toString());


        //Data contains agenda id -> execute queries for detailed data
        DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/agenda_item/" + agenda_id);

        //Also query for matching video:
        //Note: disable video button as there is no quarantee that video file exists before querying:
        view_.findViewById(R.id.buttonPlayVideoMP4).setEnabled(false);
        view_.findViewById(R.id.buttonPlayVideoOGV).setEnabled(false);

        DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/video/?agenda_item_=" + agenda_id);

    }
}

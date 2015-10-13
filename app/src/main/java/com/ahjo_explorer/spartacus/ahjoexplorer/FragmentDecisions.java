package com.ahjo_explorer.spartacus.ahjoexplorer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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
    private String video_path;
    private String agenda_data_html;
    private String agenda_id;

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
        view.findViewById(R.id.buttonPlayVideo).setOnClickListener(this);
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
        }
        catch (Exception e){


        }

        //Also pass parameters to AttachmentFragment:
        ((MainActivity) getActivity()).exchange(2, agenda_id);

        //TODO: just a dummy
        video_path = "http://dev.hel.fi/paatokset/media/video/valtuusto180112b.ogv";

        //decisions = (List) decision_data.get("objects");

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

        if(v.getId() == R.id.buttonPlayVideo){

            Uri uri = Uri.parse(video_path);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //intent.putExtra( EXTRA_POSITION, v.getId()*1000); //MX player: video position - if needed

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {

                Log.w("FragmentDefault", "onClick():ActivityNotFoundException");
                Toast.makeText(getActivity(), "Warning: video player not found. Consider installing MX Player.", Toast.LENGTH_LONG).show();
            }
        }
        else if(v.getId() == R.id.buttonBackToUpFragmentDecisions){

            ((ScrollView) view_.findViewById(R.id.scrollView)).smoothScrollTo(0, 0);
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
    }
}

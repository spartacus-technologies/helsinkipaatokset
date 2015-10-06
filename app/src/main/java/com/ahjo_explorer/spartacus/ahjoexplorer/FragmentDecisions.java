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
import android.widget.TextView;
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

        //Register listeners
        view.findViewById(R.id.buttonPlayVideo).setOnClickListener(this);

        return view;
    }

    @Override
    public void DataAvailable(String data) {

        view_.findViewById(R.id.progressBarContentLoading).setVisibility(View.INVISIBLE);

        Map m_data = new Gson().fromJson(data, Map.class);

        //TODO: just a dummy
        video_path = "http://dev.hel.fi/paatokset/media/video/valtuusto180112b.ogv";

        //decisions = (List) decision_data.get("objects");

        //All good -> inflate decisions table
        inflateDecisions();

        //TODO: debugging:
        //((VideoView)view_.findViewById(R.id.videoView)).setVideoPath("http://dev.hel.fi/paatokset/media/video/valtuusto180112b.ogv");
        //((VideoView)view_.findViewById(R.id.videoView)).pla
        ((TextView)view_.findViewById(R.id.textViewDebug)).setText(((Map)((List)m_data.get("content")).get(0)).get("text").toString());

    }

    private void inflateDecisions() {

        //TODO: for debugging. Remove.

        if(decisions == null){

            Log.e("FragmentDecisions", "Error: decision data was empty!");
            return;
        }

        ((TextView)view_.findViewById(R.id.textViewDebug)).setText(decisions.toString());

        //Loop all decisions and construct needed UI components with data:
        for (Object meeting:
                decisions) {


        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.buttonPlayVideo){

            Uri uri = Uri.parse(video_path);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //intent.putExtra( EXTRA_POSITION, v.getId()*1000);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {

                Log.w("FragmentDefault.onClick()", "ActivityNotFoundException");
                Toast.makeText(getActivity(), "Warning: video player not found. Consider installing MX Player.", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void exchange(int target, Object data) {

        getActivity().findViewById(R.id.progressBarContentLoading).setVisibility(View.VISIBLE);

        //Data contains meeting id -> execute queries for video data
        //DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/" + data.toString());
        DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/agenda_item/" + data.toString());
        //DataAccess.requestData(this, data.toString());

    }
}

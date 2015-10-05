package com.ahjo_explorer.spartacus.ahjoexplorer.data_access;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ahjo_explorer.spartacus.ahjoexplorer.R;
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
        View view = inflater.inflate(R.layout.fragment_main2, container, false);
        view_ = view;
        return view;
    }

    @Override
    public void DataAvailable(String data) {

        view_.findViewById(R.id.progressBarContentLoading).setVisibility(View.INVISIBLE);

        Map decision_data = new Gson().fromJson(data, Map.class);
        //decisions = (List) decision_data.get("objects");

        //All good -> inflate decisions table
        inflateDecisions();
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

    }

    @Override
    public void exchange(int target, Object data) {

        getActivity().findViewById(R.id.progressBarContentLoading).setVisibility(View.VISIBLE);
        //In case of data exchange, check for URL and begin HTTP get:
        DataAccess.requestData(this, data.toString());

    }
}

package com.spartacus.ahjoexplorer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.spartacus.ahjoexplorer.data_access.DataAccess;
import com.spartacus.ahjoexplorer.data_access.iFragmentDataExchange;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentMeetings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMeetings extends Fragment implements View.OnClickListener, DataAccess.NetworkListener, iFragmentDataExchange {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view_;
    private String next_path;
    private List meetings;

    private int policy_maker = -1;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentMeetings.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMeetings newInstance() {
        FragmentMeetings fragment = new FragmentMeetings();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentMeetings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //view_ = inflater.inflate(R.layout.splashscreen, container, false);

        //return view_;

        view_ = inflater.inflate(R.layout.fragment_meetings, container, false);

        //Request meetings data

        //For all policy makers:
        /*
        if(policy_maker < 0){
            DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/meeting/");
        }
        //For specific policy maker:
        else{
            DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/meeting/?limit=1000&offset=0&policymaker=" + policy_maker);
        }
        */
        //Register listeners
        view_.findViewById(R.id.buttonUpdateFragmentMeetings).setOnClickListener(this);
        //view.findViewById(R.id.scrollView).setOnScrollChangeListener(this);
        view_.findViewById(R.id.buttonBackToUpFragmentMeetings).setOnClickListener(this);

        // Inflate the layout for this fragment
        return view_;

    }

    @Override
    public void DataAvailable(String data) {

        if (data == null){
            return;
        }

        //Examples & source: https://github.com/google/gson/blob/master/examples/android-proguard-example/src/com/google/gson/examples/android/GsonProguardExampleActivity.java
        Gson gson = new Gson();

        Map m_data;

        try {

            m_data = new Gson().fromJson(data, Map.class);

            meetings = (List) m_data.get("objects");
            /*
            if(meetings == null){

            }else{
                meetings.addAll((List) m_data.get("objects"));
            }
            */
            //next_path = (String)((Map)m_data.get("meta")).get("next");
        }
        catch (Exception e){

            Log.e("FragmentAgenda", e.getMessage());
            Toast.makeText(getActivity(), "Datahaun yhteydessä tapahtui virhe.", Toast.LENGTH_LONG).show();
        }

        inflateMeetingsData();
        //Hide spinner to indicate loading is complete:
        getActivity().findViewById(R.id.progressBarContentLoadingFragmentMeetings).setVisibility(View.INVISIBLE);
    }

    private void inflateMeetingsData() {

        //Empty current list:
        ((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentMeetings)).removeAllViews();

        //Check that there actually are meetings:
        if(meetings.size() == 0){

            View view = getActivity().getLayoutInflater().inflate(R.layout.layout_single_meeting, null, false);
            ((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentMeetings)).addView(view);

            ((TextView)view.findViewById(R.id.textViewHeader)).setText("Ei kokouksia.");
        }

        String dates = "";
        //Loop all meetings and construct needed UI components with data:
        for (Object single_meeting:
                meetings) {

            Map temp = (Map) single_meeting;
            //String text = temp.get("date").toString() + " " + temp.get("subject").toString() + '\n';

            View view = getActivity().getLayoutInflater().inflate(R.layout.layout_single_meeting, null, false);
            ((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentMeetings)).addView(view);

            //Set data:
            //=========
            ((TextView)view.findViewById(R.id.textViewHeader)).setText("Kokous");
            //((TextView)view.findViewById(R.id.textViewDate)).setText(((Map)temp.get("meeting")).get("date").toString());
            ((TextView)view.findViewById(R.id.textViewDate)).setText(/*temp.get("id").toString()
                                                                      + "  " + */
                    temp.get("date").toString()
                                                                     /*
                                                                     + "  " +
                                                                    ((Map)temp.get("meeting")).get("id").toString()*/
            );


            //Register listeners for link:
            //View link = view.findViewById(R.id.textViewMeetingLink);
            view.setTag(temp);
            //((TextView)view.findViewById(R.id.textViewHeader)).setOnClickListener(new View.OnClickListener() {
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Object tag_data = v.getTag();

                    //Fire event to target fragment:
                    Integer meeting_id = Double.valueOf(((Map) v.getTag()).get("id").toString()).intValue();
                    ((MainActivity) getActivity()).exchange(2, meeting_id);

                    //Switch tab after clicking link
                    ((MainActivity) getActivity()).getmViewPager().setCurrentItem(2);
                }
            });
        }

    }

    @Override
    public void BinaryDataAvailable(Object data) {

    }

    @Override
    public void onClick(View v) {

        Log.i("FragmentMeetings", "onClick");

        switch (v.getId()){

            case R.id.buttonUpdateFragmentMeetings:

                //Display loading spinner and remove all children:
                getActivity().findViewById(R.id.progressBarContentLoadingFragmentMeetings).setVisibility(View.VISIBLE);
                //Clear current content
                ((LinearLayout)getActivity().findViewById(R.id.linearLayoutFragmentMeetings)).removeAllViews();
                DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/meeting/");
                break;
            case R.id.buttonBackToUpFragmentMeetings:

                ((ScrollView) getActivity().findViewById(R.id.scrollViewFragmentMeetings)).smoothScrollTo(0, 0);
                break;
        }
    }

    @Override
    public void exchange(int target, Object data) {

        //Data provided is policy maker id:
        try {

            policy_maker = (int) data;

        }catch (Exception e){

            Log.e("FragmentMeetings", "Exception:" + e.getMessage());
            return;
        }

        DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/meeting/?limit=1000&offset=0&policymaker=" + policy_maker);
    }
}

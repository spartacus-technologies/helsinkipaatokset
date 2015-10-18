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

        view_ = inflater.inflate(R.layout.fragment_meetings, container, false);

        //Request meetings data
        DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/meeting/");

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

            if(meetings == null){

                meetings = (List) m_data.get("objects");
            }else{
                meetings.addAll((List) m_data.get("objects"));
            }
            next_path = (String)((Map)m_data.get("meta")).get("next");
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
            ((TextView)view.findViewById(R.id.textViewHeader)).setText(temp.get("policymaker_name").toString());
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
            //link.setTag(temp); //TODO: add retrieved JSON as metadata
            ((TextView)view.findViewById(R.id.textViewHeader)).setTag(temp);
            ((TextView)view.findViewById(R.id.textViewHeader)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //((TextView)((View)v.getParent()).findViewById(R.id.textViewHeader)).getText()

                    Object tag_data =  v.getTag();

                    /*
                    Toast.makeText(getActivity(), "You clicked agenda item '"
                            + ((Map) tag_data).get("subject").toString()
                            + "'.", Toast.LENGTH_LONG).show();
                    */

                    //Invoke new data request in decisions fragment:
                    //Fragment fragment = getActivity().getSupportFragmentManager().getFragment(null, "FragmentName");

                    //Log.i("FragmentAgenda", "Fragment tag: " + fragment.getTag());

                    //Fire event to target fragment:
                    Integer meeting_id = Double.valueOf(((Map) v.getTag()).get("id").toString()).intValue();
                    ((MainActivity) getActivity()).exchange(1, meeting_id);

                    //Switch tab after clicking link
                    ((MainActivity) getActivity()).getmViewPager().setCurrentItem(1);
                }
            });



            /*
            ((TextView)getActivity().findViewById(R.id.textViewFragmentMainTest)).setText(dates);

            //Create wrapping container for text data:
            LinearLayout wrapper = new LinearLayout(getActivity());
            wrapper.setOrientation(LinearLayout.VERTICAL);
            wrapper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams();
            //params.width = LinearLayout.Fi
            //wrapper.setLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            //Create textView for header
            TextView header = new TextView(getActivity());
            header.setText( temp.get("policymaker_name").toString());
            header.setTextSize(18);

            //Create textView for date
            TextView date = new TextView(getActivity());
            date.setText(temp.get("date").toString());
            date.setTextSize(12);

            //Create empty view as spacer
            View spacer = new View(getActivity());
            spacer.setMinimumHeight(30);

            //Add all views to wrapper
            wrapper.addView(header);
            wrapper.addView(date);
            wrapper.addView(spacer);

            //Add wrapper to main linear layout
            ((LinearLayout) getActivity().findViewById(R.id.linearLayoutFragmentMainMeetings)).addView(wrapper);
            */
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

    }
}

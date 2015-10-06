package com.ahjo_explorer.spartacus.ahjoexplorer;

import android.app.Activity;
import android.net.Uri;
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

import com.ahjo_explorer.spartacus.ahjoexplorer.data_access.iFragmentDataExchange;
import com.google.gson.Gson;

import com.ahjo_explorer.spartacus.ahjoexplorer.data_access.DataAccess;

import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentMain.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMain#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMain extends Fragment implements View.OnClickListener, DataAccess.NetworkListener, iFragmentDataExchange, View.OnScrollChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;
    private List agenda_items;
    private String next_path;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMain.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMain newInstance(String param1, String param2) {
        FragmentMain fragment = new FragmentMain();
        Bundle args = new Bundle();
        args.putString("FragmentName", fragment.getClass().toString());
        //args.putString(ARG_PARAM2, param2);

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentMain() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_main2, container, false);

        //Register listeners
        view.findViewById(R.id.buttonTestAPI).setOnClickListener(this);
        view.findViewById(R.id.scrollView).setOnScrollChangeListener(this);
        //TODO for testing
        //DataAccess.testConnection(this);
        view.findViewById(R.id.progressBarContentLoading).setVisibility(View.INVISIBLE);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        Log.i("FragmentMain", "onClick");
        getActivity().findViewById(R.id.progressBarContentLoading).setVisibility(View.VISIBLE);
        DataAccess.requestData(this, "/paatokset/v1/agenda_item/?order_by=-meeting");
        //DataAccess.requestData(this, "/paatokset/v1/agenda_item/?order_by=-origin_last_modified_time");
    }

    @Override
    public void DataAvailable(String data) {

        //Show received JSON:
        //Examples & source: https://github.com/google/gson/blob/master/examples/android-proguard-example/src/com/google/gson/examples/android/GsonProguardExampleActivity.java
        Gson gson = new Gson();

        Map m_data;


        try {

            m_data = new Gson().fromJson(data, Map.class);
            agenda_items = (List) m_data.get("objects");
            next_path = (String)((Map)m_data.get("meta")).get("next");
        }
        catch (Exception e){

            Log.e("FragmentMain", e.getMessage());
        }
        /*
        String dates = null;

        for (Object meeting:
             meetings) {

            Log.i("FragmentMain", "Meeting:" + ((Map)meeting).get("date").toString());
            dates += ((Map)meeting).get("date").toString();
        }
        */

        //Log.v("FragmentMain", "insEmpty(): " + m_data.isEmpty());

        //Log.v("FragmentMain", "DataAvailable: " + data);

        if(data != null){

            Toast.makeText(getActivity(), "It works! Received " + data.length()*8/1000 + " kilobytes.", Toast.LENGTH_SHORT).show();
            //((TextView)getActivity().findViewById(R.id.textViewFragmentMainTest)).setText(dates);
        }
        else{

            Toast.makeText(getActivity(), "No connection. :-/", Toast.LENGTH_LONG).show();
        }

        //Call GUI data updater
        fillMeetingsData();

        //TODO: error handling

        //Hide spinner to indicate loading is complete:
        getActivity().findViewById(R.id.progressBarContentLoading).setVisibility(View.INVISIBLE);

    }

    /**
     * Function for generating all UI components for queried meetings
     */
    private void fillMeetingsData(){

        if(agenda_items == null){

            Log.e("FragmentMain", "Error: meetings data was empty!");
            return;
        }
        String dates = "";
        //Loop all meetings and construct needed UI components with data:
        for (Object agenda_item:
                agenda_items) {

            Map temp = (Map) agenda_item;
            //String text = temp.get("date").toString() + " " + temp.get("subject").toString() + '\n';

            View view = getActivity().getLayoutInflater().inflate(R.layout.layout_single_meeting, null, false);
            ((LinearLayout)getActivity().findViewById(R.id.linearLayoutFragmentMainMeetings)).addView(view);

            //Set data:
            //=========
            ((TextView)view.findViewById(R.id.textViewHeader)).setText(temp.get("subject").toString());
            //((TextView)view.findViewById(R.id.textViewDate)).setText(((Map)temp.get("meeting")).get("date").toString());
            ((TextView)view.findViewById(R.id.textViewDate)).setText(temp.get("id").toString() + "  " + ((Map)temp.get("meeting")).get("date").toString() + "  " + ((Map)temp.get("meeting")).get("id").toString());

            //Register listeners for link:
            View link = view.findViewById(R.id.textViewMeetingLink);
            link.setTag(temp); //TODO: add retrieved JSON as metadata
            link.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //((TextView)((View)v.getParent()).findViewById(R.id.textViewHeader)).getText()

                    Toast.makeText(getActivity(), "You clicked meeting '"
                            + ((Map)v.getTag()).get("policymaker_name").toString()
                            + "' but nothing to show here yet.", Toast.LENGTH_LONG).show();

                    //Invoke new data request in decisions fragment:
                    //Fragment fragment = getActivity().getSupportFragmentManager().getFragment(null, "FragmentName");

                    //Log.i("FragmentMain", "Fragment tag: " + fragment.getTag());

                    //Fire event to target fragment:
                    Integer meeting_id = Double.valueOf(((Map) v.getTag()).get("id").toString()).intValue();
                    ((MainActivity)getActivity()).exchange(1, meeting_id);

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
    public void exchange(int target, Object data) {

    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

        //Get scrollview and determine if we have reached bottom:
        ScrollView scrollview = (ScrollView) getActivity().findViewById(R.id.scrollView);
        int maxScrollAmount = scrollview.getChildAt(0).getMeasuredHeight();
        int currentScroll = scrollview.getScrollY() + scrollview.getHeight();

        Log.i("FragmentMain", "NewScroll=" + scrollY + " OldScroll=" + oldScrollY + " MaxScrollAmount=" + maxScrollAmount);
        Log.w("ScrollHeight", scrollview.getScrollY() + scrollview.getHeight() + " / " + scrollview.getChildAt(0).getMeasuredHeight());

        //We have reached the end:
        if(currentScroll >= maxScrollAmount && !isDataRequestActive()){

            //Request more data & show spinner:
            DataAccess.requestData(this, next_path);
            getActivity().findViewById(R.id.progressBarContentLoading).setVisibility(View.VISIBLE);
        }
    }

    //TODO: this is a bit ghetto
    boolean isDataRequestActive(){

        return getActivity().findViewById(R.id.progressBarContentLoading).getVisibility() == View.VISIBLE;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}

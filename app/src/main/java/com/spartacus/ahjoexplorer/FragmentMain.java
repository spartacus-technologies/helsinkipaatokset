package com.spartacus.ahjoexplorer;

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

import com.spartacus.ahjoexplorer.data_access.iFragmentDataExchange;
import com.google.gson.Gson;

import com.spartacus.ahjoexplorer.data_access.DataAccess;

import java.io.UnsupportedEncodingException;
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
public class FragmentMain extends Fragment implements View.OnClickListener, DataAccess.NetworkListener, iFragmentDataExchange /*, View.OnScrollChangeListener*/ {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;
    private List agenda_items;
    private String next_path;
    View view_ = null;

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

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        view_ = view;

        //Register listeners
        view.findViewById(R.id.buttonTestAPI).setOnClickListener(this);
        //view.findViewById(R.id.scrollView).setOnScrollChangeListener(this);
        view.findViewById(R.id.buttonBackToUpFragmentMain).setOnClickListener(this);

        //Request data if not already available
        if(agenda_items == null){

            DataAccess.requestData(this, "/paatokset/v1/agenda_item/?order_by=-meeting");
            DataAccess.requestData(this, "/paatokset/v1/agenda_item/?order_by=-meeting__date&limit=20&from_minutes=true");
            view.findViewById(R.id.progressBarContentLoadingFragmentMain).setVisibility(View.VISIBLE);
        }
        else{
            fillMeetingsData();
        }
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

        switch (v.getId()){

            case R.id.buttonTestAPI:

                //Display loading spinner and remove all children:
                getActivity().findViewById(R.id.progressBarContentLoadingFragmentMain).setVisibility(View.VISIBLE);
                //Clear current content
                ((LinearLayout)getActivity().findViewById(R.id.linearLayoutFragmentMainMeetings)).removeAllViews();
                DataAccess.requestData(this, "/paatokset/v1/agenda_item/?order_by=-meeting");
                break;
            case R.id.buttonBackToUpFragmentMain:

                ((ScrollView) getActivity().findViewById(R.id.scrollView)).smoothScrollTo(0, 0);
                break;
        }
    }

    @Override
    public void DataAvailable(String data) {

        if(data != null){

            try {
                Toast.makeText(getActivity(), "Ladattiin " + data.getBytes("UTF-8").length/1000 + " kilobittiä.", Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //((TextView)getActivity().findViewById(R.id.textViewFragmentMainTest)).setText(dates);
        }
        else{

            Toast.makeText(getActivity(), "Ei yhteyttä.", Toast.LENGTH_LONG).show();
            return;
        }


        //Show received JSON:
        //Examples & source: https://github.com/google/gson/blob/master/examples/android-proguard-example/src/com/google/gson/examples/android/GsonProguardExampleActivity.java
        Gson gson = new Gson();

        Map m_data;

        try {

            m_data = new Gson().fromJson(data, Map.class);

            if(agenda_items == null){

                agenda_items = (List) m_data.get("objects");
            }else{
                agenda_items.addAll((List) m_data.get("objects"));
            }
            next_path = (String)((Map)m_data.get("meta")).get("next");
        }
        catch (Exception e){

            Log.e("FragmentMain", e.getMessage());
            Toast.makeText(getActivity(), "Datahaun yhteydessä tapahtui virhe.", Toast.LENGTH_LONG).show();
        }

        //Call GUI data updater
        fillMeetingsData();

        //Hide spinner to indicate loading is complete:
        getActivity().findViewById(R.id.progressBarContentLoadingFragmentMain).setVisibility(View.INVISIBLE);

    }

    @Override
    public void BinaryDataAvailable(Object data) {

    }

    /**
     * Function for generating all UI components for queried agendas
     */
    private void fillMeetingsData(){

        if(agenda_items == null){

            Log.e("FragmentMain", "Error: meetings data was empty!");
            return;
        }

        //Empty current list:
        ((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentMainMeetings)).removeAllViews();

        String dates = "";
        //Loop all meetings and construct needed UI components with data:
        for (Object agenda_item:
                agenda_items) {

            Map temp = (Map) agenda_item;
            //String text = temp.get("date").toString() + " " + temp.get("subject").toString() + '\n';

            View view = getActivity().getLayoutInflater().inflate(R.layout.layout_single_meeting, null, false);
            ((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentMainMeetings)).addView(view);

            //Set data:
            //=========
            ((TextView)view.findViewById(R.id.textViewHeader)).setText(temp.get("subject").toString());
            //((TextView)view.findViewById(R.id.textViewDate)).setText(((Map)temp.get("meeting")).get("date").toString());
            ((TextView)view.findViewById(R.id.textViewDate)).setText(/*temp.get("id").toString()
                                                                      + "  " + */
                                                                    ((Map)temp.get("meeting")).get("date").toString()
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

                    //Log.i("FragmentMain", "Fragment tag: " + fragment.getTag());

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
    public void exchange(int target, Object data) {

    }

    //@Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

        //Get scrollview and determine if we have reached bottom:
        ScrollView scrollview = (ScrollView) view_.findViewById(R.id.scrollView);
        int maxScrollAmount = scrollview.getChildAt(0).getMeasuredHeight();
        int scrollViewHeight = scrollview.getHeight();
        int currentScroll = scrollViewHeight + scrollview.getScrollY();

        Log.i("FragmentMain", "NewScroll=" + scrollY + " OldScroll=" + oldScrollY + " MaxScrollAmount=" + maxScrollAmount);
        Log.w("ScrollHeight", scrollview.getScrollY() + scrollview.getHeight() + " / " + scrollview.getChildAt(0).getMeasuredHeight());

        //We have reached the end:
        if(currentScroll >= maxScrollAmount && !isDataRequestActive()){

            //Request more data & show spinner:
            DataAccess.requestData(this, next_path);
            getActivity().findViewById(R.id.progressBarContentLoadingFragmentMain).setVisibility(View.VISIBLE);
        }
    }

    //TODO: this is a bit ghetto
    boolean isDataRequestActive(){

        return getActivity().findViewById(R.id.progressBarContentLoadingFragmentMain).getVisibility() == View.VISIBLE;
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

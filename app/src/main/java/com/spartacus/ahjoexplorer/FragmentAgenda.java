package com.spartacus.ahjoexplorer;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.spartacus.ahjoexplorer.data_access.iFragmentDataExchange;
import com.google.gson.Gson;

import com.spartacus.ahjoexplorer.data_access.DataAccess;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentAgenda.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentAgenda#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAgenda extends Fragment implements View.OnClickListener, DataAccess.NetworkListener, iFragmentDataExchange /*, View.OnScrollChangeListener*/ {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;
    private List agenda_items;
    private String next_path;
    View view_ = null;
    View popup;
    boolean isPopupVisible = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentAgenda.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAgenda newInstance() {
        FragmentAgenda fragment = new FragmentAgenda();
        Bundle args = new Bundle();
        args.putString("FragmentName", fragment.getClass().toString());
        //args.putString(ARG_PARAM2, param2);

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentAgenda() {
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

        View view = inflater.inflate(R.layout.fragment_agenda, container, false);
        view_ = view;

        //Register listeners
        view.findViewById(R.id.buttonTestAPIFragmentAgenda).setOnClickListener(this);
        //view.findViewById(R.id.scrollView).setOnScrollChangeListener(this);
        view.findViewById(R.id.buttonControlBottomFragmentAgenda).setOnClickListener(this);
        view_.setOnClickListener(this);

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

        Log.i("FragmentAgenda", "onClick");

        switch (v.getId()){

            case R.id.buttonTestAPIFragmentAgenda:

                //Display loading spinner and remove all children:
                getActivity().findViewById(R.id.progressBarContentLoadingFragmentAgenda).setVisibility(View.VISIBLE);
                //Clear current content
                ((LinearLayout)getActivity().findViewById(R.id.linearLayoutFragmentAgenda)).removeAllViews();
                DataAccess.requestData(this, "/paatokset/v1/agenda_item/?order_by=-meeting");
                break;
            case R.id.buttonControlBottomFragmentAgenda:

                if(popup != null){
                    ((TextView)view_.findViewById(R.id.buttonControlBottomFragmentAgenda)).setText("↑ ylös");
                    ((FrameLayout) view_).removeView(popup);
                    popup = null;
                }
                else{

                    ((ScrollView) getActivity().findViewById(R.id.scrollViewFragmentAgenda)).smoothScrollTo(0, 0);
                }
                break;
        }

        if(popup != null) ((FrameLayout)view_).removeView(popup);

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

            Log.e("FragmentAgenda", e.getMessage());
            Toast.makeText(getActivity(), "Datahaun yhteydessä tapahtui virhe.", Toast.LENGTH_LONG).show();
        }

        //Call GUI data updater
        fillMeetingsData();

        //Hide spinner to indicate loading is complete:
        getActivity().findViewById(R.id.progressBarContentLoadingFragmentAgenda).setVisibility(View.INVISIBLE);

    }

    @Override
    public void BinaryDataAvailable(Object data) {

    }

    /**
     * Function for generating all UI components for queried agendas
     */
    private void fillMeetingsData(){

        if(agenda_items == null){

            Log.e("FragmentAgenda", "Error: meetings data was empty!");
            return;
        }

        //Empty current list:
        ((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentAgenda)).removeAllViews();

        String dates = "";
        //Loop all meetings and construct needed UI components with data:
        for (Object agenda_item:
                agenda_items) {

            Map temp = (Map) agenda_item;
            //String text = temp.get("date").toString() + " " + temp.get("subject").toString() + '\n';

            View view = getActivity().getLayoutInflater().inflate(R.layout.layout_single_meeting, null, false);
            ((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentAgenda)).addView(view);

            //Set data:
            //=========
            ((TextView)view.findViewById(R.id.textViewHeader)).setText(temp.get("subject").toString());
            //((TextView)view.findViewById(R.id.textViewDate)).setText(((Map)temp.get("meeting")).get("date").toString());
            view.findViewById(R.id.textViewDate).setVisibility(View.INVISIBLE);

            view.setTag(temp); //add retrieved JSON as metadata

            //Register listeners for link:
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //((TextView)((View)v.getParent()).findViewById(R.id.textViewHeader)).getText()

                    Map tag_data = (Map) v.getTag();

                    Log.i("FragmentAgenda", tag_data.get("subject").toString());
                    //Fire event to target fragment:
                    Integer meeting_id = Double.valueOf(((Map) v.getTag()).get("id").toString()).intValue();
                    //((MainActivity) getActivity()).exchange(1, meeting_id);


                    if(popup != null){

                        ((FrameLayout) view_).removeView(popup);
                        popup = null;
                        return;
                    }
                    String agenda_data = ((Map)((List)tag_data.get("content")).get(0)).get("text").toString();
                    popup = getActivity().getLayoutInflater().inflate(R.layout.pop_up_agenda, null, false);
                    ((TextView)popup.findViewById(R.id.textViewHederPopup)).setText(tag_data.get("subject").toString());

                    //Attachments
                    //===========

                    if(((List)tag_data.get("attachments")).size() == 0){

                        TextView tv =new TextView(getActivity());
                        tv.setText("(ei liitteitä)");
                        ((LinearLayout) popup.findViewById(R.id.linearLayoutAttachmentsPopupAgenda)).addView(tv);

                    }else{

                        LinearLayout container = (LinearLayout) popup.findViewById(R.id.linearLayoutAttachmentsPopupAgenda);

                        for (Object attachment : (List)tag_data.get("attachments")) {

                            TextView tv_att = new TextView(getActivity());
                            TextView tv_type = new TextView(getActivity());

                            LinearLayout inner_container = new LinearLayout(getActivity());
                            inner_container.setLayoutParams(container.getLayoutParams());
                            inner_container.setOrientation(LinearLayout.VERTICAL);

                            //Check if attachment is public
                            if(!(boolean)((Map)attachment).get("public")){

                                tv_att.setText("(sisältö ei julkisesti saatavilla)");
                            }
                            else{

                                String html_data = "<a href=" + ((Map)attachment).get("file_uri").toString() + ">" + ((Map)attachment).get("name").toString()
                                        + " (" + ((Map)attachment).get("file_type").toString() + ")";
                                tv_att.setText(Html.fromHtml(html_data));
                                tv_att.setMovementMethod(LinkMovementMethod.getInstance());

                                tv_type.setText(" (" + ((Map)attachment).get("file_type").toString() + ")");

                                //tv_att.setText(((Map) attachment).get("name").toString());
                            }
                            inner_container.addView(tv_att);
                            //inner_container.addView(tv_type);
                            container.addView(inner_container);
                        }
                    }




                    ((WebView)popup.findViewById(R.id.webViewPopup)).loadDataWithBaseURL(null, agenda_data, "text/html", "UTF-8", null);
                    ((FrameLayout) view_).addView(popup);

                    //Set button text for lower control button:
                    ((TextView)view_.findViewById(R.id.buttonControlBottomFragmentAgenda)).setText("Sulje");
                }
            });
        }

    }
    @Override
    public void exchange(int target, Object data) {

        Log.i("FragmentAgenda", "exchange");
        DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/agenda_item/?limit=1000&offset=0&show_all=1&meeting=" + (int) data);
        view_.findViewById(R.id.progressBarContentLoadingFragmentAgenda).setVisibility(View.VISIBLE);
    }

    //@Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

        //Get scrollview and determine if we have reached bottom:
        ScrollView scrollview = (ScrollView) view_.findViewById(R.id.scrollView);
        int maxScrollAmount = scrollview.getChildAt(0).getMeasuredHeight();
        int scrollViewHeight = scrollview.getHeight();
        int currentScroll = scrollViewHeight + scrollview.getScrollY();

        Log.i("FragmentAgenda", "NewScroll=" + scrollY + " OldScroll=" + oldScrollY + " MaxScrollAmount=" + maxScrollAmount);
        Log.w("ScrollHeight", scrollview.getScrollY() + scrollview.getHeight() + " / " + scrollview.getChildAt(0).getMeasuredHeight());

        //We have reached the end:
        if(currentScroll >= maxScrollAmount && !isDataRequestActive()){

            //Request more data & show spinner:
            DataAccess.requestData(this, next_path);
            getActivity().findViewById(R.id.progressBarContentLoadingFragmentAgenda).setVisibility(View.VISIBLE);
        }
    }

    //TODO: this is a bit ghetto
    boolean isDataRequestActive(){

        return getActivity().findViewById(R.id.progressBarContentLoadingFragmentAgenda).getVisibility() == View.VISIBLE;
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

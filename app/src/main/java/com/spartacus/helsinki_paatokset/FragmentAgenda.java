package com.spartacus.helsinki_paatokset;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.spartacus.helsinki_paatokset.data_access.ConfigurationManager;
import com.spartacus.helsinki_paatokset.data_access.DataAccess;
import com.spartacus.helsinki_paatokset.data_access.iFragmentDataExchange;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private List video_items;

    private String next_path;
    View view_ = null;
    View popup;
    boolean isPopupVisible = false;
    private String video_url;
    private String screen_shot_uri;

    String primary_video_source = null;

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

        ConfigurationManager.initialize(getActivity());
        primary_video_source = ConfigurationManager.getVideoSource();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_agenda, container, false);
        view_ = view;

        //Register listeners
        view.findViewById(R.id.buttonTestAPIFragmentAgenda).setOnClickListener(this);
        view.findViewById(R.id.buttonPlayVideoMP4FragmentAgenda).setOnClickListener(this);
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
                DataAccess.requestData(this, "/paatokset/v1/agenda_item/?order_by=-meeting", RequestType.MEETING);
                break;
            case R.id.buttonControlBottomFragmentAgenda:

                if(popup != null){

                    //((TextView)view_.findViewById(R.id.buttonControlBottomFragmentAgenda)).setText("↑ ylös");
                    ((RelativeLayout) view_).removeView(popup);
                    popup = null;
                }
                else{

                    ((ScrollView) getActivity().findViewById(R.id.scrollViewFragmentAgenda)).smoothScrollTo(0, 0);
                }
                break;

            case R.id.buttonPlayVideoMP4FragmentAgenda:

                Uri uri = Uri.parse(video_url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {

                    Log.w("FragmentDefault", "onClick():ActivityNotFoundException");
                    Toast.makeText(getActivity(), "Warning: video player not found. Consider installing MX Player.", Toast.LENGTH_LONG).show();
                }

                break;
        }

        if(popup != null) ((RelativeLayout)view_).removeView(popup);

    }

    @Override
    public void DataAvailable(String data, RequestType type) {

        if(data == null){

            Toast.makeText(getActivity(), "Ei yhteyttä.", Toast.LENGTH_LONG).show();
            return;
        }
        //Map object containing parsed JSON:
        Map m_data;

        switch (type) {

            case AGENDAS:

                try {

                    m_data = new Gson().fromJson(data, Map.class);

                    agenda_items = (List) m_data.get("objects");

                }
                catch (Exception e){

                    Log.e("FragmentAgenda", e.getMessage());
                    Toast.makeText(getActivity(), "Datahaun yhteydessä tapahtui virhe.", Toast.LENGTH_LONG).show();
                }

                try{

                    fillMeetingsData();
                    //Hide spinner to indicate loading is complete:
                    getActivity().findViewById(R.id.progressBarContentLoadingFragmentAgenda).setVisibility(View.INVISIBLE);

                }catch (Exception e){
                    Log.e("FragmentAgenda", "Exception:" + e.getMessage());
                    return;
                }
                //Call GUI data updater


                break;

            case VIDEO:

                Log.i("FragmentAgenda", "DataAvailable: VIDEO");

                primary_video_source = ConfigurationManager.getVideoSource();

                //Video data arrived -> check whether video exists:
                m_data = new Gson().fromJson(data, Map.class);
                video_items = (List) m_data.get("objects");

                if(video_items.size() > 0){

                    String source_mp4 = null;
                    String source_ogv = null;
                    String source_hki = null;

                    try{
                        source_mp4 = ((Map)((Map)video_items.get(0)).get("local_copies")).get("video/mp4").toString();
                    }catch(Exception e){}

                    try{
                        source_ogv = ((Map)((Map)video_items.get(0)).get("local_copies")).get("video/ogg").toString();
                    }catch(Exception e){}

                    try{
                        source_hki = ((Map)video_items.get(0)).get("url").toString();
                    }catch(Exception e){}


                    if(primary_video_source.equals("MP4")){

                        if(source_mp4 != null){

                            video_url = source_mp4;

                        }else if(source_ogv != null){

                            video_url = source_ogv;
                        }
                        else if(source_hki != null){

                            video_url = source_hki;
                        }
                    }
                    if(primary_video_source.equals("OGV")){

                        if(source_ogv != null){

                            video_url = source_ogv;

                        }else if(source_mp4 != null){

                            video_url = source_mp4;
                        }
                        else if(source_hki != null){

                            video_url = source_hki;
                        }
                    }
                    if(primary_video_source.equals("HKI")){

                        if(source_hki != null){

                            video_url = source_hki;

                        }else if(source_mp4 != null){

                            video_url = source_mp4;
                        }
                        else if(source_ogv != null){

                            video_url = source_ogv;
                        }
                    }
                    //video_url = ((Map)((Map)video_items.get(0)).get("local_copies")).get("video/mp4").toString();
                    screen_shot_uri = ((Map)video_items.get(0)).get("screenshot_uri").toString();

                    if(screen_shot_uri != ""){

                        DataAccess.requestImageData(this, screen_shot_uri, RequestType.IMAGE);
                    }


                    Log.i("FragmentAgenda", "DataAvailable: VIDEO URL " + video_url);

                    if(video_url != ""){

                        //Display video button
                        view_.findViewById(R.id.buttonPlayVideoMP4FragmentAgenda).setVisibility(View.VISIBLE);
                        //view_.findViewById(R.id.textViewVideoDataAvailabilityMessageFragmentAgenda).setVisibility(View.INVISIBLE);
                        view_.findViewById(R.id.textViewVideoDataAvailabilityMessageFragmentAgenda).setVisibility(View.VISIBLE);
                        ((TextView) view_.findViewById(R.id.textViewVideoDataAvailabilityMessageFragmentAgenda)).setText(video_url);

                    }
                }else{

                    view_.findViewById(R.id.textViewVideoDataAvailabilityMessageFragmentAgenda).setVisibility(View.VISIBLE);
                    ((TextView) view_.findViewById(R.id.textViewVideoDataAvailabilityMessageFragmentAgenda)).setText("Video ei saatavilla.");
                }


                break;

            case IMAGE:

                break;

            default:

                Log.e("FragmentAgenda", "Unknown response type.");
                break;
        }

        //Show received JSON:
        //Examples & source: https://github.com/google/gson/blob/master/examples/android-proguard-example/src/com/google/gson/examples/android/GsonProguardExampleActivity.java


    }

    @Override
    public void BinaryDataAvailable(Object data, RequestType type) {

        //Bitmap received: add to layout
        if(data == null){

            //TODO: set message here that request failed
            Bitmap err_image = BitmapFactory.decodeResource(getResources(), R.mipmap.place_holder_error);
            ((ImageView) view_.findViewById(R.id.imageViewVideoPreviewFragmentAgenda)).setImageBitmap(err_image);

            return;
        }
        ((ImageView) view_.findViewById(R.id.imageViewVideoPreviewFragmentAgenda)).setImageBitmap((Bitmap) data);
    }

    /**
     * Function for generating all UI components for queried agendas
     */
    private void fillMeetingsData(){

        if(agenda_items == null){

            Log.e("FragmentAgenda", "Error: meetings data was empty!");
            return;
        }

        //Create headers & date:
        if(agenda_items.size() > 0){

            String policy_maker = ((Map)((Map)agenda_items.get(0)).get("meeting")).get("policymaker_name").toString();



            String date_str = ((Map)((Map)agenda_items.get(0)).get("meeting")).get("date").toString();

            //Do some formatting:
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat df_print = new SimpleDateFormat("dd.MM.yyyy");
            try {

                Date result =  df.parse(date_str);
                date_str = df_print.format(result);

            }catch (Exception e){

            }

            ((TextView)view_.findViewById(R.id.textViewFragmentAgendaDate)).setText(date_str);
            ((TextView)view_.findViewById(R.id.textViewPolicyMakerFragmentAgenda)).setText(policy_maker);
        }

        //Empty current list:
        ((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentAgenda)).removeAllViews();

        String dates = "";
        //Loop all meetings and construct needed UI components with data:
        for (Object agenda_item:
                agenda_items) {

            Map temp = (Map) agenda_item;
            //String text = temp.get("date").toString() + " " + temp.get("subject").toString() + '\n';

            View view = getActivity().getLayoutInflater().inflate(R.layout.layout_list_item, null, false);
            final Integer agenda_item_id = Double.valueOf(temp.get("id").toString()).intValue();
            ((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentAgenda)).addView(view);

            //final Integer meeting_id = Double.valueOf(temp.get("id").toString()).intValue();

            //((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentMeetings)).addView(view);

            //Set fav state based on configuration:
            ImageView iv = (ImageView) view.findViewById(R.id.imageViewFavIcon);
            if (ConfigurationManager.getIsFav("agenda_item_id=" + agenda_item_id)) {

                iv.setImageResource(R.mipmap.fav_icon_selected);
            }
            else{
                iv.setImageResource(R.mipmap.fav_icon_unselected);
            }

            view.findViewById(R.id.relativeLayoutListItem).setOnClickListener(new View.OnClickListener() {


                int state = 0;

                @Override
                public void onClick(View v) {

                    ImageView iv = (ImageView) v.findViewById(R.id.imageViewFavIcon);
                    if (state == 0) {

                        ConfigurationManager.setIsFav("agenda_item_id=" + agenda_item_id, true);
                        iv.setImageResource(R.mipmap.fav_icon_selected);
                        state = 1;
                    } else {
                        state = 0;
                        ConfigurationManager.setIsFav("agenda_item_id=" + agenda_item_id, false);
                        iv.setImageResource(R.mipmap.fav_icon_unselected);
                    }

                }
            });

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
                    //((MainActivity) getActivity()).exchange(1, meeting_id);


                    if(popup != null){

                        ((RelativeLayout) view_).removeView(popup);
                        popup = null;
                        return;
                    }
                    String agenda_data = ((Map)((List)tag_data.get("content")).get(0)).get("text").toString();
                    popup = getActivity().getLayoutInflater().inflate(R.layout.pop_up_agenda, null, false);
                    ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    popup.setLayoutParams(params);

                    ((TextView) popup.findViewById(R.id.textViewHederPopup)).setText(tag_data.get("subject").toString());
                    popup.findViewById(R.id.imageButtonClosePopupAgenda).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ((RelativeLayout) view_).removeView(popup);
                            popup = null;
                        }
                    });
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
                            }
                            inner_container.addView(tv_att);
                            //inner_container.addView(tv_type);
                            container.addView(inner_container);
                        }
                    }

                    ((WebView)popup.findViewById(R.id.webViewPopup)).loadDataWithBaseURL(null, agenda_data, "text/html", "UTF-8", null);
                    ((RelativeLayout) view_).addView(popup);

                    //Set button text for lower control button:
                    //((TextView)view_.findViewById(R.id.buttonControlBottomFragmentAgenda)).setText("Sulje");
                }
            });
        }

    }
    @Override
    public void exchange(int target, Object data) {

        Log.i("FragmentAgenda", "exchange");
        DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/agenda_item/?limit=1000&offset=0&show_all=1&meeting=" + (int) data, RequestType.AGENDAS);
        DataAccess.requestData(this, "http://dev.hel.fi:80/paatokset/v1/video/?meeting=" + (int) data, RequestType.VIDEO);
        view_.findViewById(R.id.progressBarContentLoadingFragmentAgenda).setVisibility(View.VISIBLE);

        //Hide video button
        view_.findViewById(R.id.buttonPlayVideoMP4FragmentAgenda).setVisibility(View.INVISIBLE);

        //If popup is visible (most likely some other meeting, hide it)
        if(view_ != null){

            //Empty current list:
            ((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentAgenda)).removeAllViews();

            ((RelativeLayout) view_).removeView(popup);

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.place_holder_new);
            ((ImageView) view_.findViewById(R.id.imageViewVideoPreviewFragmentAgenda)).setImageBitmap(bm);
        }
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
            DataAccess.requestData(this, next_path, null);
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

package com.spartacus.helsinki_paatokset;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.spartacus.helsinki_paatokset.data_access.ConfigurationManager;
import com.spartacus.helsinki_paatokset.data_access.CustomDictionary;
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
 * to handle interaction events.
 * Use the {@link FragmentMeetings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMeetings extends Fragment implements View.OnClickListener, DataAccess.NetworkListener, iFragmentDataExchange /*, TextWatcher*/ {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "FragmentMeetings";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view_;
    private String next_path;

    private static List meetings;           //TODO: remove staticness and pass information some other means
    private List meeetings_video_data;

    //Map m_data = null;

    private static CustomDictionary dictionary;

    private int policy_maker = -1;

    //Progressbar test
    int total_count = 1;
    int current_progress = 0;

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
        if(dictionary == null){

            dictionary = new CustomDictionary();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //view_ = inflater.inflate(R.layout.splashscreen, container, false);

        //return view_;
        if(view_ == null)
            view_ = inflater.inflate(R.layout.fragment_meetings, container, false);

        final ScrollView scrollView = (ScrollView) view_.findViewById(R.id.scrollViewFragmentMeetings);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {

                int scrollX = scrollView.getScrollX(); //for horizontalScrollView
                int scrollY = scrollView.getScrollY(); //for verticalScrollView
                //DO SOMETHING WITH THE SCROLL COORDINATES
                //Log.i(TAG, "ScrollY= " + scrollY);

                //Use some fancy animation
                Animation fade_in = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                Animation fade_out = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);

                //If scroll is someting else than top, show scroll to top button:
                if(scrollY > 0 && view_.findViewById(R.id.buttonBackToUpFragmentMeetings).getVisibility() != View.VISIBLE){

                    view_.findViewById(R.id.buttonBackToUpFragmentMeetings).startAnimation(fade_in);
                    view_.findViewById(R.id.buttonBackToUpFragmentMeetings).setVisibility(View.VISIBLE);
                }
                else if(scrollY <= 0 &&  view_.findViewById(R.id.buttonBackToUpFragmentMeetings).getVisibility() != View.GONE){
                    view_.findViewById(R.id.buttonBackToUpFragmentMeetings).startAnimation(fade_out);
                    view_.findViewById(R.id.buttonBackToUpFragmentMeetings).setVisibility(View.GONE);
                }

            }
        });
        
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
        //((EditText)view_.findViewById(R.id.editTextSearchFragmentMeetings)).addTextChangedListener(this);



        inflateMeetingsData();

        // Inflate the layout for this fragment
        return view_;

    }

    @Override
    public void DataAvailable(String data, RequestType type) {

        if (data == null){
            return;
        }
        Map m_data;
        //Examples & source: https://github.com/google/gson/blob/master/examples/android-proguard-example/src/com/google/gson/examples/android/GsonProguardExampleActivity.java
        switch (type){

            case MEETING:

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

                break;

            case VIDEO:

                Log.w("FragmentMeetings", "Video metadata");
                meeetings_video_data = (List) new Gson().fromJson(data, Map.class).get("objects");

                current_progress += 1;

                try{

                    updateProgressBar();
                    fillMeetingMetaData();

                }catch (Exception e){

                    Log.e("FragmentMeetings", e.getMessage());
                    return;
                }
                break;
            case AGENDAS:
                break;
            case VIDEO_PREVIEW:
                break;

            case AGENDA_ITEM:

                //Agenda item received: push to dictionary for searching
                //dictionary.addData();

                try {

                    m_data = new Gson().fromJson(data, Map.class);

                    List agenda_items = (List) m_data.get("objects");

                    if(agenda_items.size() > 0){

                        Integer meeting_id = (int)Double.parseDouble(((Map) ((Map) agenda_items.get(0)).get("meeting")).get("id").toString());

                        //dictionary.addData(meeting_id, data);
                    }
                    //Get meeting id:
                }
                catch (Exception e){

                    Log.e("FragmentAgenda", e.getMessage());
                }

                current_progress += 1;
                updateProgressBar();
                //fillMeetingMetaData(data);

                break;

            case POLICY_MAKERS:
                break;
            case POLICY_MAKER:
                break;
            case IMAGE:
                break;
        }
    }

    //Loop meetings and add meta data once match is found:
    private void fillMeetingMetaData() {

        //Map data_m = new Gson().fromJson(data, Map.class);

        //Check for total count: -> return if no video data available
        if(meeetings_video_data == null || meeetings_video_data.size() == 0) return;

        LinearLayout container = ((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentMeetings));

        String tmp =  ((Map)meeetings_video_data.get(0)).get("meeting").toString();
        tmp = tmp.substring(0, tmp.length() - 1);
        tmp = tmp.substring(tmp.lastIndexOf("/") + 1, tmp.length());
        Integer meeting_id = Double.valueOf(tmp).intValue();


        for(int i = 0; i < container.getChildCount(); ++i){

            View view  = container.getChildAt(i);
            if(Double.valueOf(((Map) view.getTag()).get("id").toString()).intValue() == meeting_id){

                //TextView footer = (TextView) view.findViewById(R.id.textViewFooterText);
                View video_icon= view.findViewById(R.id.imageViewVideoAvailable);
                video_icon.setVisibility(View.VISIBLE);
                //footer.setText("vid");
            }
        }
    }

    private void inflateMeetingsData() {

        //Empty current list:
        ((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentMeetings)).removeAllViews();

        //First run -> return
        if(meetings == null) return;

        //Check that there actually are meetings:
        if(meetings.size() == 0){

            View view = getActivity().getLayoutInflater().inflate(R.layout.layout_list_item, null, false);
            ((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentMeetings)).addView(view);

            ((TextView)view.findViewById(R.id.textViewHeader)).setText("Ei kokouksia.");
        }
        //Update total count
        total_count = meetings.size();// *2;
        current_progress = 0;
        updateProgressBar();

        String dates = "";
        //Loop all meetings and construct needed UI components with data:
        for (Object single_meeting:
                meetings) {

            Map temp = (Map) single_meeting;
            //String text = temp.get("date").toString() + " " + temp.get("subject").toString() + '\n';

            final Integer meeting_id = Double.valueOf(temp.get("id").toString()).intValue();
            View view = getActivity().getLayoutInflater().inflate(R.layout.layout_list_item, null, false);
            ((LinearLayout)view_.findViewById(R.id.linearLayoutFragmentMeetings)).addView(view);

            //Set fav state based on configuration:
            ImageView iv = (ImageView) view.findViewById(R.id.imageViewFavIcon);
            if (ConfigurationManager.getIsFav("meeting_id=" + meeting_id)) {

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

                        ConfigurationManager.setIsFav("meeting_id=" + meeting_id, true);
                        iv.setImageResource(R.mipmap.fav_icon_selected);
                        state = 1;
                    } else {
                        state = 0;
                        ConfigurationManager.setIsFav("meeting_id=" + meeting_id, false);
                        iv.setImageResource(R.mipmap.fav_icon_unselected);
                    }

                }
            });

            //Set data:
            //=========

            //Do some formatting first:
            String date_str = "";
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat df_print = new SimpleDateFormat("dd.MM.yyyy");
            try {

            Date result =  df.parse(temp.get("date").toString());
                date_str = df_print.format(result);
            }catch (Exception e){

                date_str = temp.get("date").toString();
            }

            ((TextView)view.findViewById(R.id.textViewHeader)).setText("Kokous");
            //((TextView)view.findViewById(R.id.textViewDate)).setText(((Map)temp.get("meeting")).get("date").toString());
            ((TextView)view.findViewById(R.id.textViewDate)).setText(/*temp.get("id").toString()
                                                                      + "  " + */
                    date_str
                                                                     /*
                                                                     + "  " +
                                                                    ((Map)temp.get("meeting")).get("id").toString()*/
            );


            //Query for meeting specific data:
            //DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/agenda_item/?limit=1000&offset=0&show_all=1&meeting=" + meeting_id, RequestType.AGENDA_ITEM);
            DataAccess.requestData(this, "http://dev.hel.fi:80/paatokset/v1/video/?meeting=" + meeting_id, RequestType.VIDEO);

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

    private void updateProgressBar() {

        if(total_count == 0){

            ((ProgressBar)view_.findViewById(R.id.progressBarAgendaItemLoading)).setProgress(0);
            return;
        }

        ((ProgressBar)view_.findViewById(R.id.progressBarAgendaItemLoading)).setProgress(100*current_progress/total_count);
    }

    @Override
    public void BinaryDataAvailable(Object data, RequestType type) {

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
                DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/meeting/", RequestType.MEETING);
                break;
            case R.id.buttonBackToUpFragmentMeetings:

                ((ScrollView) getActivity().findViewById(R.id.scrollViewFragmentMeetings)).smoothScrollTo(0, 0);
                break;
        }
    }

    @Override
    public void exchange(int target, Object data) {

        //Clear any current list of meetings:
        meetings = null;

        //Data provided is policy maker id:
        try {

            policy_maker = (int) data;

        }catch (Exception e){

            Log.e("FragmentMeetings", "Exception:" + e.getMessage());
            return;
        }

        DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/meeting/?limit=1000&offset=0&policymaker=" + policy_maker, RequestType.MEETING, 1000);
        ((LinearLayout)getActivity().findViewById(R.id.linearLayoutFragmentMeetings)).removeAllViews();
        getActivity().findViewById(R.id.progressBarContentLoadingFragmentMeetings).setVisibility(View.VISIBLE);
    }
    /*
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        CustomDictionary.Tuple<Integer, String> matching_meetings = dictionary.searchForMeeting(s.toString());

        for (:
        ){

        }
        dictionary.

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    */
}

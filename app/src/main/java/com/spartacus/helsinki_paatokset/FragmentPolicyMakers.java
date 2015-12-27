package com.spartacus.helsinki_paatokset;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.spartacus.helsinki_paatokset.data_access.ConfigurationManager;
import com.spartacus.helsinki_paatokset.data_access.DataAccess;
import com.spartacus.helsinki_paatokset.data_access.ListItem;
import com.spartacus.helsinki_paatokset.data_access.iFragmentDataExchange;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentPolicyMakers interface
 * to handle interaction events.
 * Use the {@link FragmentPolicyMakers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPolicyMakers extends Fragment implements View.OnClickListener, DataAccess.NetworkListener, iFragmentDataExchange, TextWatcher {

    private View view_;
    private List policy_makers;
    private String TAG = "FragmentPolicyMakers";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentPolicyMakers.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPolicyMakers newInstance() {
        FragmentPolicyMakers fragment = new FragmentPolicyMakers();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentPolicyMakers() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfigurationManager.initialize(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view_ = inflater.inflate(R.layout.fragment_policy_makers, container, false);

        //Register listeners
        view_.findViewById(R.id.buttonUpdateFragmentPolicyMakers).setOnClickListener(this);
        //view.findViewById(R.id.scrollView).setOnScrollChangeListener(this);
        view_.findViewById(R.id.buttonBackToUpFragmentPolicyMakers).setOnClickListener(this);
        ((EditText)view_.findViewById(R.id.editTextSearchFragmentPolicyMakers)).addTextChangedListener(this);

        final ScrollView scrollView = (ScrollView) view_.findViewById(R.id.scrollViewFragmentPolicyMakers);

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
                if(scrollY > 0 && view_.findViewById(R.id.buttonBackToUpFragmentPolicyMakers).getVisibility() != View.VISIBLE){

                    view_.findViewById(R.id.buttonBackToUpFragmentPolicyMakers).startAnimation(fade_in);
                    view_.findViewById(R.id.buttonBackToUpFragmentPolicyMakers).setVisibility(View.VISIBLE);
                }
                else if(scrollY <= 0 &&  view_.findViewById(R.id.buttonBackToUpFragmentPolicyMakers).getVisibility() != View.GONE){
                    view_.findViewById(R.id.buttonBackToUpFragmentPolicyMakers).startAnimation(fade_out);
                    view_.findViewById(R.id.buttonBackToUpFragmentPolicyMakers).setVisibility(View.GONE);
                }

            }
        });

        //Request data:
        if(policy_makers == null){

            view_.findViewById(R.id.progressBarContentLoadingFragmentPolicyMakers).setVisibility(View.VISIBLE);
            DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/policymaker/?limit=200", RequestType.POLICY_MAKERS, 1000);
        }
        else
            inflatePolicyMakerData();

        return view_;
    }


    @Override
    public void DataAvailable(String data, RequestType type) {

        Log.i("FragmentPolicyMakers", "DataAvailable");

        if(data == null){

            Toast.makeText(getActivity(), "Ei yhteyttä", Toast.LENGTH_SHORT).show();
            view_.findViewById(R.id.progressBarContentLoadingFragmentPolicyMakers).setVisibility(View.INVISIBLE);
            return;
        }

        switch (type){

            case MEETING:
                break;
            case VIDEO:
                break;
            case AGENDAS:
                break;
            case VIDEO_PREVIEW:
                break;
            case AGENDA_ITEM:
                break;
            case POLICY_MAKERS:

                Map m_data;

                try {

                    m_data = new Gson().fromJson(data, Map.class);

                    if(policy_makers == null){

                        policy_makers = (List) m_data.get("objects");
                    }else{

                        policy_makers.addAll((List) m_data.get("objects"));
                    }
                }
                catch (Exception e){

                    Log.e("FragmentPolicyMakers", e.getMessage());
                    Toast.makeText(getActivity(), "Datahaun yhteydessä tapahtui virhe.", Toast.LENGTH_LONG).show();
                }

                customSortingOfPolicyMakers();

                //All good ->  inflate views
                inflatePolicyMakerData();

                //Hide loading animation:
                view_.findViewById(R.id.progressBarContentLoadingFragmentPolicyMakers).setVisibility(View.INVISIBLE);
                break;

            case POLICY_MAKER:

                m_data = new Gson().fromJson(data, Map.class);
                int size = ((List)m_data.get("objects")).size();

                //Skip if empty response:
                if(size == 0) return;

                String latest_meeting = ((Map)((List)m_data.get("objects")).get(0)).get("date").toString();
                String policy_maker_name = ((Map)((List)m_data.get("objects")).get(0)).get("policymaker_name").toString();

                //Do some formatting:
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat df_print = new SimpleDateFormat("dd.MM.yyyy");
                try {

                    Date result =  df.parse(latest_meeting);
                    latest_meeting = df_print.format(result);

                }catch (Exception e){

                }

                //Loop all policymakers and set visibilitystate according to user input:
                //TODO: inefficient!?
                LinearLayout container = (LinearLayout) view_.findViewById(R.id.linearLayoutFragmentPolicyMakers);
                int count = container.getChildCount();
                View v = null;
                for(int i=0; i<count; i++) {

                    v = container.getChildAt(i);
                    TextView header = (TextView)v.findViewById(R.id.textViewHeader);
                    String pol_maker_tmp = header.getText().toString();
                    TextView footer = (TextView)v.findViewById(R.id.textViewFooterText);

                    if(pol_maker_tmp.toLowerCase().contains(policy_maker_name.toLowerCase())){
                        try{

                            footer.setText(size + " kokousta, viimeisin " + latest_meeting);
                        }catch (Exception e){

                            Log.e("FragmentPolicyMakers", "Exception " + e.getStackTrace());
                        }
                        //header.setText("jeejee");
                        break;
                    }
                }

                break;

            case IMAGE:
                break;
        }

        //Log.e("FragmentPolicyMakers", "The End");
    }

    //Function for arranging policy makers in desired order instead of random.
    void customSortingOfPolicyMakers(){

        //Find "Kaupunginvaltuusto" and "Kaupunginhallitus" and move to front
        List temp_list = new ArrayList<>();



        Map temp = null;
        for(Object obj : policy_makers){

            String str = ((Map) obj).get("name").toString();

            if(         str.equals("Kaupunginvaltuusto")
                    ||  str.equals("Kaupunginhallitus")){

                temp_list.add(obj);
                //policy_makers.remove(obj);
            }
        }

        //Push items back to front:
        for (Object tmp : temp_list){
            policy_makers.remove(tmp);
            policy_makers.add(0, tmp);
        }

    }

    void inflatePolicyMakerData() {

        if (policy_makers == null) {

            Log.e("FragmentAgenda", "Error: meetings data was empty!");
            return;
        }

        //Empty current list:
        ((LinearLayout) view_.findViewById(R.id.linearLayoutFragmentPolicyMakers)).removeAllViews();

        String dates = "";
        //Loop all meetings and construct needed UI components with data:
        for (Object policy_maker :
                policy_makers) {

            Map temp = (Map) policy_maker;

            //ListItem new_list_item = ListItem.newInstance();

            final View view = getActivity().getLayoutInflater().inflate(R.layout.layout_list_item, null, false);
            ((LinearLayout) view_.findViewById(R.id.linearLayoutFragmentPolicyMakers)).addView(view);

            //Set data:
            //=========
            ((TextView) view.findViewById(R.id.textViewHeader)).setText(temp.get("name").toString());
            final Integer policy_maker_id = Double.valueOf(temp.get("id").toString()).intValue();
            //Register listeners for link:
            //View link = view.findViewById(R.id.textViewMeetingLink);
            view.setTag(temp);

            //Set fav state based on configuration:
            ImageView iv = (ImageView) view.findViewById(R.id.imageViewFavIcon);
            if (ConfigurationManager.getIsFav("policy_maker=" + policy_maker_id)) {

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

                        ConfigurationManager.setIsFav("policy_maker=" + policy_maker_id, true);
                        iv.setImageResource(R.mipmap.fav_icon_selected);
                        state = 1;
                    }
                    else{
                        state = 0;
                        ConfigurationManager.setIsFav("policy_maker=" + policy_maker_id, false);
                        iv.setImageResource(R.mipmap.fav_icon_unselected);
                    }

                }
            });

            //view.findViewById(R.id.textViewHeader).setTag(temp);
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Object tag_data = v.getTag();

                    //Fire event to target fragment:
                    Integer policy_maker_id = Double.valueOf(((Map) v.getTag()).get("id").toString()).intValue();
                    ((MainActivity) getActivity()).exchange(1, policy_maker_id);

                    //Switch tab after clicking link
                    ((MainActivity) getActivity()).getmViewPager().setCurrentItem(1);
                }
            });

            //Query data in order to count how many meetings policy maker has:
            DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/meeting/?limit=1000&offset=0&policymaker=" + policy_maker_id, RequestType.POLICY_MAKER, 1000);

            //Hide date as unused
            view.findViewById(R.id.textViewDate).setVisibility(View.GONE);
        }
    }

    @Override
    public void BinaryDataAvailable(Object data, RequestType type) {

    }

    @Override
    public void onClick(View v) {

        Log.i("FragmentAgenda", "onClick");

        switch (v.getId()){

            case R.id.buttonUpdateFragmentPolicyMakers:

                //Display loading spinner and remove all children:
                getActivity().findViewById(R.id.progressBarContentLoadingFragmentPolicyMakers).setVisibility(View.VISIBLE);
                //Clear current content
                ((LinearLayout)getActivity().findViewById(R.id.linearLayoutFragmentPolicyMakers)).removeAllViews();
                DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/policymaker/?limit=200", RequestType.POLICY_MAKERS);
                break;

            case R.id.buttonBackToUpFragmentPolicyMakers:

                ((ScrollView) getActivity().findViewById(R.id.scrollViewFragmentPolicyMakers)).smoothScrollTo(0, 0);
                break;
        }
    }

    void filterPolicyMakersVisibility(String input){

        //Loop all policymakers and set visibilitystate according to user input:
        LinearLayout container = (LinearLayout) view_.findViewById(R.id.linearLayoutFragmentPolicyMakers);
        int count = container.getChildCount();

        View v = null;
        for(int i=0; i<count; i++) {

            v = container.getChildAt(i);
            if(!((TextView)v.findViewById(R.id.textViewHeader)).getText().toString().toLowerCase().contains(input.toLowerCase())){

                v.setVisibility(View.GONE);
            }else{
                v.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void exchange(int target, Object data) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        Log.i("FragmentPolicyMakers", "onTextChanged");
        filterPolicyMakersVisibility(s.toString());

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}

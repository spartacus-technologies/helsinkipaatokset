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

import com.google.gson.Gson;
import com.spartacus.ahjoexplorer.data_access.DataAccess;
import com.spartacus.ahjoexplorer.data_access.iFragmentDataExchange;

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
public class FragmentPolicyMakers extends Fragment implements View.OnClickListener, DataAccess.NetworkListener, iFragmentDataExchange {

    private View view_;
    private List policy_makers;

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


        //Request data:
        DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/policymaker/?limit=200");

        return view_;
    }


    @Override
    public void DataAvailable(String data) {

        Log.i("FragmentPolicyMakers", "DataAvailable");

        if(data != null){

            Gson gson = new Gson();

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

            //All good ->  inflate views
            inflatePolicyMakerData();

            //Hide loading animation:
            view_.findViewById(R.id.progressBarContentLoadingFragmentPolicyMakers).setVisibility(View.INVISIBLE);
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

            View view = getActivity().getLayoutInflater().inflate(R.layout.layout_single_meeting, null, false);
            ((LinearLayout) view_.findViewById(R.id.linearLayoutFragmentPolicyMakers)).addView(view);

            //Set data:
            //=========
            ((TextView) view.findViewById(R.id.textViewHeader)).setText(temp.get("name").toString());

            //Register listeners for link:
            //View link = view.findViewById(R.id.textViewMeetingLink);
            //link.setTag(temp); //TODO: add retrieved JSON as metadata
            ((TextView) view.findViewById(R.id.textViewHeader)).setTag(temp);
            ((TextView) view.findViewById(R.id.textViewHeader)).setOnClickListener(new View.OnClickListener() {

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

            //Hide date as unused
            view.findViewById(R.id.textViewDate).setVisibility(View.GONE);
        }
    }

    @Override
    public void BinaryDataAvailable(Object data) {

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
                DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/policymaker/?limit=200");
                break;

            case R.id.buttonBackToUpFragmentPolicyMakers:

                ((ScrollView) getActivity().findViewById(R.id.scrollViewFragmentPolicyMakers)).smoothScrollTo(0, 0);
                break;
        }
    }

    @Override
    public void exchange(int target, Object data) {

    }
}

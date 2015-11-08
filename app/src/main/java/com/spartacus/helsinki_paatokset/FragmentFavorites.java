package com.spartacus.helsinki_paatokset;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.spartacus.helsinki_paatokset.data_access.ConfigurationManager;
import com.spartacus.helsinki_paatokset.data_access.DataAccess;
import com.spartacus.helsinki_paatokset.data_access.iFragmentDataExchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentFavorites.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentFavorites#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFavorites extends Fragment implements DataAccess.NetworkListener, iFragmentDataExchange /*, View.OnScrollChangeListener*/ {

    private OnFragmentInteractionListener mListener;
    private View view_;

    //Data
    private List agenda_items;
    private List policy_makers;

    public static FragmentFavorites newInstance() {
        com.spartacus.helsinki_paatokset.FragmentFavorites fragment = new FragmentFavorites();


        return fragment;
    }

    public FragmentFavorites() {
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

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        view_ = view;

        //Query for data:
        DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/policymaker/?limit=200", RequestType.POLICY_MAKERS, 300);

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

                    //fillMeetingsData();
                    //Hide spinner to indicate loading is complete:
                    getActivity().findViewById(R.id.progressBarContentLoadingFragmentAgenda).setVisibility(View.INVISIBLE);

                }catch (Exception e){
                    Log.e("FragmentAgenda", "Exception:" + e.getMessage());
                    return;
                }
                //Call GUI data updater


                break;
            case MEETING:

                
                break;

            case POLICY_MAKERS:

                try {

                    m_data = new Gson().fromJson(data, Map.class);
                    policy_makers = (List) m_data.get("objects");
                }
                catch (Exception e){

                    Log.e("FragmentPolicyMakers", e.getMessage());
                    Toast.makeText(getActivity(), "Datahaun yhteydessä tapahtui virhe.", Toast.LENGTH_LONG).show();
                }

                sortAndFilterPolicyMakers();

                //All good ->  inflate views
                inflatePolicyMakerData();

                //Hide loading animation:
                //view_.findViewById(R.id.progressBarContentLoadingFragmentPolicyMakers).setVisibility(View.INVISIBLE);
                break;

            default:

                Log.e("FragmentAgenda", "Unknown response type.");
                break;
        }

        //Show received JSON:
        //Examples & source: https://github.com/google/gson/blob/master/examples/android-proguard-example/src/com/google/gson/examples/android/GsonProguardExampleActivity.java


    }

    private void inflatePolicyMakerData() {

        //Loop all meetings and construct needed UI components with data:
        for (Object policy_maker :
                policy_makers) {

            Map temp = (Map) policy_maker;

            //ListItem new_list_item = ListItem.newInstance();

            final View view = getActivity().getLayoutInflater().inflate(R.layout.layout_list_item, null, false);
            ((LinearLayout) view_.findViewById(R.id.linearLayoutFragmentFavoritesPolicyMakers)).addView(view);

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

                    boolean state = ConfigurationManager.getIsFav("policy_maker=" + policy_maker_id);

                    if (!state) {

                        ConfigurationManager.setIsFav("policy_maker=" + policy_maker_id, true);
                        iv.setImageResource(R.mipmap.fav_icon_selected);
                    }
                    else{
                        ConfigurationManager.setIsFav("policy_maker=" + policy_maker_id, false);
                        iv.setImageResource(R.mipmap.fav_icon_unselected);
                    }

                }
            });

            //view.findViewById(R.id.textViewHeader).setTag(temp);
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //TODO: target fragment is destroyed so it can't be called to front or passed data

                    //Fire event to target fragment:
                    Integer policy_maker_id = Double.valueOf(((Map) v.getTag()).get("id").toString()).intValue();
                    //((MainActivity) getActivity()).exchange(1, policy_maker_id);

                    //Switch tab after clicking link
                    //((MainActivity) getActivity()).getmViewPager().setCurrentItem(1);
                }
            });

            //Query data in order to count how many meetings policy maker has:
            //DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/meeting/?limit=1000&offset=0&policymaker=" + policy_maker_id, RequestType.POLICY_MAKER, 1000);

            //Hide date as unused
            view.findViewById(R.id.textViewDate).setVisibility(View.GONE);
        }


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

    //Function for arranging policy makers in desired order instead of random.
    void sortAndFilterPolicyMakers(){

        //Find "Kaupunginvaltuusto" and "Kaupunginhallitus" and move to front
        List temp_list = new ArrayList<>();

        Map temp = null;
        for(Object obj : policy_makers){

            Integer policy_maker_id = Double.valueOf(((Map)obj).get("id").toString()).intValue();

            if (ConfigurationManager.getIsFav("policy_maker=" + policy_maker_id)) {

               temp_list.add(obj);
            }
        }
        policy_makers = temp_list;
        /*
        //Push items back to front:
        for (Object tmp : temp_list){
            policy_makers.remove(tmp);
            policy_makers.add(0, tmp);
        }
        */
    }

    @Override
    public void exchange(int target, Object data) {

        Log.i("FragmentAgenda", "exchange");

        /*
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
        */
    }

    //TODO: not very elegant solution
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

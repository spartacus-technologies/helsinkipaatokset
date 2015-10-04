package com.ahjo_explorer.spartacus.ahjoexplorer;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ahjo_explorer.spartacus.ahjoexplorer.APIObjects.Meeting;
import com.google.gson.Gson;

import com.ahjo_explorer.spartacus.ahjoexplorer.data_access.DataAccess;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.Array;
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
public class FragmentMain extends Fragment implements View.OnClickListener, DataAccess.NetworkListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentMain() {
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


        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_main2, container, false);
        view.findViewById(R.id.buttonTestAPI).setOnClickListener(this);

        return view;
        //Register listeners
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

        DataAccess.testConnection(this);
    }

    @Override
    public void DataAvailable(String data) {

        if(data != null){

            Toast.makeText(getActivity(), "OMG It works! Received " + data.length()*8/1000 + " kilobytes.", Toast.LENGTH_LONG).show();
            ((TextView)getActivity().findViewById(R.id.textViewFragmentMainTest)).setText(data);
        }
        else{

            Toast.makeText(getActivity(), "No connection. :-/", Toast.LENGTH_LONG).show();
        }


        Map jsonJavaRootObject = null;



        //Show received JSON:
        //Examples & source: https://github.com/google/gson/blob/master/examples/android-proguard-example/src/com/google/gson/examples/android/GsonProguardExampleActivity.java
        Gson gson = new Gson();

        try {

            jsonJavaRootObject = new Gson().fromJson(data, Map.class);

            /*
            JsonElement jelement = new JsonParser().parse(data);
            JsonObject jobject = jelement.getAsJsonObject();
            jobject = jobject.getAsJsonObject("data");
            JsonArray jarray = jobject.getAsJsonArray("translations");
            jobject = jarray.get(0).getAsJsonObject();
            String result = jobject.get("translatedText").toString();
            */
            //Log.i("FragmentMain", meetings.toString());
        }
        catch (Exception e){

            Log.e("FragmentMain", e.getMessage());
        }

        List meetings = (List) jsonJavaRootObject.get("objects");

        for (Object meeting:
             meetings) {

            Log.i("FragmentMain", "Meeting:" + meeting.toString());
        }

        Log.v("FragmentMain", "insEmpty(): " + jsonJavaRootObject.isEmpty());

        Log.v("FragmentMain", "DataAvailable: " + data);
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

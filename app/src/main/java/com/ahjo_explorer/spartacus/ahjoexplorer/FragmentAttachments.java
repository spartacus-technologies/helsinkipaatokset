package com.ahjo_explorer.spartacus.ahjoexplorer;

import android.app.ActionBar;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahjo_explorer.spartacus.ahjoexplorer.R;
import com.ahjo_explorer.spartacus.ahjoexplorer.data_access.DataAccess;
import com.ahjo_explorer.spartacus.ahjoexplorer.data_access.iFragmentDataExchange;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentAttachments.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentAttachments#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAttachments extends Fragment implements iFragmentDataExchange, DataAccess.NetworkListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private List attachments;
    private View view_;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentAttachments.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAttachments newInstance() {

        FragmentAttachments fragment = new FragmentAttachments();
        return fragment;
    }

    public FragmentAttachments() {
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
        view_ =  inflater.inflate(R.layout.fragment_attachments, container, false);
        if(attachments != null){

            inflateAttachmentsData();
        }

       return view_;
    }

    @Override
    public void exchange(int target, Object data) {

        //Query for data:
        DataAccess.requestData(this, "http://dev.hel.fi/paatokset/v1/agenda_item/" + data.toString());
    }

    @Override
    public void DataAvailable(String data) {

        if(data == null){

            return;
        }

        Map m_data = new Gson().fromJson(data, Map.class);

        //Attachments data:
        attachments = (List)m_data.get("attachments");

        inflateAttachmentsData();
    }

    void inflateAttachmentsData(){

        LinearLayout container = (LinearLayout) view_.findViewById(R.id.FragmentAttachmentsLinearLayoutContent);

        //Clear current content:
        container.removeAllViews();


        if(attachments.size() == 0){

            TextView tv_att = new TextView(getActivity());
            tv_att.setText("(ei liitteitä)");
            container.addView(tv_att);
            return;
        }

        for (Object attachment : attachments) {

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

                String html_data = "<a href=" + ((Map)attachment).get("file_uri").toString() + ">" + ((Map)attachment).get("name").toString();
                tv_att.setText(Html.fromHtml(html_data));
                tv_att.setMovementMethod(LinkMovementMethod.getInstance());

                tv_type.setText(" (" + ((Map)attachment).get("file_type").toString() + ")");

                //tv_att.setText(((Map) attachment).get("name").toString());
            }
            inner_container.addView(tv_att);
            inner_container.addView(tv_type);
            container.addView(inner_container);
        }
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

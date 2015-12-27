package com.spartacus.helsinki_paatokset;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.spartacus.helsinki_paatokset.data_access.DataAccess;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentSearch.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSearch extends Fragment implements DataAccess.NetworkListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view_;
    private WebView webView_;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSearch newInstance() {
        FragmentSearch fragment = new FragmentSearch();

        return fragment;
    }

    public FragmentSearch() {
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
        view_ = inflater.inflate(R.layout.fragment_fragment_search, container, false);

        webView_ = (WebView)view_.findViewById(R.id.webViewSearch);

        WebSettings settings = webView_.getSettings();
        settings.setJavaScriptEnabled(true);
        webView_.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        webView_.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("WebView", "Processing webview...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i("WebView", "Finished loading URL: " + url);
            }
        });

        webView_.loadUrl("https://decisions.dcentproject.eu/");

        //Register listeners
        view_.findViewById(R.id.buttonWebViewBack).setOnClickListener(this);
        view_.findViewById(R.id.buttonWebViewForward).setOnClickListener(this);

        return view_;
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

    }

    @Override
    public void BinaryDataAvailable(Object data, RequestType type) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.buttonWebViewBack:

                webView_.goBack();

                break;
            case R.id.buttonWebViewForward:

                webView_.goForward();

                break;
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

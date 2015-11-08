package com.spartacus.helsinki_paatokset.data_access;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.spartacus.helsinki_paatokset.R;

/**
 * Created by Eetu on 8.11.2015.
 */
public class ListItem extends Fragment{

    View view_;

    public static ListItem newInstance() {

        ListItem fragment = new ListItem();
        return fragment;
    }

    public ListItem() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view_ = inflater.inflate(R.layout.layout_list_item, null, false);

        return view_;
    }
}

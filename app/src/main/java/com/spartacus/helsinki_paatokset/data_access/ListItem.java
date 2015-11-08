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
public class ListItem extends View{

    View view_;

    public ListItem(Context context) {
        super(context);
    }

    public static ListItem newInstance(Context context) {

        ListItem item = new ListItem(context);

        //this.inf

        return item;
    }
    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view_ = inflater.inflate(R.layout.layout_list_item, null, false);

        return view_;
    }
    */
}

package com.spartacus.helsinki_paatokset;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Eetu on 28.10.2015.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    static Map<Integer, Fragment> fragment_container;   //TODO: this is a bit ghetto solution but works for now.

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        if(fragment_container == null)
            fragment_container = new HashMap<>();
    }

    Fragment getFragmentByPosition(int pos){

        return fragment_container.get(pos);
    }

    @Override
    public Fragment getItem(int position) {

        if(fragment_container != null)
            return  getFragmentByPosition(position);

        Fragment frag;

        switch (position){

            case 0:

                //frag = FragmentAgenda.newInstance(null, null);
                frag = FragmentPolicyMakers.newInstance();
                break;
            case 1:
                frag =  FragmentMeetings.newInstance();
                break;
            case 2:
                //TODO
                frag =  FragmentAgenda.newInstance();
                break;
            default:
                frag =  null;
        }

        fragment_container.put(position, frag);
        return frag;

        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return FragmentAgenda.newInstance(null, null);
    }

    public CharSequence getPageTitle(int position, Context context) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return context.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return context.getString(R.string.title_section2).toUpperCase(l);
            case 2:
                return context.getString(R.string.title_section3).toUpperCase(l);
        }
        return null;
    }

    @Override
    public int getCount() {
        return fragment_container.size();
    }
}

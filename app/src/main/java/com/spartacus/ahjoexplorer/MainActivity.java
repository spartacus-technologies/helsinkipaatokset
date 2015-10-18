package com.spartacus.ahjoexplorer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.spartacus.ahjoexplorer.data_access.iFragmentDataExchange;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener, FragmentAgenda.OnFragmentInteractionListener, iFragmentDataExchange {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("MainActivity", "Starting application...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        //Add spinner to main layout
        //TODO: doesn't really show anything...
        /*
        FrameLayout.LayoutParams full_window = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        FrameLayout frame = new FrameLayout(this);
        frame.setLayoutParams(full_window);

        TextView prog = new TextView(this);
        prog.setText("jeejee");
        prog.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                ((ViewPager) findViewById(R.id.pager)).addView(frame);
        frame.addView(prog);
        */

        //Register listeners:
        //findViewById(R.id.buttonTestAPIConnection).setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(mViewPager.getCurrentItem() == 0){

                return super.onKeyDown(keyCode, event);

            }else{

                mViewPager.setCurrentItem(0, true);
            }
            return true;

        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_h_kanava_videot) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public ViewPager getmViewPager() {
        return mViewPager;
    }

    @Override
    public void exchange(int target, Object data) {

        Fragment frag = mSectionsPagerAdapter.getFragmentByPosition(target);

        if(frag == null){

            Log.e("MainActivity", "Fragment with id " + target + " not found");
        }
        else{

            Log.i("MainActivity", "ID=" + frag.getId());

            //Pass actual data:
            ((iFragmentDataExchange)frag).exchange(target, data);
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        Map<Integer, Fragment> fragment_container;   //TODO: this is a bit ghetto solution but works for now.

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragment_container = new HashMap<>();
        }

        Fragment getFragmentByPosition(int pos){

            return fragment_container.get(pos);
        }

        @Override
        public Fragment getItem(int position) {

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

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

}

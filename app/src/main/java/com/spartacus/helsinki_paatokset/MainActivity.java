package com.spartacus.helsinki_paatokset;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.spartacus.helsinki_paatokset.data_access.iFragmentDataExchange;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements FragmentSearch.OnFragmentInteractionListener, FragmentFavorites.OnFragmentInteractionListener,FragmentAgenda.OnFragmentInteractionListener, iFragmentDataExchange, View.OnClickListener {

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
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("MainActivity", "Starting application...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        try{

            setSupportActionBar(toolbar);

        }catch(Exception e){

            Log.e("MainActivity", e.getMessage());
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.setContext(this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onResume(){
        super.onResume();

    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(mViewPager.getCurrentItem() == 0){

                return super.onKeyDown(keyCode, event);

            }else{

                int index = mViewPager.getCurrentItem();
                index -= 1;
                index = index < 0 ? 0 : index;
                mViewPager.setCurrentItem(index, true);
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

        /*
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_shares);

        mShareActionProvider = new ShareActionProvider(this);

        // Fetch and store ShareActionProvider
        MenuItemCompat.setActionProvider(item, mShareActionProvider);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        item.setIcon(R.mipmap.share_white);
        */

        //Dummy send intent:
        //TODO: add real data here...
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Katselen avointa päätösdataa @HKIPaatokset avulla! Katso sinäkin: http://app.sprtc.us #HelsinginKaupunki #HelsinkiPäätökset");
        sendIntent.setType("text/plain");
        //startActivity(sendIntent);
        setShareIntent(sendIntent);
        Log.i("ActivityMain", "Version " + BuildConfig.VERSION_NAME);


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

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        /*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_h_kanava_videot) {
            return true;
        }
        */

        if(id == R.id.menu_item_shares){

            //String tweetUrl = "https://twitter.com/intent/tweet?text=Katselen avointa päätösdata @HKIPaatokset avulla! Katso sinäkin: www.spartacus-technologies.fi&hashtags=HelsinginKaupunki, HelsinkiPäätökset";
            String text = "Katselen avointa päätösdataa @HKIPaatokset avulla! Katso sinäkin: http://app.sprtc.us #HelsinginKaupunki #HelsinkiPäätökset";

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

            return true;
        }

        if (id == R.id.action_twitter) {

            //String tweetUrl = "https://twitter.com/intent/tweet?text=Katselen avointa päätösdata @HKIPaatokset avulla! Katso sinäkin: www.spartacus-technologies.fi&hashtags=HelsinginKaupunki, HelsinkiPäätökset";
            String tweetUrl = "https://twitter.com/intent/tweet?text=Katselen avointa päätösdataa @HKIPaatokset avulla! Katso sinäkin:&url=http://app.sprtc.us&hashtags=HelsinginKaupunki, HelsinkiPäätökset";
                    //+ "&hashtags=#HelsinginKaupunki, HelsinkiPäätökset";
            Uri uri = Uri.parse(tweetUrl);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onClick(View v) {


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        Fragment fragment_container[];      //TODO: this is a bit lazy solution but works for now.
        Context context;                                //TODO: also quite lazy. Find alternative if time.

        public void setContext(Context con) {

            context = con;
        }

        Fragment getFragmentByPosition(int pos) {

            return fragment_container[pos];
        }

        public SectionsPagerAdapter(FragmentManager fm) {

            super(fm);
            fragment_container = new Fragment[COUNT];
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag;

            switch (position) {

                case 0:

                    //frag = FragmentAgenda.newInstance(null, null);
                    frag = FragmentPolicyMakers.newInstance();
                    break;
                case 1:
                    frag = FragmentMeetings.newInstance();
                    break;
                case 2:
                    //TODO
                    frag = FragmentAgenda.newInstance();
                    break;
                case 3:

                    frag = FragmentSearch.newInstance();
                    break;
                case 4:
                    frag = FragmentFavorites.newInstance();
                    break;

                default:
                    frag = null;
            }

            fragment_container[position] = frag;
            return frag;
        }

        final int COUNT = 5;

        @Override
        public int getCount() {

            // Show 4 total pages.
            return COUNT;
        }


        private int[] imageResId = {
                R.mipmap.arrow_up,
                R.mipmap.arrow_up_inv,
                R.mipmap.close_image,
                R.mipmap.close_image
        };

        public CharSequence getPageTitle(int position) {

            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return context.getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return context.getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return context.getString(R.string.title_section3).toUpperCase(l);
                case 3:

                    return context.getString(R.string.title_section4).toUpperCase(l);
                case 4:
                    return context.getString(R.string.title_section5).toUpperCase(l);
                    /*
                    Drawable image = ContextCompat.getDrawable(context, R.mipmap.fav_icon_tabs);
                    image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
                    SpannableString sb = new SpannableString(" ");
                    ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return sb;
                   */
                default:
                    return null;
            }
        }
    }
}

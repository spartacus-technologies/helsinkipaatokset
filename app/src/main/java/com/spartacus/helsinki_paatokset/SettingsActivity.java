package com.spartacus.helsinki_paatokset;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.spartacus.helsinki_paatokset.data_access.ConfigurationManager;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Set some default values
        ((TextView)findViewById(R.id.textViewVersionInfoAcitivitySettings)).setText(BuildConfig.VERSION_NAME + " (build #" + BuildConfig.VERSION_CODE + ")");

        //Radio button group selection:
        ConfigurationManager.initialize(this);
        String video_source =  ConfigurationManager.getVideoSource();

        if(video_source.equals("MP4")){

            ((RadioButton)findViewById(R.id.radioButtonMP4)).setChecked(true);
        }
        else if(video_source.equals("OGV")){

            ((RadioButton)findViewById(R.id.radioButtonOGV)).setChecked(true);
        }
        else if(video_source.equals("HKI")){

            ((RadioButton)findViewById(R.id.radioButtonHKIKanava)).setChecked(true);
        }


        //Listeners
        findViewById(R.id.buttonCloseSettingsActivity).setOnClickListener(this);

        ((RadioButton)findViewById(R.id.radioButtonMP4)).setOnClickListener(this);
        ((RadioButton)findViewById(R.id.radioButtonHKIKanava)).setOnClickListener(this);
        ((RadioButton)findViewById(R.id.radioButtonOGV)).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_settings, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.radioButtonMP4:

                ConfigurationManager.setVideoSource("MP4");
                break;

            case R.id.radioButtonHKIKanava:

                ConfigurationManager.setVideoSource("HKI");
                break;

            case R.id.radioButtonOGV:

                ConfigurationManager.setVideoSource("OGV");
                break;

            case R.id.buttonCloseSettingsActivity:

                super.finish();
                break;
        }
    }
}

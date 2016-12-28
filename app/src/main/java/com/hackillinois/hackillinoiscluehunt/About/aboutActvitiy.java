package com.hackillinois.hackillinoiscluehunt.About;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hackillinois.hackillinoiscluehunt.R;

/**
 * Created by tommypacker for HackIllinois' 2016 Clue Hunt
 */
public class aboutActvitiy extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);

        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new aboutFragment())
                .commit();

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.aboutToolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }
}

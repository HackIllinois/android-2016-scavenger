package com.hackillinois.hackillinoiscluehunt.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.hackillinois.hackillinoiscluehunt.R;
import com.hackillinois.hackillinoiscluehunt.clueHuntActivity;

/**
 * Created by tommypacker for HackIllinois' 2016 Clue Hunt
 */
public class splash_screen extends AppCompatActivity {

    private boolean loggedIn;
    private TextView banner;
    private Intent intent;
    private Typeface bigFont;
    private Typeface smallFont;
    private SharedPreferences prefs;

    private final Handler mHandler = new Handler();
    private final Runnable mUpdateBanner = new Runnable() {
        boolean flag = false;
        public void run() {
            if(!flag){
                banner.setText("Tap the Compass to Begin");
                banner.setTextSize(30);
                banner.setTypeface(smallFont);
            }else{
                banner.setText("HACK\nILLINOIS");
                banner.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                banner.setTypeface(bigFont);
            }
            flag = !flag;
            mHandler.postDelayed(this, 2500);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.splash_page_layout);

        //ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription("HackIllinois", , getColor(R.color.primary_dark));
        //((Activity)this).setTaskDescription(taskDescription);

        bigFont = Typeface.createFromAsset(getAssets(), "fonts/Gotham-Black.otf");
        smallFont = Typeface.createFromAsset(getAssets(), "fonts/Brandon_med.otf");

        banner = (TextView) findViewById(R.id.banner);
        banner.setTypeface(bigFont);

        prefs = getSharedPreferences(clueHuntActivity.PREFS_NAME, MODE_PRIVATE);
        loggedIn = prefs.getBoolean("loggedIn", false);

        Button splashButton = (Button) findViewById(R.id.splashButton);
        splashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIntent();
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume(){
        checkIntent();
        mHandler.postDelayed(mUpdateBanner, 2500);
        super.onResume();
    }

    @Override
    public void onPause(){
        mHandler.removeCallbacks(mUpdateBanner);
        super.onPause();
    }

    private void checkIntent(){
        loggedIn = prefs.getBoolean("loggedIn", false);
        if (!loggedIn) {
            intent = new Intent(getApplicationContext(), login_activity.class);
        } else {
            intent = new Intent(getApplicationContext(), clueHuntActivity.class);
        }
    }
}

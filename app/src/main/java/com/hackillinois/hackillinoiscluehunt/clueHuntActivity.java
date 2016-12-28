package com.hackillinois.hackillinoiscluehunt;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hackillinois.hackillinoiscluehunt.About.aboutActvitiy;
import com.hackillinois.hackillinoiscluehunt.Clue.clueListFragment;
import com.hackillinois.hackillinoiscluehunt.CustomTextViews.TextViewFontGotham;
import com.hackillinois.hackillinoiscluehunt.Scoring.scoreFragment;


import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class clueHuntActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 12;

    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.navView) NavigationView navDrawer;

    public static final String PREFS_NAME = "PositionTracker";
    public static String userName;
    public static String picUrl;

    private FragmentManager fragmentManager;
    private ActionBarDrawerToggle drawerToggle;
    private SharedPreferences.Editor editor;
    public static Firebase ref;
    private int checkPosition;
    private Dialog dialog;
    private MenuItem item;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestPerms();
        setContentView(R.layout.clue_hunt_activity);
        Firebase.setAndroidContext(this);
        ButterKnife.bind(this);
        setMaxNumberOfClues();

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor =sharedPreferences.edit();
        userName = sharedPreferences.getString("userName", "");
        picUrl = sharedPreferences.getString("picUrl", "");
        ref = new Firebase("https://hi-scavenger2016.firebaseio.com/").child("users").child(userName).child("profile");

        //Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        //Setup the nav drawer
        setupDrawerContent(navDrawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
        View headerView = navDrawer.inflateHeaderView(R.layout.drawer_header);
        TextViewFontGotham navHeaderName = (TextViewFontGotham) headerView.findViewById(R.id.nameDisplay);
        CircleImageView navHeaderImage = (CircleImageView) headerView.findViewById(R.id.profile_image);
        navHeaderName.setText(userName);
        Glide.with(getApplicationContext()).load(picUrl).into(navHeaderImage);

        checkPosition = sharedPreferences.getInt("clueNumber", 0);

        fragmentManager = getSupportFragmentManager();
        if(checkPosition < 15){
            swapFragment(new clueListFragment());
        }else{
            swapFragment(new Finished_Fragment());
        }
    }

    private void setupDrawerContent(NavigationView navView){
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                item = menuItem;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        selectDrawerItem(item); // your fragment transactions go here
                    }
                }, 200);
                return false;
            }
        });
    }

    private void selectDrawerItem(MenuItem item){
        final int selectedId = item.getItemId();
        switch (selectedId){
            case R.id.clueList:
                if(checkPosition < 15){
                    swapFragment(new clueListFragment());
                }else{
                    swapFragment(new Finished_Fragment());
                }
                item.setChecked(true);
                setTitle("HackIllinois Clue Hunt");
                break;
            case R.id.leaderBoard:
                swapFragment(new scoreFragment());
                item.setChecked(true);
                setTitle("HackIllinois Clue Hunt");
                break;
            case R.id.about:
                Intent intent = new Intent(this, aboutActvitiy.class);
                startActivity(intent);
                setTitle(item.getTitle());
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_info){
            showInfoDialog();
            return true;
        }else{
            return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }
    }

    private void swapFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in,
                0,
                0,
                android.R.anim.fade_out);
        transaction.replace(R.id.content_holder, fragment).commit();
    }

    private void showInfoDialog(){
        dialog = new Dialog(clueHuntActivity.this);
        dialog.setContentView(R.layout.info_dialog_layout);
        dialog.setTitle("How To Play");
        Button okButton = (Button) dialog.findViewById(R.id.infoButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_view, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    //Override so we can close drawer on back button press
    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume(){
        super.onResume();
        setTitle("HackIllinois Clue Hunt");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void requestPerms(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    this.finish();
                    System.exit(0);
                }
                return;
            }
        }
    }

    public void setMaxNumberOfClues(){
        Firebase appArgRef = new Firebase("https://hi-scavenger2016.firebaseio.com/appargs/0/total_clues");
        appArgRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long maxClues = (long) dataSnapshot.getValue();
                int max = (int) maxClues;
                editor.putInt("maxClues", max).commit();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}

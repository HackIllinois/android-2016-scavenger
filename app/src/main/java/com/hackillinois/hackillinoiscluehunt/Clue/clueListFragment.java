package com.hackillinois.hackillinoiscluehunt.Clue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hackillinois.hackillinoiscluehunt.R;
import com.hackillinois.hackillinoiscluehunt.CustomTextViews.TextViewFontGotham;
import com.hackillinois.hackillinoiscluehunt.clueHuntActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tommypacker on 10/14/15.
 */
public class clueListFragment extends Fragment {

    private static ArrayList<clue> clues;
    private FragmentManager fragmentManager;
    private int cluePosition;
    private long score;
    private cluesAdapter adapter;
    private Firebase ref;
    @Bind(R.id.listOfClues) ListView clueList;
    @Bind(R.id.scoreDisplayClueList) TextViewFontGotham scoreDisplay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.clue_list, container, false);
        ButterKnife.bind(this, view);

        ref = new Firebase("https://hi-scavenger2016.firebaseio.com/users").child(clueHuntActivity.userName).child("profile").child("score");

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(clueHuntActivity.PREFS_NAME, Context.MODE_PRIVATE);
        cluePosition = sharedPreferences.getInt("clueNumber", 0);

        fragmentManager = getActivity().getSupportFragmentManager();
        clues = new ArrayList<clue>();
        clueList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        if(savedInstanceState == null){
            populateArrayList();
        }

        adapter = new cluesAdapter(getActivity(), clues);
        clueList.setAdapter(adapter);

        clueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (cluePosition == position){
                    swapFragment(new clueFragment());
                }
            }
        });

        updateScoreDisplay();
        return view;
    }

    private void swapFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in,
                0,
                0,
                android.R.anim.fade_out);
        transaction.replace(R.id.content_holder, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void populateArrayList(){
        Firebase ref = new Firebase("https://hi-scavenger2016.firebaseio.com/appdata");
        ref.addChildEventListener(new ChildEventListener() {
            int count = 0;

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                clue appClue = dataSnapshot.getValue(clue.class);
                if (count == cluePosition) {
                    appClue.setFlag(true);
                }
                count++;
                clues.add(appClue);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void updateScoreDisplay(){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (scoreDisplay != null) {
                    score = (long) dataSnapshot.getValue() * -1;
                    scoreDisplay.setText("Score:  " + (score));
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        updateScoreDisplay();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public static String getClueID(int position){
        clue currentClue = clues.get(position);
        return currentClue.getQr_id();
    }

    public static int getClueValue(int position){
        clue currentClue = clues.get(position);
        return currentClue.getInitial_pts();
    }

    public static long getClueTime(int position){
        clue currentClue = clues.get(position);
        return Long.parseLong(currentClue.getTime_released());
    }

}

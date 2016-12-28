package com.hackillinois.hackillinoiscluehunt.Scoring;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.hackillinois.hackillinoiscluehunt.R;
import com.hackillinois.hackillinoiscluehunt.CustomTextViews.TextViewFontGotham;
import com.hackillinois.hackillinoiscluehunt.clueHuntActivity;

import java.util.ArrayList;

/**
 * Created by tommypacker for HackIllinois' 2016 Clue Hunt
 */
public class scoreFragment extends Fragment{

    private ArrayList<User> participants;
    private scoreAdapter adapter;
    private TextViewFontGotham scoreDisplay;
    private long score;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.score_layout, container, false);
        Firebase.setAndroidContext(this.getContext());

        scoreDisplay = (TextViewFontGotham) view.findViewById(R.id.displayScore);
        ListView leaderboard = (ListView) view.findViewById(R.id.leaderBoard);

        updateScoreDisplay();

        participants = new ArrayList<>();
        adapter = new scoreAdapter(getContext(), participants);
        populateParticipants();
        leaderboard.setAdapter(adapter);

        return view;
    }

    private void updateScoreDisplay(){
        Firebase ref = new Firebase("https://hi-scavenger2016.firebaseio.com/users").child(clueHuntActivity.userName).child("profile").child("score");
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

    //Save the current time so it can be restored
    @Override
    public void onDetach(){
        super.onDetach();
    }

    private void populateParticipants(){
        Firebase userRef = new Firebase("https://hi-scavenger2016.firebaseio.com/users");
        Query queryRef = userRef.orderByChild("profile/score");

        queryRef.limitToFirst(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleUser : dataSnapshot.getChildren()) {
                    User participant = singleUser.getValue(User.class);
                    participant.getProfile().setScore(participant.getProfile().getScore() * -1);
                    String userName = participant.getProfile().getUsername();
                    if(userName.equals(clueHuntActivity.userName)){
                        participant.setIsUser(true);
                    }
                    participants.add(participant);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

}

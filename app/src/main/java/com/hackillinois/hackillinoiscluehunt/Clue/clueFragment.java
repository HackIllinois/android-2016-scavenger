package com.hackillinois.hackillinoiscluehunt.Clue;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hackillinois.hackillinoiscluehunt.R;
import com.hackillinois.hackillinoiscluehunt.CustomTextViews.TextViewFontBrandon;
import com.hackillinois.hackillinoiscluehunt.CustomTextViews.TextViewFontGotham;
import com.hackillinois.hackillinoiscluehunt.clueHuntActivity;
import com.hackillinois.hackillinoiscluehunt.Scoring.scannerFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tommypacker for HackIllinois' 2016 Clue Hunt
 */
public class clueFragment extends Fragment{

    @Bind(R.id.dynamicImage) ImageView cluePicture;
    @Bind(R.id.clueHint) TextViewFontBrandon clueHint;
    @Bind(R.id.clueTitle) TextViewFontGotham title;
    @Bind(R.id.scanButton1) Button scanButton;
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.clue, container, false);
        Firebase.setAndroidContext(this.getContext());
        ButterKnife.bind(this, view);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(clueHuntActivity.PREFS_NAME, Context.MODE_PRIVATE);
        int position = sharedPreferences.getInt("clueNumber", 0);

        fragmentManager = getActivity().getSupportFragmentManager();

        setUpClueDisplay(position);
        title.setText("Clue: " + (position + 1));

        //Style Button
        scanButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        scanButton.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Brandon_med.otf"));

        return view;
    }

    @OnClick(R.id.scanButton1)
    public void onClick(View v) {
        swapFragment(new scannerFragment());
    }

    private void swapFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(0, 0, 0, android.R.anim.fade_out);
        transaction.replace(R.id.content_holder, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setUpClueDisplay(int position){
        Firebase ref = new Firebase("https://hi-scavenger2016.firebaseio.com/appdata").child(Integer.toString(position));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clueHint.setText((String) dataSnapshot.child("hint").getValue());
                String imgURL = (String) dataSnapshot.child("img_url").getValue();
                Glide.with(getContext()).load(imgURL).crossFade().into(cluePicture);
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

}

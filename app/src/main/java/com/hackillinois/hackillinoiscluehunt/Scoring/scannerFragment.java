package com.hackillinois.hackillinoiscluehunt.Scoring;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.zxing.Result;
import com.hackillinois.hackillinoiscluehunt.Clue.clueListFragment;
import com.hackillinois.hackillinoiscluehunt.Finished_Fragment;
import com.hackillinois.hackillinoiscluehunt.R;
import com.hackillinois.hackillinoiscluehunt.clueHuntActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.livotov.labs.android.camview.ScannerLiveView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by tommypacker on 10/10/15.
 */
public class scannerFragment extends Fragment implements ZXingScannerView.ResultHandler {

    @Bind(R.id.scanner) ZXingScannerView camera;

    private SharedPreferences.Editor editor;
    private FragmentManager fragmentManager;
    private int position, score, maxClues;
    private String checkClue;
    private Firebase ref;
    private long timeClueWasPushed;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.scanner, container, false);
        Firebase.setAndroidContext(this.getContext());
        ButterKnife.bind(this, view);
        fragmentManager = getActivity().getSupportFragmentManager();
        ref = new Firebase("https://hi-scavenger2016.firebaseio.com/users").child(clueHuntActivity.userName);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(clueHuntActivity.PREFS_NAME, clueHuntActivity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        position = sharedPreferences.getInt("clueNumber", 0);
        checkClue = clueListFragment.getClueID(position);
        maxClues = sharedPreferences.getInt("maxClues", 0);

        score = sharedPreferences.getInt("globalScore", 0);

        //Setup qr code scanner
        /*camera.setHudVisible(false);
        camera.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override
            public void onScannerStarted(ScannerLiveView scanner) {
                Toast.makeText(getContext(), "Hold camera steady", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScannerStopped(ScannerLiveView scanner) {
            }

            @Override
            public void onScannerError(Throwable err) {
            }

            @Override
            public void onCodeScanned(String data) {
                if(scannedRightQR(data)){
                    scanSucceeded();
                }else{
                    Toast.makeText(getActivity(), "Wrong QR Code Scanned", Toast.LENGTH_LONG).show();
                }
            }
        });*/
        camera.setResultHandler(this);

        return view;
    }

    private void scanSucceeded(){
        editor.putInt("clueNumber", position+1).apply(); //Increments sharedprefs clue position
        if (position+1 < maxClues) {
            clueHuntActivity.ref.child("position").setValue(position);
            ref.child("sv_time").setValue(ServerValue.TIMESTAMP, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    setFirebaseScore();
                    swapFragment();
                }
            });
        }else{
            clueHuntActivity.ref.child("finished_hunt").setValue(true, new Firebase.CompletionListener(){
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    setFirebaseScore();
                }
            });
            fragmentManager.beginTransaction()
                    .replace(R.id.content_holder, new Finished_Fragment()).commit();
            Toast.makeText(getContext(), "Congrats, you finished the clue hunt", Toast.LENGTH_LONG).show();
        }
    }

    private void swapFragment() {
        fragmentManager.popBackStack();
    }

    @Override
    public void onResume(){
        super.onResume();
        camera.startCamera();
    }

    @Override
    public void onPause(){
        camera.stopCamera();
        super.onPause();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private boolean scannedRightQR(String codeScanned){
        return codeScanned.equals(checkClue);
    }

    private void setFirebaseScore(){
        ref.child("sv_time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                timeClueWasPushed = (long) dataSnapshot.getValue();
                int clueValue = clueListFragment.getClueValue(position);
                long clueReleaseTime = clueListFragment.getClueTime(position);
                score += (clueValue - (int)((timeClueWasPushed-clueReleaseTime)/6000));
                score = (score < 0) ? 0 : score;

                clueHuntActivity.ref.child("score").setValue(score * -1);
                editor.putInt("globalScore", score).commit();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void handleResult(Result result) {
        String data = result.getText();

        if(scannedRightQR(data)){
            camera.stopCamera();
            scanSucceeded();
        }else{
            Toast.makeText(getActivity(), "Wrong QR Code Scanned", Toast.LENGTH_SHORT).show();
            camera.stopCamera();
            getFragmentManager().popBackStack();
        }
    }
}

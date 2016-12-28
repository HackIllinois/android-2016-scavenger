package com.hackillinois.hackillinoiscluehunt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tommypacker for HackIllinois' 2016 Clue Hunt
 */
public class Finished_Fragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        return inflater.inflate(R.layout.finished_layout, parent, false);
    }
}

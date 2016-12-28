package com.hackillinois.hackillinoiscluehunt;

import com.hackillinois.hackillinoiscluehunt.Clue.clue;

import java.util.ArrayList;

/**
 * Created by tommypacker for HackIllinois' 2016 Clue Hunt
 */

public class Utils {
    public static String getClueID(ArrayList<clue> clues, int position){
        clue currentClue = clues.get(position);
        return currentClue.getQr_id();
    }

    public static int getClueValue(ArrayList<clue> clues, int position){
        clue currentClue = clues.get(position);
        return currentClue.getInitial_pts();
    }

    public static long getClueTime(ArrayList<clue> clues, int position){
        clue currentClue = clues.get(position);
        return Long.parseLong(currentClue.getTime_released());
    }

}

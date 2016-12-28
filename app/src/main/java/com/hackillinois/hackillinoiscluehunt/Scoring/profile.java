package com.hackillinois.hackillinoiscluehunt.Scoring;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by tommypacker for HackIllinois' 2016 Clue Hunt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class profile {

    private String avatarUrl;
    private String username;
    private int score;
    private int position;
    private boolean finished_hunt;

    public profile(){}

    public String getAvatarUrl(){return avatarUrl;}

    public String getUsername(){return username;}

    public int getScore(){return score;}

    public int getPosition(){return position;}

    public void setScore(int score){this.score = score;}

    public boolean getFinished_hunt(){return finished_hunt;}
}

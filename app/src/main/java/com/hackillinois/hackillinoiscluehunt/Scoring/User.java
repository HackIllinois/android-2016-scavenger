package com.hackillinois.hackillinoiscluehunt.Scoring;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by tommypacker for HackIllinois' 2016 Clue Hunt
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private long sv_time;
    private profile profile;
    private boolean isUser;

    public User(){}

    public profile getProfile(){return profile;}

    public long getSv_time(){return sv_time;}

    public boolean getIsUser(){
        return isUser;
    }

    public void setIsUser(Boolean flag){
        isUser = flag;
    }
}

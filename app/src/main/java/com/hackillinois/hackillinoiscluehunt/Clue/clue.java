package com.hackillinois.hackillinoiscluehunt.Clue;

/**
 * Created by tommypacker on 10/15/15.
 */
public class clue {

    private String api_ver;
    private String clue_name;
    private String desc;
    private String hint;
    private int img_crop;
    private String img_url;
    private int initial_pts;
    private int loss_per_min;
    private String qr_id;
    private int release;
    private String time_released;
    private boolean flag;

    public clue(){}

    public String getApi_ver(){
        return api_ver;
    }

    public String getClue_name(){return clue_name;}

    public String getDesc(){return desc;}

    public String getHint(){return hint;}

    public int getImg_crop(){return img_crop;}

    public String getImg_url(){return img_url;}

    public int getInitial_pts(){return initial_pts;}

    public int getLoss_per_min(){return loss_per_min;}

    public String getQr_id(){return qr_id;}

    public int getRelease(){return release;}

    public boolean getFlag(){return flag;}

    public String getTime_released(){return time_released;}

    public void setFlag(boolean newFlag){this.flag = newFlag;}

}

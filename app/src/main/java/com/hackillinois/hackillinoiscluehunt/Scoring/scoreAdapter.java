package com.hackillinois.hackillinoiscluehunt.Scoring;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hackillinois.hackillinoiscluehunt.R;
import com.hackillinois.hackillinoiscluehunt.CustomTextViews.TextViewFontBrandon;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tommypacker for HackIllinois' 2016 Clue Hunt
 */
public class scoreAdapter extends ArrayAdapter<User> {

    public scoreAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        User participant = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.leaderboard_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.userScore.setText(Integer.toString(participant.getProfile().getScore()));
        viewHolder.userName.setText(participant.getProfile().getUsername());

        if(!participant.getIsUser()){
            viewHolder.userName.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
            viewHolder.userScore.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.icons));
        }else{
            viewHolder.userName.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
            viewHolder.userScore.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.clueHighlight));
        }

        Glide.with(getContext()).load(participant.getProfile().getAvatarUrl()).override(175, 175).centerCrop().into(viewHolder.profPic);

        return convertView;
    }

    static class ViewHolder{
        @Bind(R.id.userProfPic) ImageView profPic;
        @Bind(R.id.userName) TextViewFontBrandon userName;
        @Bind(R.id.userScore) TextViewFontBrandon userScore;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}

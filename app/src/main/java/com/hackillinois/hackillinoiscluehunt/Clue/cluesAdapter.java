package com.hackillinois.hackillinoiscluehunt.Clue;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hackillinois.hackillinoiscluehunt.CustomTextViews.TextViewFontBrandon;
import com.hackillinois.hackillinoiscluehunt.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tommypacker on 10/15/15.
 */
public class cluesAdapter extends ArrayAdapter<clue> {

    public cluesAdapter(Context context, ArrayList<clue> clues) {
        super(context, 0, clues);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        clue clue = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.clue_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.clueName.setText(clue.getHint());

        if(!clue.getFlag()){
            viewHolder.clueName.setTextColor(ContextCompat.getColor(getContext(), R.color.divider));
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.icons));
        }else{
            viewHolder.clueName.setTextColor(ContextCompat.getColor(getContext(), R.color.icons));
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.clueHighlight));
        }

        try{
            Glide.with(getContext()).load(clue.getImg_url()).override(200,200).centerCrop().into(viewHolder.cluePic);
        }catch (NullPointerException e){
        }

        return convertView;
    }

    static class ViewHolder{
        @Bind(R.id.clueName) TextViewFontBrandon clueName;
        @Bind(R.id.cluePreview) ImageView cluePic;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

}

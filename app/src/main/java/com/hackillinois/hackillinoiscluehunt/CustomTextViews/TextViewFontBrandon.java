package com.hackillinois.hackillinoiscluehunt.CustomTextViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by tommypacker for HackIllinois' 2016 Clue Hunt
 */
public class TextViewFontBrandon extends TextView {
    public TextViewFontBrandon(Context context, AttributeSet attrs) {
        super(context, attrs);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Brandon_med.otf");
        setTypeface(typeface);
    }
}

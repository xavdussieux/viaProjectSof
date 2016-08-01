package com.example.xavier.viaproject;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Xavier on 31/07/2016.
 */
public class ScalableButton extends Button {

    public ScalableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScalableButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width * 70 / 100, height);
    }
}

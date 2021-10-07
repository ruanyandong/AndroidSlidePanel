package com.ryd.slidingpanel.xpanel.lib.view.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class TopView extends FrameLayout {

    public TopView(Context context) {
        super(context);
        setBackgroundColor(Color.TRANSPARENT);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private int mHeight = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeight = mHelper == null ? 0 : mHelper.measureHeight(widthMeasureSpec);
        Log.d("ruanyandong", "XpTopView onMeasure: mHeight "+mHeight);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private ITopSpaceViewHeightCalculate mHelper;

    public void setSpaceViewHeightCalculate(ITopSpaceViewHeightCalculate helper) {
        mHelper = helper;
    }

    public interface ITopSpaceViewHeightCalculate {
        int measureHeight(int widthSpec);
    }
}

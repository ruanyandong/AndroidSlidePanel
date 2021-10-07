package com.ryd.slidingpanel.xpanel.lib.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class BottomView extends FrameLayout {


    public interface IBottomViewHeightCaculate {
        int measureBottomHeight();
    }

    private IBottomViewHeightCaculate mInterface;


    public BottomView(@NonNull Context context) {
        this(context, null);
    }

    public BottomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void bindInterface(IBottomViewHeightCaculate i) {
        mInterface = i;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = mInterface == null ? 0 : mInterface.measureBottomHeight();
        Log.d("xpanel_bottomview", "height = " + height);
        //setMeasuredDimension的measuredHeight/Width高八位是状态，低二十四位才是真正的尺寸，这也就是View.getMeasuredWidth/Height和View.getMeasuredWidth/HeightAndState()方法的区别所在
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }
}

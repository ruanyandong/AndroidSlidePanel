package com.ryd.slidingpanel.hoverview.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.ryd.slidingpanel.R;
/**
 * 类似于 android.support.design.widget.NavigationView
 */
public class HoverView extends LinearLayout implements ViewStateManager {

    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;
    /**
     * 关闭状态，拉倒底部
     */
    public static final float TOP_CLOSE = 1.0f;
    /**
     * 全屏状态，拉倒顶部
     */
    private float mTopFill = 0.0f;
    /**
     * 悬停状态，距离顶部0.6的屏幕高度，距离底部0.4屏幕高度
     */
    private float mTopHover = 0.6f;

    public float getTopFill() {
        return mTopFill;
    }

    public void setTopFill(float topFill) {
        mTopFill = topFill;
    }

    public float getTopHover() {
        return mTopHover;
    }

    public void setTopHover(float topHover) {
        mTopHover = topHover;
    }

    public HoverViewContainer getContainer(){
        if(this.getParent() instanceof HoverViewContainer)
            return (HoverViewContainer)this.getParent();
        return null;
    }

    public HoverView(Context context) {
        super(context);
        init(context);
        initAttrs(context, null);
    }

    public HoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttrs(context, attrs);
    }

    public HoverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttrs(context, attrs);
    }

    private void init(Context context) {
        mContext = context;
    }

    protected void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HoverView);
        mTopFill = ta.getFloat(R.styleable.HoverView_mTopFill, mTopFill);
        mTopHover = ta.getFloat(R.styleable.HoverView_mTopHover, mTopHover);
        ta.recycle();
    }

    // ------ 对外接口 ViewStateManager: begin ------
    @Override
    public void changeState(ViewState viewState){
        changeState(viewState, true);
    }

    @Override
    public void changeState(ViewState viewState, boolean isSmoothScroll) {
        if(getContainer() != null)
            getContainer().changeState(viewState, isSmoothScroll);
    }

    @Override
    public ViewState getState() {
        if(getContainer() != null)
            return getContainer().getState();
        return ViewState.CLOSE;
    }
    // ------ 对外接口 ViewStateManager: end ------
}

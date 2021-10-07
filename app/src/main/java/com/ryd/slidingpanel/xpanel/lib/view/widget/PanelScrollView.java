package com.ryd.slidingpanel.xpanel.lib.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;
import androidx.core.widget.NestedScrollView;

import com.ryd.slidingpanel.xpanel.lib.util.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class PanelScrollView extends NestedScrollView {

    private static final String TAG = "ruanyandong";

    /**
     * 拉起态
     */
    public static final int X_PULL_UP = 0x1;
    /**
     * 默认态
     */
    public static final int X_DEFAULT = 0x2;
    /**
     * 收缩态
     */
    public static final int X_PULL_DOWN = 0x3;


    /**
     * 往上滑动
     */
    public static final int XP_SCROLL_UP = 1;
    /**
     * 往下滑动
     */
    public static final int XP_SCROLL_DOWN = 2;

    /**
     * 停止态
     */
    public static final int XP_SCROLL_IDLE = 3;



    private ScrollViewListener mScrollViewListener;

    /**
     * 滑动前的状态
     */
    @PanelStatus
    private int mLastStatus = X_DEFAULT;

    /**
     * 是否禁止猛掷
     */
    private boolean mBlockFlinging;

    public void setBlockFlinging(boolean blockFlinging) {
        mBlockFlinging = blockFlinging;
    }

    /**
     * 迅速按下、滑动再抬起
     * @param velocityY
     */
    @Override
    public void fling(int velocityY) {
        if (!mBlockFlinging) {
            super.fling(velocityY);
        }
    }


    @IntDef({X_PULL_UP, X_DEFAULT, X_PULL_DOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PanelStatus {
    }


    private Context mContext;

    private static final long DELAY = 10;

    /**
     * 当前ScrollY
     */
    private int currentScrollY;
    /**
     * 手指第一次按下时在ScrollView上的y坐标
     */
    private float mFirstTouchY;

    private float mScrollDistance;

    private Runnable scrollCheckTask;


    public PanelScrollView(Context context) {
        this(context, null, 0);
    }

    public PanelScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanelScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private boolean scrollEnabled = true;

    public void setScrollEnabled(boolean scrollEnabled) {
        this.scrollEnabled = scrollEnabled;
    }

    private void init(Context context) {
        mContext = context;

        scrollCheckTask = new Runnable() {
            @Override
            public void run() {
                // 获取ScrollY
                int newScroll = getScrollY();
                Log.e(TAG, "run: newScroll getScrollY() "+newScroll+" currentScrollY "+currentScrollY);

                if (currentScrollY == newScroll) {//如果之前的currentScrollY和新获取的ScrollY相等，就停止滑动了，回调当前滑动的距离，并进行平滑滚动
                    if (onScrollListener != null) {
                        onScrollListener.onScrollStopped(mScrollDistance);

                    }
                } else {
                    if (onScrollListener != null) {// 如果在滑动就回调正在滑动，获取当前的ScrollY，然后每个0.01秒执行一次runnable
                        onScrollListener.onScrolling();
                    }
                    currentScrollY = getScrollY();
                    postDelayed(scrollCheckTask, DELAY);
                }
            }
        };

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // 把滑动的x，y坐标回调出去
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.d(TAG, "onTouch: MotionEvent.ACTION_MOVE  event.getX() "+event.getX()+" event.getY() "+event.getY());
                    if (mScrollViewListener != null) {
                        mScrollViewListener.event(event.getX(), event.getY());
                    }
                }

                // 手指抬起来时的Y坐标和手指第一次按下时的y坐标对比，大于向下滑动，小于向上滑动
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "onTouch: MotionEvent.ACTION_UP  event.getY() "+event.getY()+"  mFirstTouchY "+mFirstTouchY);
                    if (event.getY() != mFirstTouchY) {
                        // 滑动方向
                        int direction = event.getY() > mFirstTouchY ? XP_SCROLL_DOWN : XP_SCROLL_UP;
                        // 滑动距离
                        mScrollDistance = Math.abs(event.getY() - mFirstTouchY);
                        if (mScrollViewListener != null) {
                            mScrollViewListener.direction(direction);
                        }
                        // 当前滑动方向和之前不一致，便重新设置
                        if (getDirection() != direction) {
                            setDirection(direction);
                        }
                    }
                    // 得倒当前ScrollY
                    currentScrollY = getScrollY();
                    // 手指抬起来的时候开始执行runnable
                    postDelayed(scrollCheckTask, DELAY);
                }

                // 不拦截事件，保证事件传递
                return false;
            }
        });
    }

    public void setDirectionListener(ScrollViewListener listener) {
        mScrollViewListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 按下时拿到ScrollView的LinearLayout
            View cellContainer = getChildAt(0);

            if (cellContainer instanceof ViewGroup && ((ViewGroup) cellContainer).getChildCount() > 1) {
                Log.d(TAG, "dispatchTouchEvent: ((ViewGroup) cellContainer).getChildCount() "+((ViewGroup) cellContainer).getChildCount());
                // 获取第一张卡片在屏幕中的位置(TopView不算卡片)
                View firstView = ((ViewGroup) cellContainer).getChildAt(1);
                int[] loc = new int[2];
                if (firstView != null) {
                    firstView.getLocationOnScreen(loc);

                    int cannot = loc[1];
                    Log.d(TAG, "dispatchTouchEvent: loc[0] "+loc[0]+" loc[1] "+loc[1]+" getStatus() "+getStatus());
                    // 如果当前是拉起态，如果手指的y坐标小于30dp，则不拦截此消息(不在滚动区域内)
                    if (getStatus() == X_PULL_UP) {
                        // 97 - 67
                        cannot = Utils.dip2px(mContext, 30);
                    } else {// 如果不是拉起态，则要减去第一张卡片之上还能滚动的区域的距离，这里是0
                        cannot -= mScrollViewListener.getAreaCanScrollAboveFirstCard();
                    }

                    Log.d(TAG, "dispatchTouchEvent: ev.getRawY() "+ev.getRawY()+" cannot "+cannot);

                    if (ev.getRawY() < cannot) {
                        return false;
                    }   // 在dispatchTouchEvent函数中返回false后，消息并不会停止传递，而是向父控件的onTouchEvent函数回传
                }
            } else {
                return false;
            }

        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!scrollEnabled) {
            return false;
        }

        try {
            return super.onTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mLastStatus = getStatus();
            mFirstTouchY = ev.getY();
            Log.d(TAG, "onInterceptTouchEvent: mLastStatus "+mLastStatus+" mFirstTouchY "+mFirstTouchY);
        }
        return super.onInterceptTouchEvent(ev);
    }

    public @PanelStatus
    int getLastStatus() {
        return mLastStatus;
    }

    /**
     * 设置上一次的状态，不止是点击的时候设置，主动调用三态前也要设置 lastStatus，否则一些判断是错误的
     *
     * @param lastStatus
     */
    public void setLastStatus(@PanelStatus int lastStatus) {
        mLastStatus = lastStatus;

    }

    public interface OnScrollListener {
        void onScrollChanged(int x, int y, int oldX, int oldY);

        void onScrollStopped(float scrollDistance);

        void onScrolling();
    }

    private OnScrollListener onScrollListener;

    /**
     * @param onScrollListener
     */
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        Log.d(TAG, "onScrollChanged: ScrollView  x "+x+" y "+y+" oldX "+oldX+" oldY "+oldY);
        if (onScrollListener != null) {
            onScrollListener.onScrollChanged(x, y, oldX, oldY);
        }
    }


    // 当前XPanel所处的状态
    @PanelStatus
    private int mCurrentStatus;

    @ScrollDirection
    private int mDirection;

    public void setCurrentStatus(@PanelStatus int status) {
        this.mCurrentStatus = status;
    }

    public @PanelStatus
    int getStatus() {
        return mCurrentStatus;
    }

    public @ScrollDirection
    int getDirection() {
        return mDirection;
    }


    private void setDirection(@ScrollDirection int mDirection) {
        this.mDirection = mDirection;
    }



    public interface ScrollViewListener {
        void direction(@ScrollDirection int direction);

        void event(float eventX, float eventY);

        int getAreaCanScrollAboveFirstCard();
    }


}

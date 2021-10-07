package com.ryd.slidingpanel.xpanel.lib.view;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.ryd.slidingpanel.xpanel.lib.view.widget.PanelScrollView.X_PULL_UP;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;

import com.ryd.slidingpanel.R;
import com.ryd.slidingpanel.xpanel.lib.base.CardProperty;
import com.ryd.slidingpanel.xpanel.lib.base.Config;
import com.ryd.slidingpanel.xpanel.lib.callback.ICallback;
import com.ryd.slidingpanel.xpanel.lib.callback.IView;
import com.ryd.slidingpanel.xpanel.lib.sdk.IPanelAbility;
import com.ryd.slidingpanel.xpanel.lib.util.Utils;
import com.ryd.slidingpanel.xpanel.lib.view.widget.BottomView;
import com.ryd.slidingpanel.xpanel.lib.view.widget.PanelScrollView;
import com.ryd.slidingpanel.xpanel.lib.view.widget.ScrollDirection;
import com.ryd.slidingpanel.xpanel.lib.view.widget.TopView;

import java.util.ArrayList;
import java.util.List;


public class PanelView implements PanelScrollView.OnScrollListener, IView, IPanelAbility {

    private static final String TAG = "ruanyandong";
    /**
     * 卡片间距
     */
    private static final float S_CARD_DISTANCE = 10;
    /**
     * 卡片左右距离屏幕两侧的距离
     */
    private static final float S_CARD_LEFT_RIGHT_MARGIN = 10;
    /**
     * ScrollView距离第一张卡片的距离，也就是ScrollView的PaddingTop值
     */
    private static final int S_SCROLLVIEW_TO_SCREEN_TOP_DP = 97;

    private Context mContext;
    private Activity mActivity;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Config mConfig;
    private LayoutInflater mInflater;

    // 布局的根View
    private FrameLayout mRootView;
    /**
     * 滚动的ScrollView
     */
    private PanelScrollView mScrollView;
    /**
     * 装载卡片的容器
     */
    private LinearLayout mCellContainer;
    /**
     * 头部透明View
     */
    private TopView mTopTransView;
    /**
     * 底部透明View
     */
    private BottomView mBottomTransView;


    private TopView.ITopSpaceViewHeightCalculate mTopTransViewCalculate;
    private BottomView.IBottomViewHeightCaculate mBottomTransViewCalculate;


    /**
     * 引起上拉吸顶的阈值
     */
    private int mUpThreshold;
    /**
     * 回缩态高度
     */
    protected int mDownHeight;
    /**
     * 第一次初始化后默认态下所有显示出来的的卡片（包含显示一部分的卡片）总高度
     */
    private int mLastDefaultShow;

    /**
     * ScrollerView 实际高度，match_parent  padding_top 97dp
     */
    protected int mScrollerHeight;

    /**
     * 吸顶态首个可见卡片距离屏幕顶部高度
     */
    private int mStateUp = 0;
    /**
     * 默认态首个可见卡片距离屏幕顶部高度
     */
    private int mStateDefault = 0;
    /**
     * 吸底态首个可见卡片距离屏幕顶部高度
     */
    private int mStateDown = 0;

    /**
     * 引起下拉回缩或上拉吸顶的变化值，滑动距离超过这个值才会变化状态
     */
    private static int mPullValue;

    /**
     * 卡片宽度
     */
    private int mCardWidth;


    /**
     * 首张卡片及消息位由吸顶态下滑是否已经重新展示
     */
    private boolean mFirstCardAndMsgShowed = true;

    /**
     * 给业务的高度等回调
     */
    private ICallback mCallback;

    /**
     * 卡片，消息位，消息位背景圆角
     */
    private int mCardRoundCornerPx;

    private boolean isAddBottomCard;

    /**
     * 卡片加消息位最大展示高度
     */
    private int mDefaultMaxHeight;


    private boolean mIsOnPause;
    private List<CardProperty> mCardProperties = new ArrayList<>();

    /**
     * 首张卡片高度变化且默认态高度变化，动画中
     */
    private boolean inAnimFirstCardHeightWillChangeTo;

    /**
     * 底部透明卡片的高度
     */
    private int mBottomViewHeight;

    public PanelView(Activity context) {
        mContext = context;
        mActivity = context;
    }

    public void init(Config config) {
        if (config == null) {
            config = new Config.Builder().build();
        }
        Log.d("ruanyandong", "init config " + config.getDefaultDpSecondCardShowHeight());
        initConfig(config);
        intView();
    }

    public void setScrollEnabled(boolean enabled) {
        if (mScrollView != null) {
            mScrollView.setScrollEnabled(enabled);
        }
    }

    private void intView() {
        mInflater = LayoutInflater.from(mContext);
        mRootView = (FrameLayout) mInflater.inflate(R.layout.panel_layout, null);
        mScrollView = mRootView.findViewById(R.id.xp_scroll_view);
        mScrollView.setOnScrollListener(this);
        // 禁止快速滑动
        mScrollView.setBlockFlinging(true);
        
        mCellContainer = mRootView.findViewById(R.id.xp_cell_container);

        mScrollView.setDirectionListener(new PanelScrollView.ScrollViewListener() {
            @Override
            public void direction(int direction) {

            }

            @Override
            public void event(float eventX, float eventY) {

            }

            @Override
            public int getAreaCanScrollAboveFirstCard() {
                return 0;
            }
        });
    }

    @Override
    public View getView() {
        return mRootView;
    }

    /**
     * 配置能力
     *
     * @param config
     */
    private void initConfig(Config config) {
        mConfig = config;
        mLastDefaultShow = Utils.dip2px(mContext, 250);
        mDownHeight = Utils.dip2px(mContext, config.getDefaultDpFoldHeight());
        mCardRoundCornerPx = Utils.dip2px(mContext, config.getCardRoundedCornerDp());
        // 97 为 状态栏+标题高度
        mScrollerHeight = Utils.getScreenHeight(mActivity) - Utils.dip2px(mContext, S_SCROLLVIEW_TO_SCREEN_TOP_DP);

        mPullValue = Utils.dip2px(mContext, 30);


        setThreeStatusHeight();
        mUpThreshold = (int) (Utils.getScreenHeight(mActivity) * 0.65);
        mDefaultMaxHeight = (int) (Utils.getScreenHeight(mActivity) * 0.6);
        mCardWidth = Utils.getScreenWidth(mContext) - 2 * Utils.dip2px(mContext, S_CARD_LEFT_RIGHT_MARGIN);
    }


    private void setThreeStatusHeight() {
        mStateUp = Utils.dip2px(mContext, S_SCROLLVIEW_TO_SCREEN_TOP_DP);
        // mStateDefault 在卡片测量完后设置
        mStateDown = Utils.getScreenHeight(mActivity) - mDownHeight;
    }

    private void addTranslateTopView(final int defaultFoldHeight) {
        // height 1694
        mTopTransView = new TopView(mContext);
        mTopTransViewCalculate = new TopView.ITopSpaceViewHeightCalculate() {
            @Override
            public int measureHeight(int widthSpec) {
                return mScrollerHeight - defaultFoldHeight;
            }
        };
        Log.d(TAG, "addTranslateTopView: "+(mScrollerHeight - defaultFoldHeight));
        mTopTransView.setSpaceViewHeightCalculate(mTopTransViewCalculate);
        mCellContainer.addView(mTopTransView);
    }

    private void addTranslateBottomView(float bottomHeight) {
        if (!isAddBottomCard) {
            mBottomTransView = new BottomView(mContext);
            int temHeight = 0;
            int screenHeight = mScrollerHeight;
            if (bottomHeight < screenHeight) {
                temHeight = screenHeight - (int) bottomHeight;
            }
            mBottomViewHeight = temHeight;
            mBottomTransViewCalculate = new BottomView.IBottomViewHeightCaculate() {
                @Override
                public int measureBottomHeight() {
                    return mBottomViewHeight;
                }
            };
            Log.d(TAG, "addTranslateBottomView: mBottomViewHeight "+mBottomViewHeight);

            mBottomTransView.bindInterface(mBottomTransViewCalculate);

            mCellContainer.addView(mBottomTransView);

            isAddBottomCard = true;
        }

    }


    @Override
    public void setData(final List<CardProperty> components) {
        setData(components, false);
    }

    @Override
    public void addCard(final CardProperty component, final int index) {
        addView(component.getView(), index, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //动画结束执行计算,延迟1s回调，确保view展示
            }
        });
    }



    public void setData(final List<CardProperty> components, boolean needTransAnim) {
        if (components == null || components.isEmpty()) {
            return;
        }
        if (mIsOnPause) {
            mCardProperties = components;
            return;
        }
        realSetDataToPanel(components, needTransAnim);

    }

    private void realSetDataToPanel(final List<CardProperty> components, boolean needTransAnim) {
        if (mCellContainer == null) {
            return;
        }
        final List<CardView> list = setNewCards(components);
        deleteOldCards();
        setNewCards2Container(list, components, false);

    }

    private List<CardView> setNewCards(final List<CardProperty> components) {
        List<CardView> list = new ArrayList<>();
        for (int i = 0; i < components.size(); i++) {
            CardProperty component = components.get(i);
            if (component != null) {
                View v = component.getView();
                if (v != null) {
                    if (v.getParent() != null && v.getParent() instanceof CardView) {
                        ((CardView) v.getParent()).removeAllViews();
                    }

                    CardView cardView = getCardContainer();
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    cardView.addView(v, params);
                    list.add(cardView);
                }
            }
        }
        return list;
    }

    private CardView getCardContainer() {
        CardView cardView = (CardView) mInflater.inflate(R.layout.panel_card_parent_layout, mCellContainer, false);
        cardView.setRadius(mCardRoundCornerPx);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(mCardRoundCornerPx);
        drawable.setColor(Color.WHITE);
        cardView.setBackground(drawable);
        return cardView;
    }

    private void setNewCards2Container(List<CardView> list, final List<CardProperty> components, final boolean needTransAnim) {
        addTranslateTopView(mDownHeight);
        final int size = components.size();
        Log.d(TAG, "cardcount:" + size);
        for (int i = 0; i < list.size(); i++) {
            mCellContainer.addView(list.get(i));
        }
        mCellContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Log.d(TAG, "onPreDraw");
                mCellContainer.getViewTreeObserver().removeOnPreDrawListener(this);

                if (mCellContainer.getChildAt(1) != null) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mCellContainer.getChildAt(1).setTranslationZ(60);
                    }
                }

                int bottomHeight = 0;
                for (int i = 1; i < mCellContainer.getChildCount(); i++) {
                    if (mCellContainer.getChildAt(i) instanceof CardView) {
                        bottomHeight += mCellContainer.getChildAt(i).getMeasuredHeight() + Utils.dip2px(mContext, S_CARD_DISTANCE);
                    }
                }

                addTranslateBottomView(bottomHeight);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setConfigValue(size);
                        mScrollView.setLastStatus(mScrollView.getStatus());
                        defaultState(needTransAnim);
                        Log.d(TAG, "run: 三态：addView 默认态 ");
                    }
                });

                return false;
            }
        });
    }

    private void deleteOldCards() {
        // 说明除了顶部透明卡片外还有其他卡片
        int lastChildCount = mCellContainer.getChildCount();
        for (int i = 0; i <= lastChildCount - 1; i++) {
            if (mCellContainer.getChildAt(i) instanceof CardView) {
                CardView cardView = (CardView) mCellContainer.getChildAt(i);
                if (cardView != null) {
                    // 消除子view与父view的关系
                    cardView.removeAllViews();
                }

            }

        }
        mCellContainer.removeAllViews();

        isAddBottomCard = false;

    }

    @Override
    public void setShowOneCard() {
        setConfigValue(1);
        mScrollView.setLastStatus(mScrollView.getStatus());
        defaultState(false);
        Log.d(TAG, "setShowOneCard: 三态：多张卡片只展示一张卡片，默认态 ");
    }

    @Override
    public void firstCardHeightWillChangeTo(Config config) {
        initConfig(config);
        setConfigValue(-1);
        mScrollView.setLastStatus(mScrollView.getStatus());

        inAnimFirstCardHeightWillChangeTo = true;


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                inAnimFirstCardHeightWillChangeTo = false;
            }
        }, 600);

        if (getStatus() != X_PULL_UP) {
            defaultState(false);
        }
    }

    @Override
    public void cardHeightChange(int index, int heightToPx) {

        int bottomHeight = 0;

        for (int i = 1; i < mCellContainer.getChildCount(); i++) {
            if (mCellContainer.getChildAt(i) instanceof CardView) {
                bottomHeight += mCellContainer.getChildAt(i).getMeasuredHeight() + Utils.dip2px(mContext, S_CARD_DISTANCE);
                Log.d(TAG, "xpanel_bottom item height = " + mCellContainer.getChildAt(i).getMeasuredHeight());
            }
        }
        int height = mCellContainer.getChildAt(index).getMeasuredHeight();
        bottomHeight -= (height - heightToPx); // 删除卡片高度 + 间距 10dp

        if (mBottomTransView == null) {
            addTranslateBottomView(bottomHeight);
        } else {
            int temHeight = 0;
            int screenHeight = mScrollerHeight;
            if (bottomHeight < screenHeight) {
                temHeight = screenHeight - bottomHeight;
            }
            mBottomViewHeight = temHeight;
            mBottomTransView.requestLayout();
        }
    }

    private void setConfigValue(int bizCardCount) {
        Log.d(TAG, "setConfigValue: hasZeroCardAfterAddBottomCard() "+hasZeroCardAfterAddBottomCard()+" mConfig.getFirstCardHeightWillChangeTo() "+mConfig.getFirstCardHeightWillChangeTo()+" mConfig.getOneCardAndXDp() "+mConfig.getOneCardAndXDp()+" bizCardCount "+bizCardCount);
        if (!hasZeroCardAfterAddBottomCard()) {
            if (mConfig.getFirstCardHeightWillChangeTo() != 0) {
                mLastDefaultShow = Utils.dip2px(mContext, S_CARD_DISTANCE + mConfig.getDefaultDpSecondCardShowHeight()) + mConfig.getFirstCardHeightWillChangeTo();
                Log.d("setConfigValue", mConfig.getDefaultDpSecondCardShowHeight() + ";" + mConfig.getFirstCardHeightWillChangeTo());
            } else if (mConfig.getOneCardAndXDp() != 0) {
                mLastDefaultShow = mCellContainer.getChildAt(1).getMeasuredHeight() + Utils.dip2px(mContext, S_CARD_DISTANCE + mConfig.getOneCardAndXDp());
            } else if (bizCardCount == 1) {
                mLastDefaultShow = mCellContainer.getChildAt(1).getMeasuredHeight() + Utils.dip2px(mContext, S_CARD_DISTANCE);
            } else {
                mLastDefaultShow = mCellContainer.getChildAt(1).getMeasuredHeight() + Utils.dip2px(mContext, S_CARD_DISTANCE + mConfig.getDefaultDpSecondCardShowHeight());
            }

            if (mLastDefaultShow  > mDefaultMaxHeight) {
                mLastDefaultShow = mDefaultMaxHeight;
            }
            // 默认态计算完成，如果比吸底态高度小，则以吸底态高度为默认态高度
            if (mLastDefaultShow <= mDownHeight) {
                mLastDefaultShow = mDownHeight + 1; // +1 的原因：从吸底态滑动到默认态，调整消息位，如果相等就不会滑动
            }
            Log.d(TAG, "setConfigValue" + mConfig.getDefaultDpSecondCardShowHeight() + " mLastDefaultShow: " + mLastDefaultShow);
            mStateDefault = Utils.getScreenHeight(mActivity) - mLastDefaultShow;
        }

    }

    public void setCallback(ICallback xpCallback) {
        mCallback = xpCallback;
        mCallback.onCardWithCallback(mCardWidth);
    }

    /**
     * 任意位置添加卡片
     *
     * @param v
     * @param index
     */
    private void addView(View v, int index, Animator.AnimatorListener listener) {
        if (mCellContainer == null) {
            return;
        }
        final CardView cardView = getCardContainer();

        if (cardView == null) {
            return;
        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        if (v.getParent() != null && v.getParent() instanceof ViewGroup) {
            ((ViewGroup) v.getParent()).removeView(v);
        }
        cardView.addView(v, params);
        mCellContainer.addView(cardView, index);
    }
    /**
     * 吸顶态删除view动画
     *
     * @param index
     */
    @Override
    public void removeCard(final int index) {
        if (mCellContainer == null) {
            return;
        }
        final View view = mCellContainer.getChildAt(index);
        if (view == null) {
            return;
        }

        mCellContainer.removeViewAt(index);
    }

    /**
     * @param direction
     * @param currentY  首张可见卡片距离屏幕顶部的距离
     */
    private void checkState(@ScrollDirection int direction, int currentY) {
        Log.d(TAG, "checkState: direction "+direction);
        if (direction == PanelScrollView.XP_SCROLL_DOWN) { // 方向向下

            if (mStateUp < currentY && currentY < mStateUp + mPullValue) {
                //吸顶态
                upState();
                Log.d(TAG, "checkState: 三态：下拉，吸顶");
            } else if (mStateUp + mPullValue <= currentY && currentY <= mStateDefault) {
                //默认态
                defaultState(false);
                Log.d(TAG, "checkState: 三态：下拉，默认");
            } else if (mStateDefault < currentY && currentY <= mStateDown && mScrollView.getLastStatus() != PanelScrollView.X_PULL_DOWN) {
                //吸底态
                bottomState();
                if (currentY == mStateDown) {
                    checkBgAlpha();
                }

                Log.d(TAG, "checkState: 三态：下拉，吸底");
            } else {
                Log.d(TAG, "checkState: 三态：下拉 currentY： "+currentY);
            }
        } else { // 方向向上
            if (currentY <= mUpThreshold && mScrollView.getLastStatus() != X_PULL_UP) {
                upState();
                Log.d(TAG, "checkState: 三态：上拉，吸顶");

            } else if (mUpThreshold < currentY && currentY < mStateDown) {
                defaultState();
                Log.d(TAG, "checkState: "+"三态：上拉，默认" + mUpThreshold + "--" + mStateDefault);
            } else {
                Log.d(TAG, "checkState: "+"三态：上拉 currentY" + currentY);
            }
        }
    }

    /**
     * 首张卡片距离屏幕顶部的距离
     *
     * @return
     */
    private int getScreenTopToFirstCard() {
        if (mCellContainer == null || mCellContainer.getChildCount() < 2 || mCellContainer.getChildAt(1) == null) {
            return 0;
        }
        int[] loc = new int[2];
        mCellContainer.getChildAt(1).getLocationOnScreen(loc);
        Log.d(TAG, "getScreenTopToFirstCard: "+loc[1] + " !! " + mScrollView.getTop() + " @ " + Utils.getScreenHeight(mActivity) + " $ " + mScrollerHeight);
        return loc[1];
    }

    /**
     * 首张卡片的高度，距离屏幕底部
     *
     * @return
     */
    @Override
    public int getFirstCardHeight() {
        return Utils.getScreenHeight(mActivity) - getScreenTopToFirstCard();
    }

    @Override
    public void onScrollChanged(int x, int y, int oldX, int oldY) {
        checkBgAlpha();
    }

    private void checkBgAlpha() {
        if ((getFirstCardHeight() > mLastDefaultShow) && (!inAnimFirstCardHeightWillChangeTo || getStatus() == X_PULL_UP)) {
            // 吸顶态滑动列表，这时候增加、删除卡片，如果第一张卡片高度变化，此时会做translationY,会导致onScrollChanged()回调，此时inAnimFirstCardHeightWillChangeTo
            float ratio = (float) ((getFirstCardHeight() - mLastDefaultShow) / ((mScrollerHeight - mLastDefaultShow) * 0.5));
            ratio = Math.max(ratio, 0);
            ratio = Math.min(ratio, 1);
            mRootView.setBackgroundColor(Color.parseColor("#0A121A"));
            mRootView.getBackground().setAlpha((int) (ratio * 0.7 * 255));
        } else {
            mRootView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onScrollStopped(float scrollDistance) {
        Log.d(TAG, "onScrollStopped "+scrollDistance + " : " + ViewConfiguration.get(mContext).getScaledTouchSlop());
        if (scrollDistance < ViewConfiguration.get(mContext).getScaledTouchSlop()) {
            return;
        }

        checkState(mScrollView.getDirection(), getScreenTopToFirstCard());

    }

    @Override
    public void fistCardHeightChange(int height) {
        Log.d(TAG, "fistCardHeightChange: height "+height+" getStatus() "+getStatus()+" mCellContainer.getChildCount() "+mCellContainer.getChildCount());
        if (getStatus() == X_PULL_UP) {
            for (int i = 2; i < mCellContainer.getChildCount(); i++) {
                View tramsView = mCellContainer.getChildAt(i);
                tramsView.setTranslationY(-(height + Utils.dip2px(mContext, S_CARD_DISTANCE)));
            }
        }
    }


    @Override
    public void onScrolling() {
    }

    /**
     * 滚动到吸顶态
     * 当前最上面可见卡片在屏幕中的位置
     * |--------^--------|
     * |        |        |
     * |        |currentY|
     * |        |        |
     * |        |        |
     * | *******^******* |
     * | *             * |
     * | *************** |
     * |_________________|
     */
    public void upState() {
        if (mCellContainer == null || mCellContainer.getChildAt(1) == null) {
            return;
        }
        int[] loc = new int[2];
        // 距离屏幕顶部的距离
        mCellContainer.getChildAt(1).getLocationOnScreen(loc);
        int currentY = loc[1];
        mScrollView.setCurrentStatus(X_PULL_UP);
        Log.e(TAG, "currentY:" + currentY + ", status:" + Utils.getStatusBarHeight(mContext) + ", 97: " + Utils.dip2px(mContext, 97));
        mScrollView.smoothScrollBy(0, currentY - Utils.dip2px(mContext, S_SCROLLVIEW_TO_SCREEN_TOP_DP));

        mCallback.onPanelStatusHeight(X_PULL_UP, mScrollerHeight);

        Log.d(TAG, "upState: state吸顶态 ");
    }

    /**
     * 没有业务卡片
     *
     * @return true
     */
    private boolean hasZeroCardAfterAddBottomCard() {
        return mCellContainer == null || mCellContainer.getChildCount() <= 2
                || mCellContainer.getChildAt(1) == null;
    }

    /**
     * 滚动到默认态
     */
    public void defaultState(boolean needTransAnim) {
        if (mCellContainer == null || mCellContainer.getChildAt(1) == null) {
            return;
        }
        int[] loc = new int[2];
        // 距离屏幕顶部的距离
        // getLocationOnScreen方法获取到的是View显示在屏幕内部分的左上角顶点在屏幕中的绝对位置.(屏幕范围包括状态栏).
        mCellContainer.getChildAt(1).getLocationOnScreen(loc);
        int currentY = loc[1];
        mScrollView.setCurrentStatus(PanelScrollView.X_DEFAULT);
        mScrollView.smoothScrollBy(0, -(Utils.getScreenHeight(mActivity) - currentY - mLastDefaultShow), mFirstCardAndMsgShowed ? 600 : 300);

        mCallback.onPanelStatusHeight(PanelScrollView.X_DEFAULT, mLastDefaultShow );
        Log.d(TAG, "defaultState: 滚动到默认态 XpUtils.getScreenHeight(mActivity) "+ Utils.getScreenHeight(mActivity)+" currentY "+currentY+" mLastDefaultShow "+mLastDefaultShow);
    }

    /**
     * 滚动到默认态
     */
    public void defaultState() {
        defaultState(false);
    }



    /**
     * 滚动到吸底态
     */
    public void bottomState() {
        if (mCellContainer == null || mCellContainer.getChildAt(1) == null) {
            return;
        }
        int[] loc = new int[2];
        // 距离屏幕顶部的距离
        mCellContainer.getChildAt(1).getLocationOnScreen(loc);
        int currentY = loc[1];
        mScrollView.setCurrentStatus(PanelScrollView.X_PULL_DOWN);
        mScrollView.smoothScrollBy(0, -(Utils.getScreenHeight(mActivity) - currentY - mDownHeight), mFirstCardAndMsgShowed ? 600 : 250);
        mCallback.onPanelStatusHeight(PanelScrollView.X_PULL_DOWN, mDownHeight);

        Log.d(TAG, "bottomState: state吸底态");
    }

    @Override
    public void setConfig(Config config) {
        Log.d(TAG, "setConfig" + config.getDefaultDpSecondCardShowHeight());
        initConfig(config);
    }

    public int getStatus() {
        return mScrollView.getStatus();
    }

    @Override
    public void onResume() {
        mIsOnPause = false;
        if (mCardProperties.size() > 0) {
            realSetDataToPanel(mCardProperties, false);
        }
    }

    @Override
    public void onPause() {
        mIsOnPause = true;
    }

    @Override
    public void destroy() {

    }
}

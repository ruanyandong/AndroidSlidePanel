package com.ryd.slidingpanel.xpanel.lib.sdk;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import com.ryd.slidingpanel.xpanel.lib.base.CardProperty;
import com.ryd.slidingpanel.xpanel.lib.base.Config;
import com.ryd.slidingpanel.xpanel.lib.callback.ICallback;
import com.ryd.slidingpanel.xpanel.lib.callback.IView;
import com.ryd.slidingpanel.xpanel.lib.view.PanelView;
import java.util.List;

/**
 * Panel功能的实现
 */
public class PanelAbility implements IPanelAbility, IView {

    private Context mContext;
    private PanelView mPanelView;

    public PanelAbility(Activity context, Config config) {
        mContext = context;
        mPanelView = new PanelView(context);
        mPanelView.init(config);
    }


    public void setData(List<CardProperty> components) {
        mPanelView.setData(components);
    }

    @Override
    public void addCard(CardProperty component, int index) {
        mPanelView.addCard(component, index);
    }

    @Override
    public void removeCard(int index) {
        mPanelView.removeCard(index);
    }

    public void setData(List<CardProperty> components, boolean withAnim) {
        mPanelView.setData(components, withAnim);
    }

    /**
     * 设置是否能够滚动
     * @param enabled
     */
    public void setScrollEnabled(boolean enabled) {
        if (mPanelView != null) {
            mPanelView.setScrollEnabled(enabled);
        }
    }

    public View getView() {
        return mPanelView.getView();
    }



    @Override
    public void setConfig(Config config) {
        mPanelView.setConfig(config);
    }


    @Override
    public void setShowOneCard() {
        mPanelView.setShowOneCard();
    }

    @Override
    public int getFirstCardHeight() {
        return mPanelView.getFirstCardHeight();
    }


    @Override
    public void fistCardHeightChange(int height) {
        mPanelView.fistCardHeightChange(height);
    }

    @Override
    public void firstCardHeightWillChangeTo(Config config) {
        mPanelView.firstCardHeightWillChangeTo(config);
    }

    @Override
    public void cardHeightChange(int index, int heightToPx) {
        mPanelView.cardHeightChange(index, heightToPx);
    }

    @Override
    public void onResume() {
        mPanelView.onResume();

    }

    @Override
    public void onPause() {

        mPanelView.onPause();
    }

    @Override
    public void destroy() {
        mPanelView.destroy();
    }

    @Override
    public void setCallback(ICallback callback) {
        mPanelView.setCallback(callback);
    }

}

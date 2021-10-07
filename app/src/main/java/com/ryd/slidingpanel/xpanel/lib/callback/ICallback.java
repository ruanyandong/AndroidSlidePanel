package com.ryd.slidingpanel.xpanel.lib.callback;


import com.ryd.slidingpanel.xpanel.lib.view.widget.PanelScrollView;

public interface ICallback {
    /**
     * 高度变化回调, 滚动
     *
     * @param height 消息位灰色背景高度
     */
    void onPanelHeightChange(int height);

    /**
     * 三态变化时的高度
     *
     * @param statusHeight
     */
    void onPanelStatusHeight(@PanelScrollView.PanelStatus int xpStatus, int statusHeight);

    /**
     * panel卡片宽度回调
     *
     * @param cardWidthPx 卡片宽度，单位px
     */
    void onCardWithCallback(int cardWidthPx);
}

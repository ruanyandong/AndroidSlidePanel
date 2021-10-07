package com.ryd.slidingpanel.xpanel.lib.sdk;

import android.view.View;

import com.ryd.slidingpanel.xpanel.lib.base.CardProperty;
import com.ryd.slidingpanel.xpanel.lib.base.Config;
import com.ryd.slidingpanel.xpanel.lib.base.ILife;

import java.util.List;

/**
 * Panel的功能
 */
public interface IPanelAbility extends ILife {


    void setData(List<CardProperty> components);

    /**
     * 添加卡片
     * @param component
     * @param index 卡片位置，从1开始
     */
    void addCard(CardProperty component, int index);

    /**
     * 删除卡片
     * @param index 卡片位置，从1开始
     */
    void removeCard(int index);

    View getView();

    /**
     * 更新config
     * @param config
     */
    void setConfig(Config config);

    /**
     * 多张卡片时只展示一张卡片
     */
    void setShowOneCard();

    /**
     * 获取首张卡片距离屏幕底部的高度
     * @return
     */
    int getFirstCardHeight();


    /**
     * 第一张卡片高度变化，吸顶态时变化，其他卡片的translationY需要调整
     */
    void fistCardHeightChange(int height);

    /**
     * 首张卡片变化，高度做动画，配置更新首张卡片高度终值&默认态高度值
     * @param config
     */
    void firstCardHeightWillChangeTo(Config config);

    /**
     * 卡片高度变化
     * @param index
     * @param heightToPx，高度变到多少px
     */
    void cardHeightChange(int index, int heightToPx);
}

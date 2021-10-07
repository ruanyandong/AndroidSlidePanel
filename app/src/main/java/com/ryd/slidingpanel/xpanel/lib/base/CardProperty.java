package com.ryd.slidingpanel.xpanel.lib.base;

import android.view.View;

/**
 * 卡片属性
 */
public class CardProperty {

    public CardProperty() {

    }

    private View mView;
    private String mId;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        this.mView = view;
    }

}

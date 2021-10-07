package com.ryd.slidingpanel.xpanel.lib.view.widget;

import static com.ryd.slidingpanel.xpanel.lib.view.widget.PanelScrollView.XP_SCROLL_DOWN;
import static com.ryd.slidingpanel.xpanel.lib.view.widget.PanelScrollView.XP_SCROLL_IDLE;
import static com.ryd.slidingpanel.xpanel.lib.view.widget.PanelScrollView.XP_SCROLL_UP;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({XP_SCROLL_UP, XP_SCROLL_DOWN, XP_SCROLL_IDLE})
@Retention(RetentionPolicy.SOURCE)
public @interface ScrollDirection {
}

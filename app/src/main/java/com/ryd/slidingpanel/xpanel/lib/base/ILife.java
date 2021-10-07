package com.ryd.slidingpanel.xpanel.lib.base;

/**
 * 生命周期
 */
public interface ILife {

    void onResume();

    void onPause();

    /**
     * 慎用，销毁时使用，不可逆
     */
    void destroy();
}

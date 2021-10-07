package com.ryd.slidingpanel.xpanel.lib.base;

/**
 * 配置属性
 */
public class Config {
    /**
     * 卡片圆角dp
     */
    private float cardRoundedCornerDp;

    /**
     * 收缩态高度，单位 dp
     */
    private float defaultDpFoldHeight;

    /**
     * 默认态第二张卡片展示高度，单位dp
     */
    private float defaultDpSecondCardShowHeight;

    /**
     * 只有一张卡片时展示一张卡片 + X Dp
     */
    private float oneCardAndXDp;

    /**
     * 第一张卡片动画高度终值，单位px
     */
    private int firstCardHeightWillChangeTo;



    public float getCardRoundedCornerDp() {
        return cardRoundedCornerDp;
    }


    private Config(Builder builder) {
        cardRoundedCornerDp = builder.cardRoundedCorner;
        defaultDpFoldHeight = builder.defaultFoldHeight;
        defaultDpSecondCardShowHeight = builder.defaultDpSecondCardShowHeight;
        oneCardAndXDp = builder.oneCardAndXDp;
        firstCardHeightWillChangeTo = builder.firstCardHeightWillChangeTo;
    }

    public float getDefaultDpFoldHeight() {
        return defaultDpFoldHeight;
    }

    public float getDefaultDpSecondCardShowHeight() {
        return defaultDpSecondCardShowHeight;
    }

    public int getFirstCardHeightWillChangeTo() {
        return firstCardHeightWillChangeTo;
    }

    public float getOneCardAndXDp() {
        return oneCardAndXDp;
    }


    public static class Builder {
        private float cardRoundedCorner = 20;
        private float defaultFoldHeight = 100;
        private float defaultDpSecondCardShowHeight = 70;
        private float oneCardAndXDp = 0;
        private int firstCardHeightWillChangeTo = 0;

        public Builder() {
        }

        public Builder setRoundedCorner(float cardRoundedCorner) {
            this.cardRoundedCorner = cardRoundedCorner;
            return this;
        }

        public Builder setOneCardAndXDp(float oneCardAndXDp) {
            this.oneCardAndXDp = oneCardAndXDp;
            return this;
        }

        public Builder setFirstCardHeightWillChangeTo(int firstCardHeightWillChangeTo) {
            this.firstCardHeightWillChangeTo = firstCardHeightWillChangeTo;
            return this;
        }

        public Config build() {
            return new Config(this);
        }

        public Builder setDefaultFoldHeight(float defaultFoldHeight) {
            this.defaultFoldHeight = defaultFoldHeight;
            return this;
        }

        public Builder setDefaultDpSecondCardShowHeight(float defaultDpSecondCardShowHeight) {
            this.defaultDpSecondCardShowHeight = defaultDpSecondCardShowHeight;
            return this;
        }
    }
}

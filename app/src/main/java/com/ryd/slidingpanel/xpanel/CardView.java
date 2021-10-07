package com.ryd.slidingpanel.xpanel;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ryd.slidingpanel.R;
import com.ryd.slidingpanel.xpanel.lib.util.Utils;

public class CardView {

    private View rootView;
    private Callback callback;

    public CardView(@NonNull final Context context, final int index) {
        rootView = LayoutInflater.from(context).inflate(R.layout.demo_card_view, null);
        final TextView title = rootView.findViewById(R.id.title);

        if (index == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ValueAnimator animatorHeight = ValueAnimator.ofInt(rootView.getHeight(), Utils.dip2px(context, 100));
                    animatorHeight.setDuration(400);
                    animatorHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            rootView.getLayoutParams().height = (int) animation.getAnimatedValue();
                            rootView.requestLayout();
                            callback.heightChange((Integer) animation.getAnimatedValue());
                        }
                    });
                    callback.willAdd(Utils.dip2px(context, 100));
                    animatorHeight.start();

                }
            }, 5000);
        }

        title.setText("AAAAAAAAAAAAAAAAAAAAAAAAA");
        TextView subtitle = rootView.findViewById(R.id.content);
        subtitle.setText("BBBBBBBBBBBBBBBBB");

        if (index == 1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ValueAnimator animatorHeight = ValueAnimator.ofInt(rootView.getHeight(), Utils.dip2px(context, 100));
                    animatorHeight.setDuration(400);
                    animatorHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            rootView.getLayoutParams().height = (int) animation.getAnimatedValue();
                            rootView.requestLayout();

                        }
                    });
                    animatorHeight.start();
                    callback.secondHeightChange(2, Utils.dip2px(context, 100));

                }
            }, 5000);
        }
    }


    public View getView() {
        return rootView;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void heightChange(int newHeight);
        void willAdd(int ToHeight);
        void remove(int toHeight);
        void secondHeightChange(int index, int to);
    }

}

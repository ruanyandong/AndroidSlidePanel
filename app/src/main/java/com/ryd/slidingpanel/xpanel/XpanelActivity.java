package com.ryd.slidingpanel.xpanel;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.ryd.slidingpanel.R;
import com.ryd.slidingpanel.xpanel.lib.base.CardProperty;
import com.ryd.slidingpanel.xpanel.lib.base.Config;
import com.ryd.slidingpanel.xpanel.lib.callback.ICallback;
import com.ryd.slidingpanel.xpanel.lib.sdk.PanelAbility;
import java.util.ArrayList;
import java.util.List;

public class XpanelActivity extends AppCompatActivity {

    private static final String TAG = "ruanyandong";

    List<CardProperty> list;
    private FrameLayout layout;
    private PanelAbility ability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 实现透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_xpanel);
        layout = findViewById(R.id.layout);

        Config.Builder builder = new Config.Builder();
        Config config = builder.setRoundedCorner(20).setDefaultFoldHeight(100).setDefaultDpSecondCardShowHeight(100).build();

        ability = new PanelAbility(this, config);
        ability.setCallback(new ICallback() {
            @Override
            public void onPanelHeightChange(int height) {
            }

            @Override
            public void onPanelStatusHeight(int xpStatus, int statusHeight) {
            }

            @Override
            public void onCardWithCallback(int cardWidthPx) {

            }
        });

        layout.addView(ability.getView());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                touch(null);
            }
        }, 1000);

    }

    private void setComponents() {
        list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CardProperty component = new CardProperty();
            component.setId("id:" + i);
            CardView cardView = new CardView(this, i);
            cardView.setCallback(new CardView.Callback() {
                @Override
                public void heightChange(int newHeight) {
                    Log.d(TAG, "heightChange: newHeight "+newHeight);
                    ability.fistCardHeightChange(newHeight);
                }

                @Override
                public void willAdd(int ToHeight) {
                    Log.d(TAG, "willAdd: ToHeight "+ToHeight);

                    addCard(ToHeight);


                }

                @Override
                public void remove(int toHeight) {
                    Log.d(TAG, "remove: toHeight "+toHeight);
                    // delete
                    removeCard(toHeight);
                }

                @Override
                public void secondHeightChange(int i, int to) {
                    Log.d(TAG, "secondHeightChange: "+i+" "+to);
                    ability.cardHeightChange(i, to);
                }

                private void removeCard(int to) {
                    ability.removeCard(2);
                    Config.Builder builder = new Config.Builder();
                    Config config = builder.setRoundedCorner(20).setDefaultFoldHeight(100).setDefaultDpSecondCardShowHeight(100).setFirstCardHeightWillChangeTo(to).build();
                    ability.firstCardHeightWillChangeTo(config);
                }

                private void addCard(int to) {
                    touch2(null);
                    Config.Builder builder = new Config.Builder();
                    Config config = builder.setRoundedCorner(20).setDefaultFoldHeight(100).setDefaultDpSecondCardShowHeight(200).setFirstCardHeightWillChangeTo(to).build();
                    ability.firstCardHeightWillChangeTo(config);
                }
            });

            component.setView(cardView.getView());
            list.add(component);
        }
    }

    public void touch(View v) {
        setComponents();
        ability.setData(list, false);

    }

    public void touch2(View view) {
        CardProperty component = new CardProperty();
        final View view1 = new CardView(this, 100).getView();
        component.setView(view1);
        ability.addCard(component, 2);
    }
}
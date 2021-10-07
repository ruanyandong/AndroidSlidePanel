package com.ryd.slidingpanel;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.ryd.slidingpanel.hoverview.HoverViewActivity;
import com.ryd.slidingpanel.slidinguppanel.SlidingUpPanelActivity;
import com.ryd.slidingpanel.xpanel.XpanelActivity;

public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        findViewById(R.id.sliding_up_panel_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this, SlidingUpPanelActivity.class));
            }
        });

        findViewById(R.id.hover_view_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this, HoverViewActivity.class));
            }
        });

        findViewById(R.id.xpanel_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this, XpanelActivity.class));
            }
        });
    }

}
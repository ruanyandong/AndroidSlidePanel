package com.ryd.slidingpanel.hoverview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ryd.slidingpanel.R;
import com.ryd.slidingpanel.hoverview.lib.HoverView;
import com.ryd.slidingpanel.hoverview.lib.ViewState;
import java.util.Arrays;

public class HoverViewActivity extends AppCompatActivity {

    private Button mBtn;
    private HoverView mHv;
    private RecyclerView mRv;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hover_view);

        mBtn = findViewById(R.id.btn);
        mHv = findViewById(R.id.hv);
        mRv = findViewById(R.id.rv);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHv.getState() == ViewState.CLOSE)  // "关闭" 状态
                    mHv.changeState(ViewState.HOVER);   // 打开至 "悬停" 状态
//                    mHv.changeState(ViewState.FILL);   // 打开至 "全屏" 状态
                else
                    mHv.changeState(ViewState.CLOSE);   // 切换至 "关闭" 状态
            }
        });

        mRv.setAdapter(mAdapter = new MyAdapter(this, this));
        mRv.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.setDataList(Arrays.asList(
                "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20"
        ));
    }

}

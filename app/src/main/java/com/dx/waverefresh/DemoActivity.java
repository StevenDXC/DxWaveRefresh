package com.dx.waverefresh;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.dx.waverefresh.lib.WaveRefreshLayout;
import com.dx.waverefresh.widget.RefreshScrollView;


public class DemoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        final RefreshScrollView scrollView = (RefreshScrollView) findViewById(R.id.scrollView);
        scrollView.setOnStartRefreshingListener(new RefreshScrollView.OnStartRefreshingListener() {
            @Override
            public void startRefreshing() {
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.stopLoading();
                    }
                },3000);
            }
        });

        WaveRefreshLayout layout = (WaveRefreshLayout)findViewById(R.id.contentLayout);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setImageResource(R.drawable.share);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /**
         * calculate & set the top margin of float action button
         */
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)fab.getLayoutParams();
        int fabSize = (int)(49*getResources().getDisplayMetrics().density);
        int top = (int)layout.getGradientTop(params.rightMargin + fabSize/2)-fabSize/2;
        layout.setPaddingTop(top);

        /**
         * scale the fab with gesture,hide the fab when loading
         */
        scrollView.setAnimatableView(fab);
    }

}

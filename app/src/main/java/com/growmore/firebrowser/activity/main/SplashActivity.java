package com.growmore.firebrowser.activity.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.application.App;
import com.growmore.firebrowser.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends BaseActivity {
    @BindView(R.id.lvStart) LinearLayout lvStart;

    private long showStartTime;
    private final static long DELAY_TIME = 3000;
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        lvStart.setVisibility(View.GONE);
        if (App.readIsFirst()){
            lvStart.setVisibility(View.VISIBLE);
        }else {
            startSplash();
        }
    }

    @OnClick(R.id.btStart)
    public void onClickStart(){
        goToMain();
    }

    @OnClick(R.id.btSkip)
    public void onClickSkip(){
        goToMain();
    }

    private void startSplash() {
        showStartTime = System.currentTimeMillis();
        isRunning = true;

        Thread background = new Thread() {
            public void run(){
                try{
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - showStartTime < DELAY_TIME) {
                        try {
                            Thread.sleep(showStartTime + DELAY_TIME - currentTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            doFinish();
                        }
                    });
                }
            }
        };
        background.start();
    }

    private void doFinish(){
        if(this.isRunning){
            try {
                goToMain();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(this.isRunning)
            this.isRunning = false;
        finish();
    }

    private void goToMain(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
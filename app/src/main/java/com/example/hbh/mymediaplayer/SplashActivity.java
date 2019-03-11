package com.example.hbh.mymediaplayer;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;

public class SplashActivity extends AppCompatActivity {

    private boolean firstStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);  //因为继承了AppCompatActivity，这种方法无效

        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startApp();
            }
        }, 1000);

    }

    private void startApp() {
        if (firstStart){
            firstStart = false;
            Intent intent = new Intent(SplashActivity.this,MediaSearchActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startApp();
        return super.onTouchEvent(event);
    }
}

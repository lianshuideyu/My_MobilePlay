package com.atguigu.my_mobileplay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

public class WelcomeActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        },2000);
    }

    private void startMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainMobilePlayActivity.class);
        startActivity(intent);
        //打开主页面，然后关闭当前页面
        finish();
    }

    //触摸及跳转到主页面
    @Override
    public boolean onTouchEvent(MotionEvent event) {
       super.onTouchEvent(event);
        startMainActivity();
        handler.removeCallbacksAndMessages(null);

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacksAndMessages(null);
    }
}

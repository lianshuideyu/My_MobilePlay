package com.atguigu.my_mobileplay.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.my_mobileplay.R;

public class AudioPlayerActivity extends AppCompatActivity {
    private ImageView iv_icon;
    private TextView tv_artist;
    private TextView tv_audioname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        iv_icon = (ImageView)findViewById(R.id.iv_icon);
        //设置背景
        iv_icon.setBackgroundResource(R.drawable.animation_bg);
        AnimationDrawable background = (AnimationDrawable) iv_icon.getBackground();
        //开始播放动画
        background.start();
    }
}

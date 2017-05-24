package com.atguigu.my_mobileplay.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.atguigu.my_mobileplay.IMusicPlayService;
import com.atguigu.my_mobileplay.R;
import com.atguigu.my_mobileplay.service.MusicPlayService;

public class AudioPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout rlTop;
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvAudioname;
    private LinearLayout llBottom;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnPlaymode;
    private Button btnPre;
    private Button btnStartPause;
    private Button btnNext;
    private Button btnLyric;
    //这个就是IMusicPlayService.Stub的实例
    private IMusicPlayService service;
    private int position;
    //链接好服务后的回调
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = IMusicPlayService.Stub.asInterface(iBinder);
            if(service != null) {
                try {
                    service.openAudio(position);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void findViews() {
        setContentView(R.layout.activity_audio_player);
        ivIcon = (ImageView)findViewById(R.id.iv_icon);
        //设置背景
        ivIcon.setBackgroundResource(R.drawable.animation_bg);
        AnimationDrawable background = (AnimationDrawable) ivIcon.getBackground();
        //开始播放动画
        background.start();

        //初始化控件
        rlTop = (RelativeLayout)findViewById( R.id.rl_top );
        tvArtist = (TextView)findViewById( R.id.tv_artist );
        tvAudioname = (TextView)findViewById( R.id.tv_audioname );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvTime = (TextView)findViewById( R.id.tv_time );
        seekbarAudio = (SeekBar)findViewById( R.id.seekbar_audio );
        btnPlaymode = (Button)findViewById( R.id.btn_playmode );
        btnPre = (Button)findViewById( R.id.btn_pre );
        btnStartPause = (Button)findViewById( R.id.btn_start_pause );
        btnNext = (Button)findViewById( R.id.btn_next );
        btnLyric = (Button)findViewById( R.id.btn_lyric );

        btnPlaymode.setOnClickListener( this );
        btnPre.setOnClickListener( this );
        btnStartPause.setOnClickListener( this );
        btnNext.setOnClickListener( this );
        btnLyric.setOnClickListener( this );
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViews();

        getData();

        startAndBindService();
    }

    private void startAndBindService() {
        Intent intent = new Intent(this, MusicPlayService.class);
        //绑定得到服务的操作对象--IMusicPlayService---service
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
        //防止多次实例化service
        startService(intent);
        Log.e("TAG","AudioPlayerActivity---------startService");
    }

    private void getData() {
        position = getIntent().getIntExtra("position", 0);
    }

    @Override
    public void onClick(View v) {
        if ( v == btnPlaymode ) {

        } else if ( v == btnPre ) {

        } else if ( v == btnStartPause ) {
            try {
                if(service.isPlaying()) {
                    //暂停
                    service.pause();
                    //按钮状态
                    btnStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                }else {
                    //播放
                    service.start();
                    //按钮专题-暂停
                    btnStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } else if ( v == btnNext ) {

        } else if ( v == btnLyric ) {
            
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(conn != null) {
            unbindService(conn);
            conn = null;//释放资源
        }

    }
}

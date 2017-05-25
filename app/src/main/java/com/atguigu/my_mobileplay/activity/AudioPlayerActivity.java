package com.atguigu.my_mobileplay.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import com.atguigu.my_mobileplay.utils.Utils;

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

    private MyReceiver receiver;
    private Utils utils;
    private final  static  int PROGRESS = 0;
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
        initData();

        findViews();

        getData();

        startAndBindService();
    }

    private void initData() {
        //注册广播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayService.OPEN_COMPLETE);
        registerReceiver(receiver,intentFilter);

        utils = new Utils();
    }

    private class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //主线程
            setViewData();
        }
    }

    private void setViewData() {
        try {
            tvArtist.setText(service.getArtistName());
            tvAudioname.setText(service.getAudioName());
            int duration = service.getDurtion();
            seekbarAudio.setMax(duration);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        //发消息
        handler.sendEmptyMessage(PROGRESS);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case PROGRESS:
                    try {
                        int currentPosition = service.getCurrentPosition();
                        seekbarAudio.setProgress(currentPosition);

                        //设置更新时间
                        tvTime.setText(utils.stringForTime(currentPosition)+"/"+utils.stringForTime(service.getDurtion()));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    //每秒中更新一次
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS,1000);

                    break;
            }
        }
    };

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

        if(conn != null) {
            unbindService(conn);
            conn = null;//释放资源
        }

        //广播取消注册
        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }


}

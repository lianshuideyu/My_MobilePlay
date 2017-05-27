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
import com.atguigu.my_mobileplay.domain.Lyric;
import com.atguigu.my_mobileplay.domain.MediaItem;
import com.atguigu.my_mobileplay.service.MusicPlayService;
import com.atguigu.my_mobileplay.utils.LyricsUtils;
import com.atguigu.my_mobileplay.utils.Utils;
import com.atguigu.my_mobileplay.view.LyricShowView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

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
    private static final int PROGRESS = 0;

    private boolean notification;

    //显示歌词
    private static final int SHOW_LYRIC = 1;
    private LyricShowView lyric_show_view;

    //链接好服务后的回调
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = IMusicPlayService.Stub.asInterface(iBinder);
            if(service != null) {
                try {
                    if(notification) {
                        //什么也不用做,但需从新加载一下数据
                        setViewData(null);//加载的还是当前服务所携带的信息

                    }else {
                        //如果是从通知栏点击跳转来的那么position默认为0；
                        service.openAudio(position);

                        //链接好服务时就可以设置这两个属性
                        tvArtist.setText(service.getArtistName());
                        tvAudioname.setText(service.getAudioName());
                    }

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

        lyric_show_view = (LyricShowView)findViewById(R.id.lyric_show_view);

        btnPlaymode.setOnClickListener( this );
        btnPre.setOnClickListener( this );
        btnStartPause.setOnClickListener( this );
        btnNext.setOnClickListener( this );
        btnLyric.setOnClickListener( this );

        //设置监听拖动音频
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
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

        //注册EventBus
        EventBus.getDefault().register(this);
    }

    private class MyReceiver extends BroadcastReceiver{
        /**
         * 接收到广播时回调该方法
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            //主线程
            setViewData(null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setViewData(MediaItem mediaItem) {
        try {
            setButtonImage();
            tvArtist.setText(service.getArtistName());
            tvAudioname.setText(service.getAudioName());

            int duration = service.getDurtion();
            seekbarAudio.setMax(duration);

            /**
             * 解析歌词
             */
            //1.得到歌词所在路径
            String audioPath = service.getAudioPath();//mnt/sdcard/audio/beijingbeijing.mp3

            String lyricPath = audioPath.substring(0,audioPath.lastIndexOf("."));//mnt/sdcard/audio/beijingbeijing
            File file = new File(lyricPath+".lrc");
            if(!file.exists()){
                file = new File(lyricPath+".txt");
            }
            LyricsUtils lyricsUtils = new LyricsUtils();
            lyricsUtils.readFile(file);

            //2.传入解析歌词的工具类
            ArrayList<Lyric> lyrics = lyricsUtils.getLyrics();
            lyric_show_view.setLyrics(lyrics);

            //3.如果有歌词，就歌词同步

            if(lyricsUtils.isLyric()){
                handler.sendEmptyMessage(SHOW_LYRIC);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        //发消息
        handler.sendEmptyMessage(PROGRESS);
        //发消息更新歌词
        handler.sendEmptyMessage(SHOW_LYRIC);

    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case SHOW_LYRIC:
                    try {
                        int currentPosition = service.getCurrentPosition();
                        //调用歌词显示控件的setNextShowLyric
                        lyric_show_view.setNextShowLyric(currentPosition);


                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    removeMessages(SHOW_LYRIC);
                    sendEmptyMessage(SHOW_LYRIC);

                    break;

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
        //进来默认为false
        notification = getIntent().getBooleanExtra("notification", false);
        if(!notification) {
            //如果不是从通知栏来的，则走这里,默认是从本地音频列表进来的
            position = getIntent().getIntExtra("position", 0);
        }

    }

    @Override
    public void onClick(View v) {
        if ( v == btnPlaymode ) {

            setPlayMode();
        } else if ( v == btnPre ) {
            try {
                service.pre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

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
            try {
                service.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } else if ( v == btnLyric ) {
            
        }
    }

    private void setPlayMode() {

        try {
            int playmode = service.getPlaymode();
            if(playmode == MusicPlayService.REPEAT_NORMAL) {
                playmode = MusicPlayService.REPEAT_SINGLE;
            }else if (playmode == MusicPlayService.REPEAT_SINGLE) {
                playmode = MusicPlayService.REPEAT_ALL;
            } else if (playmode == MusicPlayService.REPEAT_ALL) {
                playmode = MusicPlayService.REPEAT_NORMAL;
            }
            //将播放模式保存到服务中
            service.setPlaymode(playmode);
            //设置默认按键背景
            setButtonImage();
            
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setButtonImage() {
        try {
            //从服务的到播放模式
            int playmode = service.getPlaymode();
            if (playmode == MusicPlayService.REPEAT_NORMAL) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_normal_selector);
            } else if (playmode == MusicPlayService.REPEAT_SINGLE) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_single_selector);
            } else if (playmode == MusicPlayService.REPEAT_ALL) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_all_selector);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
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

        //取消注册EventBus
        EventBus.getDefault().unregister(this);

        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        super.onDestroy();
    }


    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}

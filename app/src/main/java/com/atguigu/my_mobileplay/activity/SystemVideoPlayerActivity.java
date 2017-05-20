package com.atguigu.my_mobileplay.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.atguigu.my_mobileplay.R;
import com.atguigu.my_mobileplay.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SystemVideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private VideoView vv;
    private Uri uri;

    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnPre;
    private Button btnStartPause;
    private Button btnNext;
    private Button btnSwitchScreen;

    private static final int PROGRESS = 0;
    private Utils utils;
    //广播
    private MyBroadCastReceiver receiver;
    private int batteryView;

    private void findViews() {
        setContentView(R.layout.activity_system_video_player);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnSwitchPlayer = (Button) findViewById(R.id.btn_switch_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnPre = (Button) findViewById(R.id.btn_pre);
        btnStartPause = (Button) findViewById(R.id.btn_start_pause);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnSwitchScreen = (Button) findViewById(R.id.btn_switch_screen);
        vv = (VideoView) findViewById(R.id.vv);

        btnVoice.setOnClickListener(this);
        btnSwitchPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSwitchScreen.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        //初始化控件
        vv = (VideoView) findViewById(R.id.vv);
        findViews();
        utils = new Utils();

        uri = getIntent().getData();
        initData();

        //设置三个监听
        //当准备好播放时候调用
        setListener();

        //设置seekbar状态的监听
        seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             *
             * @param seekBar
             * @param progress
             * @param fromUser true:用户拖动改变的，false:系统更新改变的
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //如果拖动了
                if (fromUser) {
                    vv.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initData() {
        //注册监听电量的广播
        receiver = new MyBroadCastReceiver();
        //过滤器,
        IntentFilter intentFilter = new IntentFilter();
        //监听电量变化,对谁感兴趣就过滤谁
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        //注册广播，不要忘了解注册
        registerReceiver(receiver, intentFilter);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    //得到当前进度
                    int currentPosition = vv.getCurrentPosition();
                    //让seekBar进度更新
                    seekbarVideo.setProgress(currentPosition);

                    //设置文本当 前的播放进入
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    //得到系统时间
                    tvSystemTime.setText(getSystemTime());

                    //循环发消息
                    sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }

        }
    };

    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());

    }

    private void setListener() {
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //得到视频的总时长
                int duration = vv.getDuration();
                seekbarVideo.setMax(duration);
                //设置文本的总时间
                tvDuration.setText(utils.stringForTime(duration));

                vv.start();//开始播放

                //发消息更新进度
                handler.sendEmptyMessage(PROGRESS);
            }
        });
        //当播放出错的时候调用
        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Toast.makeText(SystemVideoPlayerActivity.this, "播放错误", Toast.LENGTH_SHORT).show();

                return false;
            }
        });
        //设置播放完成的监听
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(SystemVideoPlayerActivity.this, "播放完成", Toast.LENGTH_SHORT).show();
                finish();//播放完成退出
            }
        });
        //设置播放地址
        vv.setVideoURI(uri);
        //设置控制面板
        //vv.setMediaController(new MediaController(this));
    }

    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
        } else if (v == btnSwitchPlayer) {

        } else if (v == btnExit) {

        } else if (v == btnPre) {

        } else if (v == btnStartPause) {
            if (vv.isPlaying()) {
                vv.pause();//暂停
                btnStartPause.setBackgroundResource(R.drawable.btn_start_selector);

            } else {
                //播放
                vv.start();
                btnStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
            }

        } else if (v == btnNext) {

        } else if (v == btnSwitchScreen) {

        }
    }



    private void setBatteryView(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);//主线程,得到电量
            Log.e("TAG", "level==" + level);
            setBatteryView(level);
        }
    }


    @Override
    protected void onDestroy() {
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

        //取消注册
        if(receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        super.onDestroy();

    }
}

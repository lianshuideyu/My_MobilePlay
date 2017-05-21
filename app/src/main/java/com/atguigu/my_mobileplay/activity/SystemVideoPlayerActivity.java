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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.atguigu.my_mobileplay.R;
import com.atguigu.my_mobileplay.domain.MediaItem;
import com.atguigu.my_mobileplay.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private ArrayList<MediaItem> mediaItems;
    private int position;//视频列表的位置
    //隐藏控制面板的码
    private static final int HIDE_MEDIACONTROLLER = 1;
    //手势识别器
    private GestureDetector detector;

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

        //uri = getIntent().getData();
        initData();

        getData();

        //设置三个监听
        //当准备好播放时候调用
        setListener();

        setData();

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
                handler.removeMessages(HIDE_MEDIACONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,3000);
            }
        });
    }

    private void getData() {
        //得到播放地址
        uri = getIntent().getData();
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position",0);
    }

    private void setData() {
        if(mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getData());
            vv.setVideoPath(mediaItem.getData());

        }else if(uri != null) {
            //当只有一个播放内容的时候
            vv.setVideoURI(uri);
        }
        
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

        //手势识别器
        detector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){

            @Override
            public void onLongPress(MotionEvent e) {
                Toast.makeText(SystemVideoPlayerActivity.this, "长按了", Toast.LENGTH_SHORT).show();

                setStartOrPause();
                super.onLongPress(e);

            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Toast.makeText(SystemVideoPlayerActivity.this, "双击了", Toast.LENGTH_SHORT).show();

                return super.onDoubleTap(e);
            }

            /**
             * 单击屏幕
             * @param e
             * @return
             */
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(isShowMediaController) {
                    hideMediaController();
                    handler.removeMessages(HIDE_MEDIACONTROLLER);
                }else  {
                    showMediaController();
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,3000);
                }

                return super.onSingleTapConfirmed(e);
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把事件交给手势识别器
        detector.onTouchEvent(event);

        return super.onTouchEvent(event);

    }

    /**
     * 是否显示控制面板
     */
    private boolean isShowMediaController;

    //隐藏控制面板
    private void hideMediaController() {
        llBottom.setVisibility(View.INVISIBLE);
        llTop.setVisibility(View.INVISIBLE);
        isShowMediaController = false;

    }

    public void showMediaController() {
        llBottom.setVisibility(View.VISIBLE);
        llTop.setVisibility(View.VISIBLE);
        isShowMediaController = true;
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
                case HIDE_MEDIACONTROLLER:
                    //隐藏控制面板
                    hideMediaController();
                    break;

            }
            //默认隐藏控制面板
            hideMediaController();
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
//                Toast.makeText(SystemVideoPlayerActivity.this, "播放完成", Toast.LENGTH_SHORT).show();
//                finish();//播放完成退出
                setNextVideo();
            }
        });
        //设置播放地址
        //vv.setVideoURI(uri);
        //设置控制面板
        //vv.setMediaController(new MediaController(this));
    }

    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
        } else if (v == btnSwitchPlayer) {

        } else if (v == btnExit) {
            finish();
        } else if (v == btnPre) {
            setPreVideo();
        } else if (v == btnStartPause) {
            setStartOrPause();

        } else if (v == btnNext) {
            setNextVideo();
        } else if (v == btnSwitchScreen) {

        }

        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,3000);

    }

    private void setStartOrPause() {
        if(vv.isPlaying()) {
            //暂停
            vv.pause();
            btnStartPause.setBackgroundResource(R.drawable.btn_start_selector);
        }else {
            vv.start();
            btnStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
        }
    }

    private void setButtonStatus() {
        if(mediaItems != null && mediaItems.size() > 0) {
            //有视频播放
            setEnable(true);
            if(position == 0) {
                //上一个不可点击
                btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnPre.setEnabled(false);
            }
            
            if(position == mediaItems.size() - 1) {

                btnNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnNext.setEnabled(false);
            }
        }else if(uri != null) {
            //上一个和下一个不可用点击
            setEnable(false);
        }


    }

    private void setEnable(boolean b) {
        if(b) {
            //上一个和下一个都可以点击
            btnPre.setBackgroundResource(R.drawable.btn_pre_selector);
            btnNext.setBackgroundResource(R.drawable.btn_next_selector);
        }else {
            //上一个和下一个灰色，并且不可用点击
            btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnNext.setBackgroundResource(R.drawable.btn_next_gray);

        }

        btnPre.setEnabled(b);
        btnNext.setEnabled(b);
    }

    private void setPreVideo() {
        position--;
        if(position >= 0) {
            //还是在列表范围内
            MediaItem mediaItem = mediaItems.get(position);
            vv.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());

            //设置按键状态
            setButtonStatus();
        }
    }

    private void setNextVideo() {
        position++;
        if(position < mediaItems.size()) {
            //还是在列表范围内
            MediaItem mediaItem = mediaItems.get(position);
            vv.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());

            //设置按键状态
            setButtonStatus();

        }else {
            //播放最后一个
            Toast.makeText(SystemVideoPlayerActivity.this, "退出播放器", Toast.LENGTH_SHORT).show();
            finish();
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

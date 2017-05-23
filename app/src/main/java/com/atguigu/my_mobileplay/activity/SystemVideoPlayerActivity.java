package com.atguigu.my_mobileplay.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.my_mobileplay.R;
import com.atguigu.my_mobileplay.domain.MediaItem;
import com.atguigu.my_mobileplay.utils.Utils;
import com.atguigu.my_mobileplay.view.VideoView;

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


    //设置视频的默认尺寸
    private static final  int DEFUALT_SCREEN = 0;
    //全屏视频尺寸
    private static final int FULL_SCREEN = 1;
    //是否全屏
    private boolean isFullScreen = false;
    //屏幕的高
    private int screenHeight;
    private int screenWidth;
    //视频原生的宽和高
    private int videoWidth;
    private int videoHeight;

    //当前的音量：0~15之间
    private int currentVoice;
    //音频管理
    private AudioManager am;
    private int maxVoice;
    //是否静音
    private boolean isMute = false;

    //private SeekBar sb_test;

    /**
     * 判断是否为网络资源
     */
    private boolean isNetUri = true;
    //缓冲的速度文本和进度
    private LinearLayout ll_buffering;
    private TextView tv_net_speed;

    //显示网速
    private static final int SHOW_NET_SPEED = 2;
    private LinearLayout ll_loading;
    private TextView tv_loading_net_speed;

    private TextView brightnessTextView;

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

        ll_buffering = (LinearLayout) findViewById(R.id.ll_buffering);
        tv_net_speed = (TextView) findViewById(R.id.tv_net_speed);
        ll_loading = (LinearLayout)findViewById(R.id.ll_loading);
        tv_loading_net_speed = (TextView)findViewById(R.id.tv_loading_net_speed);
        brightnessTextView = (TextView)findViewById(R.id.brightnessTextView);

        //----------
        //sb_test = (SeekBar) findViewById(R.id.sb_test);
        //------

        btnVoice.setOnClickListener(this);
        btnSwitchPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSwitchScreen.setOnClickListener(this);

        //关联最大音量
        seekbarVoice.setMax(maxVoice);
        //设置当前进度
        seekbarVoice.setProgress(currentVoice);

        //发消息开始显示网速
        handler.sendEmptyMessage(SHOW_NET_SPEED);
    }

    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            isMute = !isMute;
            updateVoice(isMute);

        } else if (v == btnSwitchPlayer) {
            switchPlayer();

        } else if (v == btnExit) {
            finish();
        } else if (v == btnPre) {
            setPreVideo();
        } else if (v == btnStartPause) {
            setStartOrPause();

        } else if (v == btnNext) {
            setNextVideo();
        } else if (v == btnSwitchScreen) {
            if(isFullScreen) {
                //默认
                setVideoType(DEFUALT_SCREEN);
            }else {
                //全屏
                setVideoType(FULL_SCREEN);
            }

        }

        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);

    }

    private void switchPlayer() {
        new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("当前使用系统播放器播放，当播放有声音没有画面，请切换到万能播放器播放")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startVitamioPlayer();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        
    }

    private void updateVoice(boolean isMute) {
        if(isMute) {
            //静音,---设置音量
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            seekbarVoice.setProgress(0);
        }else {
            //非静音
            am.setStreamVolume(AudioManager.STREAM_MUSIC,currentVoice,0);
            seekbarVoice.setProgress(currentVoice);
        }
    }

    private int preCurrentPosition;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_NET_SPEED:
                    if(isNetUri) {
                        String netSpeed = utils.getNetSpeed(SystemVideoPlayerActivity.this);
                        tv_loading_net_speed.setText("正在加载中...."+netSpeed);
                        tv_net_speed.setText("正在缓冲...."+netSpeed);
                        sendEmptyMessageDelayed(SHOW_NET_SPEED,1000);

                    }
                    break;

                case PROGRESS:
                    //得到当前进度
                    int currentPosition = vv.getCurrentPosition();
                    //让seekBar进度更新
                    seekbarVideo.setProgress(currentPosition);

                    //设置文本当 前的播放进入
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    //得到系统时间
                    tvSystemTime.setText(getSystemTime());
                    
                    //设置视频缓存效果
                    if(isNetUri) {
                        int bufferPercentage = vv.getBufferPercentage();//缓存百分比
                        //得到当前的缓存量
                        int totalBuffer = bufferPercentage * seekbarVideo.getMax();
                        //缓存的进度条Progress
                        int secondaryProgress = totalBuffer / 100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);

                        //tvName.setText(uri.toString());

                    }else {
                        seekbarVideo.setSecondaryProgress(0);
                    }

                    /*if(isNetUri && vv.isPlaying()) {
                        int duration = currentPosition - preCurrentPosition;
                        if(duration < 500) {
                            //卡顿
                            ll_buffering.setVisibility(View.VISIBLE);
                        }else {
                            //不卡
                            ll_buffering.setVisibility(View.GONE);

                        }
                        //不要忘记重新赋值
                        preCurrentPosition = currentPosition;
                    }*/

                    //循环发消息
                    sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
                case HIDE_MEDIACONTROLLER:
                    //隐藏控制面板
                    hideMediaController();
                    break;

            }
            //默认隐藏控制面板
            //hideMediaController();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        findViews();

        //uri = getIntent().getData();

        getData();

        //设置三个监听
        //当准备好播放时候调用
        setListener();

        setData();


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

            //缓存
            isNetUri = utils.isNetUri(mediaItem.getData());

        }else if(uri != null) {
            //当只有一个播放内容的时候
            vv.setVideoURI(uri);
            isNetUri = utils.isNetUri(uri.toString());
        }
        
    }

    private void initData() {

        utils = new Utils();
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
                if(isFullScreen) {
                    //默认
                    setVideoType(DEFUALT_SCREEN);
                } else {
                    //全屏
                    setVideoType(FULL_SCREEN);
                }

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
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                }

                return super.onSingleTapConfirmed(e);
            }
        });


        //得到屏幕的宽高
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        //初始化声音相关
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }


    private float touchRange;//可移动的最大距离
    private float downY ;//手指按下时Y轴坐标
    private int currVol;//当前的音量
    //用于区别左右屏
    private float downX;

    //currentVoice当前音量
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把事件交给手势识别器
        detector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                downX = event.getX();
                downY = event.getY();
                touchRange = Math.min(screenWidth,screenHeight);//得到收滑动的范围
                currVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);//得到当前的音量

                handler.removeMessages(HIDE_MEDIACONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE :
                float newY = event.getY();//来到新的坐标
                float distanceY = downY - newY;//移动的距离
                //Math.abs(distanceY)/maxY;滑动距离的百分比
                //将要改变的音"量";要改变的声音 = (滑动的距离 / 总距离)*最大音量

                if(downX > screenWidth /2) {
                    //处理音量变化
                    float newVoice = distanceY / touchRange * maxVoice;
                    //得到最终要改变为的音量
                    float lastVoice = Math.min(Math.max(newVoice + currVol, 0), maxVoice);

                    if(newVoice != 0) {
                        updateVoiceProgress((int) lastVoice);
                    }
                }else if(downX < screenWidth / 2) {
                    brightnessTextView.setVisibility(View.VISIBLE);
                    //处理屏幕亮度调节
                    final double FLING_MIN_DISTANCE = 0.5;
                    final double FLING_MIN_VELOCITY = 0.5;
                    if (distanceY > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                        setBrightness(10);
                    }
                    if (distanceY < FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                        setBrightness(-10);
                    }
                    Log.e("TAG","进入屏幕亮度调节");
                    //Toast.makeText(SystemVideoPlayerActivity.this, "进入屏幕亮度调节", Toast.LENGTH_SHORT).show();

                }
                break;
            case MotionEvent.ACTION_UP :
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                brightnessTextView.setVisibility(View.INVISIBLE);

                break;
        }
        return super.onTouchEvent(event);
    }

    /*
* 设置屏幕亮度
* 0 最暗
* 1 最亮
*/

    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0.1) {
            lp.screenBrightness = (float) 0.1;
        }
        getWindow().setAttributes(lp);
        float sb = lp.screenBrightness;
        brightnessTextView.setText((int) Math.ceil(sb * 100) + "%");
    }

    //监听手机音量加减按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //音量减
            currentVoice--;
            updateVoiceProgress(currentVoice);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);

            return true;//目的:不要用系统的音量条，用自己
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //音量加
            currentVoice++;
            updateVoiceProgress(currentVoice);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);

            return true;
        }


        return super.onKeyDown(keyCode, event);
    }

    /**
     * 是否显示控制面板
     */
    private boolean isShowMediaController = false;

    //隐藏控制面板
    private void hideMediaController() {
        llBottom.setVisibility(View.INVISIBLE);
        llTop.setVisibility(View.GONE);
        isShowMediaController = false;

    }

    public void showMediaController() {
        llBottom.setVisibility(View.VISIBLE);
        llTop.setVisibility(View.VISIBLE);
        isShowMediaController = true;
    }




    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());

    }

    private void setListener() {
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //得到视频本身的宽和高
                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();

                //得到视频的总时长
                int duration = vv.getDuration();
                seekbarVideo.setMax(duration);
                //设置文本的总时间
                tvDuration.setText(utils.stringForTime(duration));

                vv.start();//开始播放

                //发消息更新进度
                handler.sendEmptyMessage(PROGRESS);

                //隐藏加载效果画面
                ll_loading.setVisibility(View.GONE);

                //默认隐藏
                //hideMediaController();
                //设默认屏幕
                setVideoType(DEFUALT_SCREEN);
                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mediaPlayer) {
                        Toast.makeText(SystemVideoPlayerActivity.this, "拖动完成", Toast.LENGTH_SHORT).show();
                    }
                });

                if(vv.isPlaying()){
                    //设置暂停
                    btnStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
                }else {
                    btnStartPause.setBackgroundResource(R.drawable.btn_start_selector);
                }
            }
        });
        //当播放出错的时候调用
        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Toast.makeText(SystemVideoPlayerActivity.this, "播放错误将切换到万能播放器", Toast.LENGTH_SHORT).show();
                startVitamioPlayer();


                return true;
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
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
            }
        });

        //监听拖动声音
        seekbarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             *
             * @param seekBar
             * @param progress
             * @param fromUser true:用户拖动改变的，false:系统更新改变的
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    updateVoiceProgress(progress);
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

    //打开万能播放器
    private void startVitamioPlayer() {
        if(vv != null) {
            vv.stopPlayback();//当前的播放器停止
        }
        Intent intent = new Intent(this, VitamioVideoPlayerActivity.class);
        if(mediaItems != null && mediaItems.size() > 0) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtra("position",position);

            intent.putExtras(bundle);
        }else if(uri != null) {
            intent.setData(uri);
        }
        startActivity(intent);
        finish();//关闭当前页面
    }

    private void updateVoiceProgress(int progress) {
        currentVoice = progress;
        //真正改变视频声音
        am.setStreamVolume(AudioManager.STREAM_MUSIC,currentVoice,0);
        //改变进度条
        seekbarVoice.setProgress(currentVoice);
        if(currentVoice <= 0) {
            isMute = true;
        }else {
            isMute = false;
        }
    }


    /**
     * 设置视频的全屏和默认
     * @param videoType
     */
    private void setVideoType(int videoType) {
        switch (videoType) {
            case  FULL_SCREEN:
                isFullScreen = true;
                //按钮状态--默认
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_default_selector);
                //设置视频的尺寸为全屏显示
                vv.setVideoSize(screenWidth,screenHeight);

                break;
            case DEFUALT_SCREEN:
                isFullScreen = false;
                //按钮状态--全屏
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_full_selector);
                //视频原生的宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;
                //计算好要显示的宽和高
                int width = screenWidth;
                int height = screenHeight;
                //需要等比例的缩放，mVideoWidth/mVideoHeight == width/height，这才是等比例
                //先判断，哪种方式的面积小，以哪种为基准
                if(mVideoWidth * height < mVideoHeight * width) {
                    //height不变
                    width = mVideoWidth / mVideoHeight * height;
                }else if(mVideoWidth * height > mVideoHeight * width) {
                    //width不变
                    height = mVideoHeight / mVideoWidth * width;
                }

                vv.setVideoSize(width,height);
                break;
        }

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
            //判断是否为网络资源
            isNetUri = utils.isNetUri(mediaItem.getData());
            ll_loading.setVisibility(View.VISIBLE);

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
            //判断是否为网络资源
            isNetUri = utils.isNetUri(mediaItem.getData());
            ll_loading.setVisibility(View.VISIBLE);

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

    class MyBroadCastReceiver extends BroadcastReceiver {

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

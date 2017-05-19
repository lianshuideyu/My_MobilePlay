package com.atguigu.my_mobileplay.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.atguigu.my_mobileplay.R;

public class SystemVideoPlayerActivity extends AppCompatActivity {
    private VideoView vv;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);

        vv = (VideoView)findViewById(R.id.vv);

        uri = getIntent().getData();

        //设置三个监听
        //当准备好播放时候调用
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                vv.start();//开始播放
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
        vv.setMediaController(new MediaController(this));
    }
}

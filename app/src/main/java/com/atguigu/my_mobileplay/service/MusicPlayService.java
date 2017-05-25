package com.atguigu.my_mobileplay.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.atguigu.my_mobileplay.IMusicPlayService;
import com.atguigu.my_mobileplay.R;
import com.atguigu.my_mobileplay.activity.AudioPlayerActivity;
import com.atguigu.my_mobileplay.domain.MediaItem;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/24.
 */

public class MusicPlayService extends Service {

    //aidl文件生成的类
    private IMusicPlayService.Stub stub = new IMusicPlayService.Stub() {
        //拿到外部的引用，就可以调用MusicPlayService的方法了
        MusicPlayService service = MusicPlayService.this;

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public String getArtistName() throws RemoteException {
            return service.getArtistName();
        }

        @Override
        public String getAudioName() throws RemoteException {
            return service.getAudioName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public int getDurtion() throws RemoteException {
            return service.getDurtion();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mediaPlayer.isPlaying();
        }

        @Override
        public int getPlaymode() throws RemoteException {
            return service.getPlaymode();
        }

        @Override
        public void setPlaymode(int playmode) throws RemoteException {
            service.setPlaymode(playmode);
        }
    };


    private ArrayList<MediaItem> mediaItems;

    private MediaPlayer mediaPlayer;
    //音频列表的下标位置
    private int position;
    //一个音频的信息类
    private MediaItem mediaItem;

    public static final String OPEN_COMPLETE = "com.atguigu.mobileplayer.OPEN_COMPLETE";
    //通知栏管理对象
    private NotificationManager nm;


    /**
     * 顺序播放
     */
    public static final int REPEAT_NORMAL = 1;

    /**
     * 单曲循环播放
     */
    public static final int REPEAT_SINGLE = 2;

    /**
     * 全部循环播放
     */
    public static final int REPEAT_ALL = 3;

    /**
     * 播放模式
     */
    private int playmode = REPEAT_NORMAL;
    /**
     * true:正常播放完成
     * false:人为手动点击下一个
     */
    private boolean isCompletion = false;
    private SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        //最初被创建的时候
        //在这里可以接收数据
        sp = getSharedPreferences("atguigu",MODE_PRIVATE);
        //每次进来读取存储的模式
        playmode = sp.getInt("playmode",getPlaymode());

        getData();

        Log.e("TAG", "MusicPlayService--onCreate()" );
    }

    private void getData() {
        new Thread() {
            public void run() {
                mediaItems = new ArrayList<MediaItem>();
                //得到解析对象
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                //通过解析者进行数据查询
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频的名称
                        MediaStore.Audio.Media.DURATION,//时长
                        MediaStore.Audio.Media.SIZE,//文件大小
                        MediaStore.Audio.Media.DATA,//视频播放地址
                        MediaStore.Audio.Media.ARTIST//歌手名
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        //添加一个判断太小的音频过滤掉
                        if (duration > 10 * 1000) {
                            mediaItems.add(new MediaItem(name, duration, size, data, artist));
                        }
                        Log.e("TAG", "name==" + name + ",duration==" + duration + ",data===" + data + ",artist==" + artist);

                    }

                    //关闭指针
                    cursor.close();
                }

            }
        }.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    /**
     * 得到播放模式
     * @return
     */
    public int getPlaymode() {
        return playmode;
    }

    /**
     * 设置播放模式
     * @param playmode
     */
    public void setPlaymode(int playmode) {
        this.playmode = playmode;

        sp.edit().putInt("playmode",playmode).commit();
    }

    //根据文职打开音频
    private void openAudio(int position) {
        this.position = position;

        if (mediaItems != null && mediaItems.size() > 0) {

            if (position < mediaItems.size()) {
                mediaItem = mediaItems.get(position);

                if (mediaPlayer != null) {
                    //如果不为空释放之前的播放的资源
                    mediaPlayer.reset();
                    mediaPlayer = null;
                }
                try {

                    mediaPlayer = new MediaPlayer();
                    //设置播放地址
                    mediaPlayer.setDataSource(mediaItem.getData());

                    //设置最基本的三个监听：准备完成，播放出错，播放完成
                    mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                    mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                    mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());

                    //准备,异步的准备,(它也有同步的准备方法，当联网是异步更好)
                    //视频播放时也需要调用准备方法，只不过videoView已经封装好
                    mediaPlayer.prepareAsync();

                    if(playmode== MusicPlayService.REPEAT_SINGLE){
                        isCompletion = false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            Toast.makeText(MusicPlayService.this, "音频还没有加载完成", Toast.LENGTH_SHORT).show();
        }
    }

    //开始播放
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void start() {
        mediaPlayer.start();
        //开始播放时开启通知栏效果
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra("notification",true);//是否来自状态栏

        PendingIntent pi = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notifation = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("音乐汇")
                .setContentText("正在播放："+getAudioName())
                .setContentIntent(pi)
                .build();
        nm.notify(1,notifation);
    }

    //暂停
    private void pause() {
        mediaPlayer.pause();
        //暂停时取消通知，取消通知栏
        nm.cancel(1);
    }

    //得到歌手名字
    private String getArtistName() {
        return mediaItem.getArtist();
    }

    //得到歌曲名字
    private String getAudioName() {
        return mediaItem.getName();
    }

    //得到播放路径
    private String getAudioPath() {
        return "";
    }

    //得到总时长
    private int getDurtion() {
        return mediaPlayer.getDuration();
    }

    //得到当前播放进度
    private int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    //音频拖动
    private void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    //下一个
    private void next() {
        //根据不同的模式设置不同的下标
        setNextPosition();

        //根据不同的下标位置打开对应的音频并且播放，边界处理
        openNextPosition();
    }


    //上一个
    private void pre() {
        //根据不同的模式设置不同的下标
        setPrePosition();

        //根据不同的下标位置打开对应的音频并且播放，边界处理
        openPrePosition();
    }

    private void openPrePosition() {
        int playmode = getPlaymode();
        if(playmode == MusicPlayService.REPEAT_NORMAL) {
            if(position >= 0 ) {
                //合法范围
                openAudio(position);
            }else {
                //大于数据总数
                position = 0;//最后一首
            }
        }else if(playmode == MusicPlayService.REPEAT_SINGLE) {
            //单曲循环
            if(position >= 0) {
                //合法范围
                openAudio(position);
            }else {
                //大于数据总数
                position = 0;//最后一首
            }
        }else if(playmode == MusicPlayService.REPEAT_ALL) {
            //无限循环
            openAudio(position);
        }
    }

    private void setPrePosition() {
        int playmode = getPlaymode();
        if(playmode == MusicPlayService.REPEAT_NORMAL) {
            //顺序播放，播放至最后一首停止
            //此处还为处理边界
            position--;
        }else if(playmode == MusicPlayService.REPEAT_SINGLE) {
            //单曲循环
            if(!isCompletion) {
                position--;//如果是人为点击的上一曲
            }
        }else if(playmode == MusicPlayService.REPEAT_ALL) {
            //无限循环
            position--;
            if(position == -1) {
                position = mediaItems.size() - 1;
            }
        }
    }

    private void openNextPosition() {
        int playmode = getPlaymode();
        if(playmode == MusicPlayService.REPEAT_NORMAL) {
            if(position < mediaItems.size()) {
                //合法范围
                openAudio(position);
            }else {
                //大于数据总数
                position = mediaItems.size() - 1;//最后一首
            }
        }else if(playmode == MusicPlayService.REPEAT_SINGLE) {
            //单曲循环
            if(position < mediaItems.size()) {
                //合法范围
                openAudio(position);
            }else {
                //大于数据总数
                position = mediaItems.size() - 1;//最后一首
            }
        }else if(playmode == MusicPlayService.REPEAT_ALL) {
            //无限循环
            openAudio(position);
        }

    }

    private void setNextPosition() {
        int playmode = getPlaymode();
        if(playmode == MusicPlayService.REPEAT_NORMAL) {
            //顺序播放，播放至最后一首停止
            //此处还为处理边界
            position++;
        }else if(playmode == MusicPlayService.REPEAT_SINGLE) {
            //单曲循环
            if(!isCompletion) {
                position++;//如果是人为点击的下一曲
            }
        }else if(playmode == MusicPlayService.REPEAT_ALL) {
            //无限循环
            position++;
            if(position == mediaItems.size()) {
                position = 0;
            }
        }


    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            //发广播
            notifyChange(OPEN_COMPLETE);

            start();
            Log.e("TAG","openAudio===position"+ position);
        }
    }

    /**
     * 发送广播
     * @param action
     */
    private void notifyChange(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            next();//播放下一个

            return true;
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            //如果是正常播完
            isCompletion = true;

            next();
        }
    }
}

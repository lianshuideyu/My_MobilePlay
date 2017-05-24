// IMusicPlayService.aidl
package com.atguigu.my_mobileplay;

// Declare any non-default types here with import statements

interface IMusicPlayService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);


            //根据文职打开音频
                 void openAudio(int position);

                //开始播放
                 void start();
                //暂停
                 void pause();

                //得到歌手名字
                 String getArtistName();

                //得到歌曲名字
                 String getAudioName();

                //得到播放路径
                 String getAudioPath();

                //得到总时长
                 int getDurtion();

                //得到当前播放进度
                 int getCurrentPosition();

                //音频拖动
                 void seekTo(int position);

                //上一个
                 void next();

                //下一个
                 void pre();
                  //是否在播放
                 boolean isPlaying();

}

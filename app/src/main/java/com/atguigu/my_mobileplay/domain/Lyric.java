package com.atguigu.my_mobileplay.domain;

/**
 * Created by Administrator on 2017/5/26.
 */

/**
 * 每行歌词都是一个对象
 */
public class Lyric {
    //该行歌词的内容
    private String content;

    //该行歌词的时间戳
    private long timePoint;

    //高亮持续的时间，上句时间戳-当前时间戳
    private long sleepTime;
    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", sleepTime=" + sleepTime +
                '}';
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }




}

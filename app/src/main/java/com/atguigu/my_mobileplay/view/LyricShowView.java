package com.atguigu.my_mobileplay.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.atguigu.my_mobileplay.domain.Lyric;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/26.
 */

public class LyricShowView extends TextView {
    private Paint paintGreen;
    private Paint paintWhite;
    private int width;
    private int height;
    private ArrayList<Lyric> lyrics;
    /**
     * 表示歌词在列表中的哪一句
     */
    private int index = 0;//默认第0句
    private float textHeight = 60;
    private int currentPosition;

    public LyricShowView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;
    }

    /**
     * 添加数据
     */
    private void initView() {
        paintGreen = new Paint();
        //设置画笔颜色
        paintGreen.setColor(Color.GREEN);
        //抗锯齿
        paintGreen.setAntiAlias(true);
        paintGreen.setTextSize(50);
        //设置居中
        paintGreen.setTextAlign(Paint.Align.CENTER);
        /**
         * 白色画笔
         */
        paintWhite = new Paint();
        //设置画笔颜色
        paintWhite.setColor(Color.WHITE);
        //抗锯齿
        paintWhite.setAntiAlias(true);
        paintWhite.setTextSize(40);
        //设置居中
        paintWhite.setTextAlign(Paint.Align.CENTER);

        //准备歌词
//        lyrics = new ArrayList<>();
//        Lyric lyric = new Lyric();
//        //先做假歌词模拟效果
//        for (int i = 0; i < 1000; i++) {
//            //不同的歌词
//            lyric.setContent("aaaaaaaaaaaaaaaaaa_" + i);
//            lyric.setSleepTime(2000);//高亮持续的时间
//            lyric.setTimePoint(2000*i);
//            //添加到集合
//            lyrics.add(lyric);
//            //重新创建新对象
//            lyric = new Lyric();
//
//        }
    }

    /**
     * 绘制歌词
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(lyrics != null && lyrics.size() > 0) {
            //当歌词的高亮行大于歌词的总行数的时候
            if(index > lyrics.size()) {
                //当歌词的高亮行大于歌词的总行数的时候直接return;避免越界
                return;
            }

            //有歌词
            //当前句--在列表中
            String currentContent = lyrics.get(index).getContent();
            //画出当前句
            canvas.drawText(currentContent,width/2,height/2,paintGreen);

            //得到中间句的Y轴坐标
            float tempY = height/2;

            //绘制中间句前面的部分
            for(int i = index - 1; i >= 0; i--) {

                //得到前一部分的歌词内容
                String preContent = lyrics.get(i).getContent();

                tempY = tempY - textHeight;
                if(tempY < 0) {
                    break;
                }
                //绘制内容
                canvas.drawText(preContent,width/2,tempY,paintWhite);
            }

            //在这里一定要将tempY重新赋值为屏幕中间的坐标
            tempY = height/2;

            //绘制中间句后面的部分
            for(int i = index + 1; i < lyrics.size(); i++) {
                //得到后一部分的歌词内容
                String nextContent = lyrics.get(i).getContent();

                tempY = tempY + textHeight;
                if(tempY > height) {
                    break;
                }
                //绘制内容
                canvas.drawText(nextContent,width/2,tempY,paintWhite);

            }

        }else {
            canvas.drawText("没有找到歌词...",width/2,height/2,paintGreen);
        }

    }


    /**
     * 根据播放的位置查找或者计算出当前该高亮显示的是哪一句
     * 并且得到这一句对应的相关信息
     *
     * @param currentPosition
     */
    public void setNextShowLyric(int currentPosition) {
        this.currentPosition = currentPosition;
        if(lyrics == null || lyrics.size() == 0) {
            return;
        }
        
        for(int i = 1; i < lyrics.size(); i++) {
            
            if(currentPosition < lyrics.get(i).getTimePoint()) {
                int temIndex = i - 1;
                if(currentPosition >= lyrics.get(temIndex).getTimePoint()) {
                    //中间高亮显示的哪一句
                    index = temIndex;
                }
                
            }
            
          
        }
        //最后重新绘制
        invalidate();
    }

    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;

    }
}

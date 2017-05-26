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
    private Paint paint;
    private int width;
    private int height;
    private ArrayList<Lyric> lyrics;


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
        paint = new Paint();
        //设置画笔颜色
        paint.setColor(Color.GREEN);
        //抗锯齿
        paint.setAntiAlias(true);
        paint.setTextSize(25);
        //设置居中
        paint.setTextAlign(Paint.Align.CENTER);

        //准备歌词
        lyrics = new ArrayList<>();
        Lyric lyric = new Lyric();
        //先做假歌词模拟效果
        for (int i = 0; i < 1000; i++) {
            //不同的歌词
            lyric.setContent("aaaaaaaaaaaaaaaaaa_" + i);
            lyric.setSleepTime(2000);//高亮持续的时间
            lyric.setTimePoint(2000*i);
            //添加到集合
            lyrics.add(lyric);
            //重新创建新对象
            lyric = new Lyric();

        }
    }

    /**
     * 绘制歌词
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText("没有找到歌词...", width / 2, height / 2, paint);
    }
}

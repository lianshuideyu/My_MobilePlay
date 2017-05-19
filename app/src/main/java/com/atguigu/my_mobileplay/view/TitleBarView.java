package com.atguigu.my_mobileplay.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.my_mobileplay.R;

/**
 * Created by Administrator on 2017/5/19.
 */

public class TitleBarView extends LinearLayout implements View.OnClickListener {
    private final Context context;
    private TextView tv_sousuo;
    private RelativeLayout rl_game;
    private ImageView iv_record;

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     * 布局加载完成时调用该方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tv_sousuo = (TextView) getChildAt(1);
        rl_game = (RelativeLayout) getChildAt(2);
        iv_record = (ImageView) getChildAt(3);

        //设置点击事件
        tv_sousuo.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sousuo :
                Toast.makeText(context , "搜索栏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_game :
                Toast.makeText(context , "游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_record :
                Toast.makeText(context , "历史记录", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

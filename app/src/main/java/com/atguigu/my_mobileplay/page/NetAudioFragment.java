package com.atguigu.my_mobileplay.page;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.atguigu.my_mobileplay.fragment.BaseFragment;

/**
 * Created by Administrator on 2017/5/19.
 */

public class NetAudioFragment extends BaseFragment {
    private TextView textView;

    @Override
    public View initView() {
        Log.e("TAG","NetAudioPager-initView");
        textView = new TextView(context);
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);

        return textView;
    }

    @Override
    public void initData() {
        Log.e("TAG","NetAudioPager-initData");
        super.initData();

        textView.setText("网络歌曲");
    }
}

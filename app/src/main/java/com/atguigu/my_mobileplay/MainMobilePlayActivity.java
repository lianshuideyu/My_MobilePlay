package com.atguigu.my_mobileplay;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.atguigu.my_mobileplay.fragment.BaseFragment;
import com.atguigu.my_mobileplay.page.LocalAudioFragment;
import com.atguigu.my_mobileplay.page.LocalVideoFragment;
import com.atguigu.my_mobileplay.page.NetAudioFragment;
import com.atguigu.my_mobileplay.page.NetVideoFragment;

import java.util.ArrayList;
import java.util.List;

public class MainMobilePlayActivity extends AppCompatActivity {
    private RadioGroup rg_bottom;
    private List<BaseFragment> fragments;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mobile_play);

        rg_bottom = (RadioGroup)findViewById(R.id.rg_bottom);

        initDatas();

        initListener();
    }

    private void initListener() {
        rg_bottom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case  R.id.rb_local_video:
                        position = 0;
                        break;
                    case  R.id.rb_local_audio:
                        position = 1;
                        break;
                    case  R.id.rb_net_audio:
                        position = 2;
                        break;
                    case  R.id.rb_net_video:
                        position = 3;
                        break;
                }

                BaseFragment currFragment = fragments.get(position);//得到当前Fragment视图

                //1得到事务管理
                FragmentManager fm = getSupportFragmentManager();
                //2开启事务
                FragmentTransaction ft = fm.beginTransaction();
                //3替换
                ft.replace(R.id.fl_pageFragment,currFragment);
                //4提交
                ft.commit();
            }
        });
        
        //默认勾选本地视频
        rg_bottom.check(R.id.rb_local_video);
    }

    private void initDatas() {
        fragments = new ArrayList<>();

        fragments.add(new LocalVideoFragment());
        fragments.add(new LocalAudioFragment());
        fragments.add(new NetAudioFragment());
        fragments.add(new NetVideoFragment());
    }
}

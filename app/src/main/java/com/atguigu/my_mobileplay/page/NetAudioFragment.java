package com.atguigu.my_mobileplay.page;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.my_mobileplay.R;
import com.atguigu.my_mobileplay.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/19.
 */

public class NetAudioFragment extends BaseFragment {


    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.progressbar)
    ProgressBar progressbar;
    @Bind(R.id.tv_nomedia)
    TextView tvNomedia;

    @Override
    public View initView() {
        Log.e("TAG", "NetAudioPager-initView");
        View view = View.inflate(context, R.layout.fragment_net_audio, null);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void initData() {
        Log.e("TAG", "NetAudioPager-initData");
        super.initData();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

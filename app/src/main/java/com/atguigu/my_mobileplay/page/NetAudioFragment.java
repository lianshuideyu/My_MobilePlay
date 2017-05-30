package com.atguigu.my_mobileplay.page;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.my_mobileplay.R;
import com.atguigu.my_mobileplay.adapter.NetAudioFragmentAdapter;
import com.atguigu.my_mobileplay.domain.NetAudioBean;
import com.atguigu.my_mobileplay.fragment.BaseFragment;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/19.
 */

public class NetAudioFragment extends BaseFragment {

    private String url = "http://s.budejie.com/topic/list/jingxuan/1/budejie-android-6.2.8/0-20.json?market=baidu&udid=863425026599592&appname=baisibudejie&os=4.2.2&client=android&visiting=&mac=98%3A6c%3Af5%3A4b%3A72%3A6d&ver=6.2.8";

    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.progressbar)
    ProgressBar progressbar;
    @Bind(R.id.tv_nomedia)
    TextView tvNomedia;

    private NetAudioFragmentAdapter myAdapter;

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

        getDataFromNet();
    }

    private void getDataFromNet() {
        RequestParams reques = new RequestParams(url);
        x.http().get(reques, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                LogUtil.e("onSuccess联网成功==" + result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("onError联网失败==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });

    }

    /**
     * 解析和显示数据
     *
     * @param json
     */
    private void processData(String json) {
        NetAudioBean netAudioBean = new Gson().fromJson(json, NetAudioBean.class);
        List<NetAudioBean.ListBean> datas = netAudioBean.getList();

        if(datas != null && datas.size() >0){
            //有数据
            tvNomedia.setVisibility(View.GONE);
            //设置适配器
            myAdapter = new NetAudioFragmentAdapter(context,datas);
            listview.setAdapter(myAdapter);
        }else{
            //没有数据
            tvNomedia.setVisibility(View.VISIBLE);
        }

        progressbar.setVisibility(View.GONE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

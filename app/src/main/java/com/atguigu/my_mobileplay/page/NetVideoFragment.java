package com.atguigu.my_mobileplay.page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.my_mobileplay.R;
import com.atguigu.my_mobileplay.activity.SystemVideoPlayerActivity;
import com.atguigu.my_mobileplay.adapter.NetVideoAdapter;
import com.atguigu.my_mobileplay.domain.MediaItem;
import com.atguigu.my_mobileplay.domain.MoveInfo;
import com.atguigu.my_mobileplay.fragment.BaseFragment;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/5/19.
 */

public class NetVideoFragment extends BaseFragment {

    private NetVideoAdapter adapter;
    private ListView lv;
    private TextView tv_nodata;

    private List<MoveInfo.TrailersBean> datas;

    private ArrayList<MediaItem> mediaItems;

    @Override
    public View initView() {
        Log.e("TAG", "NetVideoPager-initView");
        View view = View.inflate(context, R.layout.fragment_net_video_pager, null);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        lv = (ListView) view.findViewById(R.id.lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MoveInfo.TrailersBean item = adapter.getItem(position);
                Intent intent = new Intent(context, SystemVideoPlayerActivity.class);
//                intent.setDataAndType(Uri.parse(item.getUrl()), "video/*");
//                startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putSerializable("videolist",mediaItems);
                intent.putExtra("position",position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void initData() {
        Log.e("TAG", "NetVideoPager-initData");
        super.initData();
        getDataFromNet();

    }

    private void getDataFromNet() {
        //配置联网请求地址
        //配置联网请求地址
        final RequestParams request = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //联网得到数据
                Log.e("TAG", "xUtils联网成功==" + result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "xUtils联网失败==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    /**
     * 解析json数据和显示数据
     *
     * @param json
     */
    private void processData(String json) {
        MoveInfo moveInfo = new Gson().fromJson(json, MoveInfo.class);
        datas = moveInfo.getTrailers();

        //用于将此集合传入播放器
        mediaItems = new ArrayList<>();
        Iterator<MoveInfo.TrailersBean> iterator = datas.iterator();
        while (iterator.hasNext()){
            MoveInfo.TrailersBean next = iterator.next();
            mediaItems.add(new MediaItem(next.getMovieName(),next.getUrl()));

        }


        if (datas != null && datas.size() > 0) {
            tv_nodata.setVisibility(View.GONE);
            //有数据-适配器
            adapter = new NetVideoAdapter(context, datas);
            lv.setAdapter(adapter);
        } else {
            tv_nodata.setVisibility(View.VISIBLE);
        }
    }
}

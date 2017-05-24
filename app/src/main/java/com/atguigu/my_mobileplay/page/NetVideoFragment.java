package com.atguigu.my_mobileplay.page;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
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

    private MaterialRefreshLayout refresh;

    private boolean isLoadMore = false;

    //缓存
    private SharedPreferences sp;
    public static final String uri = "http://api.m.mtime.cn/PageSubArea/TrailerList.api";

    @Override
    public View initView() {
        Log.e("TAG", "NetVideoPager-initView");
        sp = context.getSharedPreferences("atguiguMediaItems", Context.MODE_PRIVATE);

        View view = View.inflate(context, R.layout.fragment_net_video_pager, null);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        lv = (ListView) view.findViewById(R.id.lv);
        refresh = (MaterialRefreshLayout) view.findViewById(R.id.refresh);

        //设置刷新
        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            //下拉刷新
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                isLoadMore = false;
                getDataFromNet();
            }

            //上拉刷新，加载更多数据
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);

                isLoadMore = true;
                getMoreDataFromNet();
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MoveInfo.TrailersBean item = adapter.getItem(position);
                Intent intent = new Intent(context, SystemVideoPlayerActivity.class);
//                intent.setDataAndType(Uri.parse(item.getUrl()), "video/*");
//                startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putSerializable("videolist", mediaItems);
                intent.putExtra("position", position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }

    private void getMoreDataFromNet() {
        //配置联网请求地址
        final RequestParams request = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //联网得到数据
                Log.e("TAG", "xUtils联网成功==" + result);
                refresh.finishRefreshLoadMore();
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

    @Override
    public void initData() {
        Log.e("TAG", "NetVideoPager-initData");
        super.initData();

        //直接先获取缓存数据，当联网成功后替代缓存的数据
        String saveJson = sp.getString(uri, "");
        if(!TextUtils.isEmpty(saveJson)) {
            //解析缓存的数据
            processData(saveJson);
            Log.e("TAG","解析缓存的数据=="+saveJson);
        }

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
                refresh.finishRefresh();//联网成功后刷新结束
                processData(result);

                //缓存
                if(result != null) {
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString(uri,result).commit();
                }
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

        if (!isLoadMore) {//下拉刷新
            datas = moveInfo.getTrailers();

            //用于将此集合传入播放器
            mediaItems = new ArrayList<>();
            Iterator<MoveInfo.TrailersBean> iterator = datas.iterator();
            while (iterator.hasNext()) {
                MoveInfo.TrailersBean next = iterator.next();
                mediaItems.add(new MediaItem(next.getMovieName(), next.getUrl()));

            }

            if (datas != null && datas.size() > 0) {
                tv_nodata.setVisibility(View.GONE);
                //有数据-适配器
                adapter = new NetVideoAdapter(context, datas);
                lv.setAdapter(adapter);
            } else {
                tv_nodata.setVisibility(View.VISIBLE);
            }
        } else {//上拉加载更多
            //加载更多得到的数据新数据
            List<MoveInfo.TrailersBean> trailersBeanList = moveInfo.getTrailers();
            //要传入播放器的
            for (int i = 0; i < trailersBeanList.size(); i++) {
                MediaItem mediaItem = new MediaItem(trailersBeanList.get(i).getMovieName(),
                        trailersBeanList.get(i).getUrl());
                mediaItems.add(mediaItem);

            }
            //加入到原来集合的数据
            datas.addAll(trailersBeanList);
            //刷新适配器
            adapter.notifyDataSetChanged();
        }
    }
}
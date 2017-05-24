package com.atguigu.my_mobileplay.page;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.my_mobileplay.R;
import com.atguigu.my_mobileplay.activity.AudioPlayerActivity;
import com.atguigu.my_mobileplay.adapter.LocalVideoAdapter;
import com.atguigu.my_mobileplay.domain.MediaItem;
import com.atguigu.my_mobileplay.fragment.BaseFragment;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/19.
 */

public class LocalAudioFragment extends BaseFragment {
    private ListView lv;
    private TextView tv_nodata;
    private ArrayList<MediaItem> mediaItems;
    private LocalVideoAdapter adapter;

    @Override
    public View initView() {
        Log.e("TAG","LocalAudioPager-initView");
        View view = View.inflate(context, R.layout.fragment_local_video_pager, null);
        lv = (ListView) view.findViewById(R.id.lv);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);

        //设置点击事件，点击开始播放视频
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MediaItem item = adapter.getItem(i);

                Intent intent = new Intent(context, AudioPlayerActivity.class);
//                intent.setDataAndType(Uri.parse(item.getData()),"video/*");
//                startActivity(intent);
                //传递视频列表过去
                //Bundle bundle = new Bundle();
                //bundle.putSerializable("videolist",mediaItems);
                intent.putExtra("position",i);
                //intent.putExtras(bundle);
                startActivity(intent);
                Log.e("TAG","LocalAudioFragment---------startAudioPlayerActivity");
            }
        });
        return view;
    }

    @Override
    public void initData() {
        Log.e("TAG","LocalVideoPager-initData");
        super.initData();

        //加载本地视频
        getData();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mediaItems != null && mediaItems.size() > 0) {
                tv_nodata.setVisibility(View.GONE);

                adapter = new LocalVideoAdapter(context,mediaItems,false);
                lv.setAdapter(adapter);
            }else {
                //没有数据
                tv_nodata.setVisibility(View.VISIBLE);
            }

        }
    };

    private void getData() {
        new Thread(){
            public void run(){
                mediaItems = new ArrayList<MediaItem>();
                //得到解析对象
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                //通过解析者进行数据查询
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频的名称
                        MediaStore.Audio.Media.DURATION,//时长
                        MediaStore.Audio.Media.SIZE,//文件大小
                        MediaStore.Audio.Media.DATA//视频播放地址
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if(cursor != null) {
                    while (cursor.moveToNext()){
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        //添加一个判断太小的音频过滤掉
                        if(duration > 10*1000) {
                            mediaItems.add(new MediaItem(name,duration,size,data));
                        }


                        //使用handler
                        handler.sendEmptyMessage(0);
                    }

                    //关闭指针
                    cursor.close();
                }


            }
        }.start();
    }
}

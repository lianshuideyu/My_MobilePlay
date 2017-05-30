package com.atguigu.my_mobileplay.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.atguigu.my_mobileplay.R;
import com.atguigu.my_mobileplay.domain.NetAudioBean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/30.
 */

public class NetAudioFragmentAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<NetAudioBean.ListBean> datas;

    /**
     * 视频
     */
    private static final int TYPE_VIDEO = 0;

    /**
     * 图片
     */
    private static final int TYPE_IMAGE = 1;

    /**
     * 文字
     */
    private static final int TYPE_TEXT = 2;

    /**
     * GIF图片
     */
    private static final int TYPE_GIF = 3;

    /**
     * 软件推广
     */
    private static final int TYPE_AD = 4;

    public NetAudioFragmentAdapter(Context context, List<NetAudioBean.ListBean> datas) {

        this.mContext = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * 根据位置去判断这条数据是什么类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        int itemViewType = -1;
        NetAudioBean.ListBean listBean = datas.get(position);
        //根据位置，从列表中得到一个数据对象
        String type = listBean.getType();
        if ("video".equals(type)) {
            itemViewType = TYPE_VIDEO;
        } else if ("image".equals(type)) {
            itemViewType = TYPE_IMAGE;
        } else if ("text".equals(type)) {
            itemViewType = TYPE_TEXT;
        } else if ("gif".equals(type)) {
            itemViewType = TYPE_GIF;
        } else {
            itemViewType = TYPE_AD;//广播
        }
        return itemViewType;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        convertView = initView(convertView, getItemViewType(position), datas.get(position));


        return convertView;
    }

    private View initView(View convertView, int itemViewType, NetAudioBean.ListBean listBean) {
        switch (itemViewType) {
            case TYPE_VIDEO:
                VideoHoder videoHoder;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.all_video_item, null);
                    videoHoder = new VideoHoder();
                    videoHoder.tv_video = (TextView) convertView.findViewById(R.id.tv_video);

                    convertView.setTag(videoHoder);
                } else {
                    videoHoder = (VideoHoder) convertView.getTag();
                }

                //设置数据
                videoHoder.tv_video.setText("视频");

                break;
            case TYPE_IMAGE:
                ImageHolder imageHolder;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.all_video_item, null);
                    imageHolder = new ImageHolder();
                    imageHolder.tv_video = (TextView) convertView.findViewById(R.id.tv_video);
                    convertView.setTag(imageHolder);
                } else {
                    imageHolder = (ImageHolder) convertView.getTag();
                }

                //设置数据
                imageHolder.tv_video.setText("img");
                break;
            case TYPE_TEXT:
                TextHolder textHolder;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.all_video_item, null);
                    textHolder = new TextHolder();
                    textHolder.tv_video = (TextView) convertView.findViewById(R.id.tv_video);
                    convertView.setTag(textHolder);
                } else {
                    textHolder = (TextHolder) convertView.getTag();
                }

                //设置数据
                textHolder.tv_video.setText("text");
                break;
            case TYPE_GIF:
                GifHolder gifHolder;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.all_video_item, null);
                    gifHolder = new GifHolder();
                    gifHolder.tv_video = (TextView) convertView.findViewById(R.id.tv_video);
                    convertView.setTag(gifHolder);
                } else {
                    gifHolder = (GifHolder) convertView.getTag();
                }

                //设置数据
                gifHolder.tv_video.setText("gif");

                break;
            case TYPE_AD:
                ADHolder adHolder;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.all_video_item, null);
                    adHolder = new ADHolder();
                    adHolder.tv_video = (TextView) convertView.findViewById(R.id.tv_video);
                    convertView.setTag(adHolder);
                } else {
                    adHolder = (ADHolder) convertView.getTag();
                }

                //设置数据
                adHolder.tv_video.setText("AD");

                break;
        }


        return convertView;
    }

    class VideoHoder {
        TextView tv_video;

    }

    class ImageHolder {
        TextView tv_video;
    }

    class TextHolder {
        TextView tv_video;

    }

    class GifHolder {
        TextView tv_video;
    }

    class ADHolder {
        TextView tv_video;
    }
}

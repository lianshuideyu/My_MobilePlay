package com.atguigu.my_mobileplay.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.my_mobileplay.R;
import com.atguigu.my_mobileplay.domain.MediaItem;
import com.atguigu.my_mobileplay.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/19.
 */

public class LocalVideoAdapter extends BaseAdapter {
    private final ArrayList<MediaItem> mediaItems;
    private final Context context;
    private final boolean isVideo;
    private Utils utils;

    public LocalVideoAdapter(Context context, ArrayList<MediaItem> mediaItems,boolean b) {
        this.context = context;
        this.mediaItems = mediaItems;
        this.isVideo = b;
        utils = new Utils();
    }


    @Override
    public int getCount() {
        return mediaItems == null ? 0 : mediaItems.size();
    }

    @Override
    public MediaItem getItem(int i) {
        return mediaItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_local_video, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //根据位置得到对应的数据
        MediaItem mediaItem = mediaItems.get(position);
        viewHolder.tv_name.setText(mediaItem.getName());
        viewHolder.tv_size.setText(Formatter.formatFileSize(context, mediaItem.getSize()));
        viewHolder.tv_duration.setText(utils.stringForTime((int) mediaItem.getDuration()));

        if(!isVideo) {
            //此时为本地音频
            viewHolder.iv_icon.setImageResource(R.drawable.music_default_bg);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
    }
}

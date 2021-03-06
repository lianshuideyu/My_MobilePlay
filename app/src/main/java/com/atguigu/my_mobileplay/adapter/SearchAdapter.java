package com.atguigu.my_mobileplay.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.my_mobileplay.R;
import com.atguigu.my_mobileplay.domain.SearchBean;
import com.atguigu.my_mobileplay.utils.Utils;
import com.squareup.picasso.Picasso;

import org.xutils.image.ImageOptions;

import java.util.List;

/**
 * Created by Administrator on 2017/5/22.
 */

public class SearchAdapter extends BaseAdapter {

    private final Context context;
    private List<SearchBean.ItemsBean> datas;
    private Utils utils;
    private ImageOptions imageOptions;

    public SearchAdapter(Context context, List<SearchBean.ItemsBean> datas) {
        this.context = context;
        this.datas = datas;

        utils = new Utils();

        imageOptions = new ImageOptions.Builder()
                .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setFailureDrawableId(R.drawable.video_default)
                .setLoadingDrawableId(R.drawable.video_default)
                .build();
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public SearchBean.ItemsBean getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_net_video, null);
            viewHolder = new ViewHolder();

            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        //根据位置得到对应的数据
        SearchBean.ItemsBean trailersBean = datas.get(position);
        viewHolder.tv_name.setText(trailersBean.getItemTitle());
        viewHolder.tv_size.setText(trailersBean.getDatecheck());
        viewHolder.tv_duration.setText(trailersBean.getKeywords());

        //xUtil请求图片
        //x.image().bind(viewHolder.iv_icon, trailersBean.getCoverImg(), imageOptions);

        //Picasso加载图片，Picasso自带三级缓存,当有图片的时候再请求不然后空，崩
        if(!(trailersBean.getItemImage().getImgUrl1().isEmpty())) {
            Picasso.with(context)
                    .load(trailersBean.getItemImage().getImgUrl1())
                    .placeholder(R.drawable.video_default)
                    .error(R.drawable.video_default)
                    .into(viewHolder.iv_icon);
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

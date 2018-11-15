package com.example.liangjiacheng.mobileplayer.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.liangjiacheng.mobileplayer.R;
import com.example.liangjiacheng.mobileplayer.domain.MediaItem;
import com.example.liangjiacheng.mobileplayer.utils.Utils;
import com.squareup.picasso.Picasso;

import org.xutils.x;

import java.util.ArrayList;

/**
 * create by liangjiacheng on 2018/10/26  15:41
 * 这个类的作用是：Net_VideoPager的适配器
 */
public class Net_VideoPagerAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<MediaItem> mediaItems;

    public Net_VideoPagerAdapter(Context context, ArrayList<MediaItem> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;

    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;//控件容器
        if (convertView == null) {
            /**添加视图布局*/
            convertView = View.inflate(context, R.layout.item_net_video_pager, null);
            viewHolder = new ViewHolder();//容器
            //绑定
            viewHolder.iv_icon = convertView.findViewById(R.id.iv_icon);//图片
            viewHolder.tv_name = convertView.findViewById(R.id.tv_name);//视频名称
            viewHolder.tev_desc = convertView.findViewById(R.id.tev_desc);//描述
            convertView.setTag(viewHolder);/**设置setTag的作用是什么？*/
        } else {
            //有setTag就有getTag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //根据position得到列表中对应位置的数据
        MediaItem mediaItem = mediaItems.get(position);
        viewHolder.tv_name.setText(mediaItem.getName());//得到网络视频的名称
        viewHolder.tev_desc.setText(mediaItem.getDesc());//得到网络视频的描述

//       1. 使用Xutils3请求图片
//        x.image().bind(viewHolder.iv_icon,mediaItem.getImageUrl());//得到网路视频的图片
//        2.使用Glide请求图片
        Glide.with(context).load(mediaItem.getImageUrl())//load（）方法得到地址
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.video_default)  //默认的图片
                        .error(R.drawable.video_default)) //出错的时候显示这张
                .into(viewHolder.iv_icon);//.into（）绑定到控件上
/**   上面的代码和视频的不太一样：  如果没有.apply(new RequestOptions()，
 * 那么 .diskCacheStrategy(DiskCacheStrategy.ALL) 这句将会报错 。
 *  .placeholder(R.drawable.btn_exit))这句也是通过百度加上去的 **/
//          3.使用Picasso请求图片
//        Picasso.with(context).load(mediaItem.getImageUrl())//load（）方法得到地址
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.video_default)  //默认的图片
//                .error(R.drawable.video_default) //出错的时候显示这张
//                .into(viewHolder.iv_icon);//.into（）绑定到控件上

        return convertView;
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tev_desc;

    }
}

package com.example.liangjiacheng.mobileplayer.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liangjiacheng.mobileplayer.R;
import com.example.liangjiacheng.mobileplayer.domain.MediaItem;
import com.example.liangjiacheng.mobileplayer.utils.Utils;

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
    public int getCount()  {
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
        x.image().bind(viewHolder.iv_icon,mediaItem.getImageUrl());//得到网路视频的图片

        return convertView;
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tev_desc;

    }
}

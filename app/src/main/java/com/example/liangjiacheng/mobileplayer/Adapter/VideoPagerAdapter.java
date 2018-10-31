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

import java.util.ArrayList;

/**
 * create by liangjiacheng on 2018/10/26  15:41
 * 这个类的作用是：VideoPager的适配器
 */
public class VideoPagerAdapter extends BaseAdapter {
    private Utils utils;
    private final Context context;
    private final ArrayList<MediaItem> mediaItems;

    public VideoPagerAdapter(Context context, ArrayList<MediaItem> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
        utils = new Utils();
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
            convertView = View.inflate(context, R.layout.item_video_pager, null);
            viewHolder = new ViewHolder();//容器
            //绑定
            viewHolder.iv_icon = convertView.findViewById(R.id.iv_icon);//图片
            viewHolder.tv_name = convertView.findViewById(R.id.tv_name);//视频名称
            viewHolder.tv_duration = convertView.findViewById(R.id.tv_duration);//时长
            viewHolder.tv_size = convertView.findViewById(R.id.tv_size);//大小
            convertView.setTag(viewHolder);/**设置setTag的作用是什么？*/
        } else {
            //有setTag就有getTag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //根据position得到列表中对应位置的数据
        MediaItem mediaItem = mediaItems.get(position);
        viewHolder.tv_name.setText(mediaItem.getName());
        viewHolder.tv_size.setText(android.text.format.Formatter.formatFileSize(context, mediaItem.getSize()));/**1android.text.format.Formatter.formatFileSize这个工具的作用是：在大于900转换成KB，如果kb大于900就转换成MB，以此类推*/
        viewHolder.tv_duration.setText(utils.stringForTime((int) mediaItem.getDuration()));
        return convertView;
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
    }
}

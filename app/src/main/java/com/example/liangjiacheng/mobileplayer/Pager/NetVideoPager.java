package com.example.liangjiacheng.mobileplayer.Pager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.liangjiacheng.mobileplayer.Base.BasePager;

/**
 * create by liangjiacheng on 2018/10/21  20:19
 * 这个类的作用是：网络视频的页面
 */
public class NetVideoPager extends BasePager {
    private TextView textView;

    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView  = new TextView(context);//父类的context
        return textView;
    }

    @Override
    public void initDate() {
        super.initDate();
        textView.setText("网络视频");
    }
}

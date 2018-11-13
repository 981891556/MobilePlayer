package com.example.liangjiacheng.mobileplayer;

import android.app.Application;

import org.xutils.x;

/**
 * create by liangjiacheng on 2018/11/13  15:22
 * 这个类的作用是：xutils的初始化
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }
}

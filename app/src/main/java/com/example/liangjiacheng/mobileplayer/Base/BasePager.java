package com.example.liangjiacheng.mobileplayer.Base;

import android.content.Context;
import android.view.View;

/**
 * create by liangjiacheng on 2018/10/21  20:12
 * 这个类的作用是：（公共类）基类---本地视频，本地音乐，网络音乐，网络视频的基类
 */
public abstract class BasePager {
    public  Context context;  //上下文
    public View rootView; //视图，各个子页面实例化的结果
    public boolean isInitDate  =  false; //判断数据是否初始化

    public  BasePager(Context context){
        this.context = context;
        rootView = initView();
        isInitDate = false;
    }

    public abstract View initView();//孩子实现方法，初始化视图
    public void initDate(){
        //当孩子需要初始化数据的时候，重写该方法，用于请求数据，或者显示数据
    }
}

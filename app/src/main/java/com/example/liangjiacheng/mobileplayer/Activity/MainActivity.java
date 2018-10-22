package com.example.liangjiacheng.mobileplayer.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.example.liangjiacheng.mobileplayer.Base.BasePager;
import com.example.liangjiacheng.mobileplayer.Fragment.ReplaceFragment;
import com.example.liangjiacheng.mobileplayer.Pager.LocalAudioPager;
import com.example.liangjiacheng.mobileplayer.Pager.LocalVideoPager;
import com.example.liangjiacheng.mobileplayer.Pager.NetAudioPager;
import com.example.liangjiacheng.mobileplayer.Pager.NetVideoPager;
import com.example.liangjiacheng.mobileplayer.R;

import java.util.ArrayList;

/**
 * create by liangjiacheng on 2018/10/21  18:51
 * 这个类的作用是：主页面
 */
public class MainActivity extends FragmentActivity  {
    //初始化activity_main布局的控件
    private RadioGroup rg_main;
    private ArrayList<BasePager> basePagers; //泛型结合
    private int Position;//页面的位置


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_main = findViewById(R.id.rg_main);

        basePagers = new ArrayList<>();
        //一般按顺序输入，创建实例
        basePagers.add(new LocalVideoPager(this));
        basePagers.add(new LocalAudioPager(this));
        basePagers.add(new NetAudioPager(this));
        basePagers.add(new NetVideoPager(this));

        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_main.check(R.id.loca_video);//默认选中
    }
    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                default://默认本地视频
                    Position = 0;
                    break;
                case R.id.loca_music:
                    Position = 1;
                    break;
                case R.id.net_music:
                    Position = 2;
                    break;
                case R.id.net_vedio:
                    Position = 3;
                    break;
            }
            setFragment();
        }
    }

    private void setFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_main,new ReplaceFragment(getBasePager()));/**解决方法详情：https://ask.csdn.net/questions/354752     */
        ft.commit();
    }

    public BasePager getBasePager() {
        BasePager basePager = basePagers.get(Position);
        if (basePager != null && !basePager.isInitDate) {//如果没有初始化数据
            basePager.isInitDate = true;
            basePager.initDate();//初始化数据
        }
        return basePager;
    }


}

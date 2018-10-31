package com.example.liangjiacheng.mobileplayer.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
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
    private FrameLayout fl_main_content;
    //初始化activity_main布局的控件
    private RadioGroup rg_main;

    //页面的结合
    private ArrayList<BasePager> basePagers; //泛型结合

    private int Position;//页面的位置


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_main = findViewById(R.id.rg_main);
        fl_main_content = findViewById(R.id.fl_main);

        basePagers = new ArrayList<>();
        //一般按顺序输入，创建实例
        basePagers.add(new LocalVideoPager(this));//添加页面  0
        basePagers.add(new LocalAudioPager(this));//添加页面  1
        basePagers.add(new NetAudioPager(this));//添加页面    2
        basePagers.add(new NetVideoPager(this));//添加页面    3

        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());//radiogroup组件
        rg_main.check(R.id.loca_video);/**  默认选中首页
         默认选中代替在布局中的android:checked="true"，如果布局中出现这个会导致进入程序不会自己扫描更新本地视频*/

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
        FragmentManager fm = getSupportFragmentManager(); //得到FragmentManager
        FragmentTransaction ft = fm.beginTransaction();//开启事务
        ft.replace(R.id.fl_main,new ReplaceFragment(getBasePager()));/**解决方法详情：https://ask.csdn.net/questions/354752     */
        ft.commit();//提交事务
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

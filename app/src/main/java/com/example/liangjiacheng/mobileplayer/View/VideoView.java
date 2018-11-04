package com.example.liangjiacheng.mobileplayer.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * create by liangjiacheng on 2018/11/3  19:45
 * 这个类的作用是：自定义的View控制视频播放的屏幕大小
 */
public class VideoView extends android.widget.VideoView {

    //在代码中创建的时候
    public VideoView(Context context) {
        this(context,null);
    }


    /**当这个类在布局文件的时候，系统通过构造方法实例化该类
     * 在布局文件中使用不能省略，否则崩溃
     * @param context
     * @param attrs
     */
    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**当需要设置样式的时候调用该方法
     * @param context
     * @param attrs
     * @param defStyleAttr  样式
     */
    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    /**设置视频的宽和高
     * @param videoWith 指定视频的宽
     * @param videoHeight 指定视频的高
     */
    public void setVideoSize(int videoWith,int videoHeight){
       ViewGroup.LayoutParams params = getLayoutParams();
        params.width = videoWith;
        params.height = videoHeight;
        setLayoutParams(params);
    }
}

package com.example.liangjiacheng.mobileplayer.utils;

import android.content.Context;
import android.net.TrafficStats;

import java.util.Formatter;
import java.util.Locale;

/**
 * create by liangjiacheng on 2018/10/24  9:11
 * 这个类的作用是：Utils：时间工具
 */
public class Utils {
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;


    private long lastTotalRxbytes = 0;
    private long lastTimeStamp = 0;

    public Utils() {
        //转换成字符串的时间
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    }

    /**
     * 把毫秒换成：1：20:30这里的形式
     *
     * @param
     * @return
     */

    public String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;

        int minutes = (totalSeconds / 60) % 60;

        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 判断是否为联网下载缓冲的
     *
     * @param uri
     * @return
     */

    public boolean isNetUri(String uri) {
        boolean result = false;
        if (uri != null) {
            //判断协议
            if (uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("rtsp") || uri.toLowerCase().startsWith("mms")) {
                result = true;
            }
        }
        return result;
    }

    //  百度找资料。集成到自己的项目，得到网速
    public String getNetSpeed(Context context) {
        String netSpeed = "0 kb/s";
        long nowTotalRxbytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转换成kb
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxbytes - lastTotalRxbytes) * 1000 / (nowTimeStamp - lastTimeStamp));//转成毫秒

        lastTimeStamp = nowTimeStamp;
        lastTotalRxbytes = nowTotalRxbytes;
        netSpeed = String.valueOf(speed) + "kb/s";
        return netSpeed;
    }

}

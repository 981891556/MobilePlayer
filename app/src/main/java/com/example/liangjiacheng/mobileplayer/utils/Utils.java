package com.example.liangjiacheng.mobileplayer.utils;
import java.util.Formatter;
import java.util.Locale;
/**
 * create by liangjiacheng on 2018/10/24  9:11
 * 这个类的作用是：Utils：时间工具
 */
public class Utils {
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
   public Utils(){
       //转换成字符串的时间
       mFormatBuilder = new StringBuilder();
       mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

   }

   /**把毫秒换成：1：20:30这里的形式
    * @param
    * @return
    * */

   public String stringForTime(int timeMs){
       int totalSeconds  = timeMs/1000;
       int seconds = totalSeconds % 60;

       int minutes = (totalSeconds/60) % 60 ;

       int hours = totalSeconds/3600;

       mFormatBuilder.setLength(0);
       if (hours>0){
           return  mFormatter.format("%d:%02d:%02d",hours,minutes,seconds).toString();
       }else {
           return mFormatter.format("%02d:%02d",minutes,seconds).toString();
       }
   }
}

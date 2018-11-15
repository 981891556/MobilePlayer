package com.example.liangjiacheng.mobileplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * create by liangjiacheng on 2018/11/15  21:13
 * 这个类的作用是：缓存工具类
 */
public class CacheUtils {


    /**保存数据
     * @param context
     * @param key
     * @param values
     */
    //
    public static void putString(Context context, String key, String values) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Date", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, values).commit();
    }

    /** //得到缓存数据
     * @param context 上下文
     * @param key  键
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Date", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }
}

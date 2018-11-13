package com.example.liangjiacheng.mobileplayer.Pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.liangjiacheng.mobileplayer.Activity.SystemViedoPlayer;
import com.example.liangjiacheng.mobileplayer.Adapter.Net_VideoPagerAdapter;
import com.example.liangjiacheng.mobileplayer.Adapter.VideoPagerAdapter;
import com.example.liangjiacheng.mobileplayer.Base.BasePager;
import com.example.liangjiacheng.mobileplayer.R;
import com.example.liangjiacheng.mobileplayer.domain.MediaItem;
import com.example.liangjiacheng.mobileplayer.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * create by liangjiacheng on 2018/10/21  20:19
 * 这个类的作用是：网络视频的页面
 */
public class NetVideoPager extends BasePager {
    /**
     * 绑定Id相当于findViewById*  ,但前提一定要有41行的代码，即：x.view().inject(this,view); 这行代码
     */
    @ViewInject(R.id.lv_video_pager)
    private ListView mListView;

    @ViewInject(R.id.tv_NoNet)
    private TextView mTv_nonet;

    @ViewInject(R.id.pb_loading)
    private ProgressBar mProgressBar;

    private ArrayList<MediaItem> mediaItems;//这个集合一定要装之前使用,数据集合

    private Net_VideoPagerAdapter adapter;

    public NetVideoPager(Context context) {
        super(context);
    }

    /**
     * 初始化控件，父类调用
     *
     * @return
     */
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.net_video_pager, null);//加载布局
        x.view().inject(NetVideoPager.this, view);//第一个参数不是上下文，而是NetVideoPager.this  。所以这里的this可以被NetVideoPager.this代替。  第二个参数布局（实例化的视图0）

        //点击播放视频
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaItem mediaItem = mediaItems.get(position);
                /**1.原来的代码     隐式*/
//                Intent intent  = new Intent();
//                intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");     其中*代表视频的各种格式
//                context.startActivity(intent);
                /**2.调用自己写的播放器代码   一个播放地址*/
//                Intent intent = new Intent(context, SystemViedoPlayer.class);/**调用自己定义的播放器*/
//                intent.setDataAndType(Uri.parse(mediaItem.getData()), "video/*");  // 其中*代表视频的各种格式
//                context.startActivity(intent);
                /**3.传递列表数据  如果是对象需要序列化  */
                Intent intent = new Intent(context, SystemViedoPlayer.class);/**调用自己定义的播放器*/
                Bundle bundle = new Bundle();
                bundle.putSerializable("videolist", mediaItems);
                intent.putExtras(bundle);
                intent.putExtra("position", position);
                context.startActivity(intent);

            }
        });

        return view;


    }


    @Override
    public void initDate() {
        super.initDate();
        /**********************联网*********************/
        //视屏内容
        RequestParams params = new RequestParams(Constants.NET_URL);//必须加上常量类的联网地址
        x.http().get(params, new Callback.CommonCallback<String>() {//后面的的这些方法都是联网请求得到的
            @Override
            public void onSuccess(String result) {
                //主线程
                processData(result);//解析数据（速度很快。所以没必要开一个子线程）
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }

    //解析数据并设置适配器
    private void processData(String json) {
        mediaItems = parseJson(json);//解析数据的方法
        //设置适配器
        // 设置适配器
        if (mediaItems != null && mediaItems.size() > 0) {
            //有数据

            adapter = new Net_VideoPagerAdapter(context, mediaItems);  //设置适配器，也就是显示内容
            mListView.setAdapter(adapter);
            //把文本隐藏起来
            mTv_nonet.setVisibility(View.GONE);
        } else {
            //没有数据
            //文本显示
            mTv_nonet.setVisibility(View.VISIBLE);
        }
        //不管有没有数据，ProgressBar都要隐藏
        mProgressBar.setVisibility(View.GONE);


    }


    /**
     * 解析Json数据有两种方式:
     * 1.用系统接口解析json数据
     * 2.使用第三方解析工具（Gson，fastjson）
     *
     * @param json
     * @return
     */
    private ArrayList<MediaItem> parseJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        /**开始解析*/
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");//trailers这个Key值是从json数据传过来的如果使用getJSONObject()代替optJSONArray的时候，当trailers这个Key不存在的时候，程序会崩溃
            if (jsonArray != null && jsonArray.length() > 0) {//判断是否为空，遍历
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                    if (jsonObjectItem != null) {
                        MediaItem mediaItem = new MediaItem();
                        //解析内容
                        String movieName = jsonObjectItem.optString("movieName");//视频名称   其中movieName为API接口的Key
                        mediaItem.setName(movieName);
                        String videoTitle = jsonObjectItem.optString("videoTitle");// 描述内容  其中videoTitle为API接口的Key
                        mediaItem.setDesc(videoTitle);
                        String imageUrl = jsonObjectItem.optString("coverImg");//图片的地址
                        mediaItem.setImageUrl(imageUrl);
                        String hightUrl = jsonObjectItem.optString("hightUrl");//播放地址
                        mediaItem.setData(hightUrl);

                        //把数据添加到集合
                        mediaItems.add(mediaItem);
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mediaItems;
    }
}

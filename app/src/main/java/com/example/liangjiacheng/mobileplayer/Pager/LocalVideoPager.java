package com.example.liangjiacheng.mobileplayer.Pager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.liangjiacheng.mobileplayer.Adapter.VideoPagerAdapter;
import com.example.liangjiacheng.mobileplayer.Base.BasePager;
import com.example.liangjiacheng.mobileplayer.R;
import com.example.liangjiacheng.mobileplayer.Activity.SystemViedoPlayer;
import com.example.liangjiacheng.mobileplayer.domain.MediaItem;

import java.util.ArrayList;

/**
 * create by liangjiacheng on 2018/10/21  20:19
 * 这个类的作用是：本地视频的页面
 */
public class LocalVideoPager extends BasePager {
    private ListView lv_video_pager;//电影列表
    private TextView tv_nomedia; //页面不存在这个TextView
    private ProgressBar pb_loading;//进度条圈圈

    private ArrayList<MediaItem> mediaItems;//这个集合一定要装之前使用,数据集合

    private VideoPagerAdapter videoPagerAdapter;

//    private Utils utils;


    public LocalVideoPager(Context context) {
        super(context);
//        utils = new Utils();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager, null);
        lv_video_pager = view.findViewById(R.id.lv_video_pager);
        tv_nomedia = view.findViewById(R.id.tv_nomedia);
        pb_loading = view.findViewById(R.id.pb_loading);

        //点击播放视频
        lv_video_pager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                bundle.putSerializable("videolist",mediaItems);
                intent.putExtras(bundle);
                intent.putExtra("position",position);
                context.startActivity(intent);

            }
        });
        return view;
    }

    @Override
    public void initDate() {
        super.initDate();
        getDataFromaLocal();
    }

    private Handler handler = new Handler() {  //使用Handler是因为在子线程中不能设置适配器
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //主线程
            // 设置适配器
            if (mediaItems != null && mediaItems.size() > 0) {
                //有数据
                //设置适配器，也就是显示内容
                videoPagerAdapter = new VideoPagerAdapter(context,mediaItems,true);//看Adapter的代码参数
                lv_video_pager.setAdapter(videoPagerAdapter);
                //把文本隐藏起来
                tv_nomedia.setVisibility(View.GONE);
            } else {
                //没有数据
                //文本显示
                tv_nomedia.setVisibility(View.VISIBLE);
            }
            //不管有没有数据，ProgressBar都要隐藏
             pb_loading.setVisibility(View.GONE);
        }
    };

    /**自定义适配器
     * 已经被抽取出来了，下面注释的都是被抽取的代码
     * */
//    class VideoPagerAdapter extends BaseAdapter {
//        @Override
//        public int getCount() {
//            return mediaItems.size();
//        }
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//             ViewHolder viewHolder;//控件容器
//            if (convertView == null) {
//                /**添加视图布局*/
//                convertView = View.inflate(context, R.layout.item_video_pager, null);
//                viewHolder = new ViewHolder();//容器
//                //绑定
//                viewHolder.iv_icon = convertView.findViewById(R.id.iv_icon);//图片
//                viewHolder.tv_name = convertView.findViewById(R.id.tv_name);//视频名称
//                viewHolder.tv_duration = convertView.findViewById(R.id.tv_duration);//时长
//                viewHolder.tv_size = convertView.findViewById(R.id.tv_size);//大小
//                convertView.setTag(viewHolder);/**设置setTag的作用是什么？*/
//            } else {
//                //有setTag就有getTag
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//            //根据position得到列表中对应位置的数据
//            MediaItem mediaItem = mediaItems.get(position);
//            viewHolder.tv_name.setText(mediaItem.getName());
//            viewHolder.tv_size.setText(android.text.format.Formatter.formatFileSize(context, mediaItem.getSize()));/**1android.text.format.Formatter.formatFileSize这个工具的作用是：在大于900转换成KB，如果kb大于900就转换成MB，以此类推*/
//            viewHolder.tv_duration.setText(utils.stringForTime((int) mediaItem.getDuration()));
//            return convertView;
//        }
//    }
//    static class ViewHolder {
//        ImageView iv_icon;
//        TextView tv_name;
//        TextView tv_duration;
//        TextView tv_size;
//    }

    /**
     * 从本地的sd卡中加载数据
     * 1.遍历sd卡，后缀名
     * 2.从内容提供者里面获取视频
     * 3.如果是6.0系统需要加载动态权限
     */
    private void getDataFromaLocal() {
        new Thread() {
            @Override
            public void run() {  //在子线程中不能设置适配器
                super.run();
                isGrantExternalRW((Activity) context);
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;//EXTERNAL_CONTENT_URI为外置存储（SD卡）
                String[] objects = {
                        MediaStore.Video.Media.DISPLAY_NAME,//在SD卡显示的名称
                        MediaStore.Video.Media.DURATION,//视频的长度
                        MediaStore.Video.Media.SIZE,//视频文件大小
                        MediaStore.Video.Media.DATA, //视频的绝对地址（SD卡的绝对地址）
                        MediaStore.Video.Media.ARTIST//歌曲的演唱者
                };
                Cursor cursor = contentResolver.query(uri, objects, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaItem = new MediaItem();
/**                       mediaItems.add(mediaItem);           写在这里和写在下面是一样的            */
                        String name = cursor.getString(0);
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);
                        mediaItem.setData(data);

                        String artist = cursor.getString(3);
                        mediaItem.setArtist(artist);

                        //把视频添加到列表中
                        mediaItems.add(mediaItem);
                    }
                    cursor.close();//为什么要关闭？
                }

                //发消息处理视图
                handler.sendEmptyMessage(10);//10可以是任意
            }
        }.start();
    }


       /**
        * 解决安卓6.0以上版本不能读取外部存储权限的问题
        * @param activity
        * @return
        */
       public static boolean isGrantExternalRW(Activity activity) {
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                   Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

               activity.requestPermissions(new String[]{
                       Manifest.permission.READ_EXTERNAL_STORAGE,
                       Manifest.permission.WRITE_EXTERNAL_STORAGE
               }, 1);
               return false;
           }
           return true;
       }
   }


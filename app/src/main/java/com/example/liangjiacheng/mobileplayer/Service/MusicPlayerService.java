package com.example.liangjiacheng.mobileplayer.Service;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.liangjiacheng.mobileplayer.IMusicPlayerService;
import com.example.liangjiacheng.mobileplayer.domain.MediaItem;
import com.example.liangjiacheng.mobileplayer.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;

import io.vov.vitamio.MediaPlayer;

/**
 * create by liangjiacheng on 2018/11/17  8:52
 * 这个类的作用是：创建服务类，用来播放音乐
 *   服务默认在主线程进行，所以new一个子线程
 */
public class MusicPlayerService extends Service {

    public static final String OPENAUDIO = "com.liangjiacheng.mobileplayer_OPENAUDIO";
    private ArrayList<MediaItem>mediaItems;//列表
    private int position;//播放位置
    private android.media.MediaPlayer mediaPlayer ;//用于播放音月

    private MediaItem mediaItem;//当前播放的音频文件的对象

    @Override
    public void onCreate() {
        super.onCreate();

        getDataFromLocal();  //加载音乐列表
    }

    /**加载音乐列表*/
    private void getDataFromLocal() {
        new Thread() {
            @Override
            public void run() {  //在子线程中不能设置适配器
                super.run();
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver contentResolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//EXTERNAL_CONTENT_URI为外置存储（SD卡）
                String[] objects = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//在SD卡显示的名称
                        MediaStore.Audio.Media.DURATION,//视频的长度
                        MediaStore.Audio.Media.SIZE,//视频文件大小
                        MediaStore.Audio.Media.DATA, //视频的绝对地址（SD卡的绝对地址）
                        MediaStore.Audio.Media.ARTIST//歌曲的演唱者
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
            }
        }.start();
    }

    private IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {
        MusicPlayerService service  = MusicPlayerService.this;/***内部类创建外部类实例调用外部类方法*/
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);

        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return  service.getName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public void setPlayMode(int playmode) throws RemoteException {
            service.setPlayMode(playmode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            mediaPlayer.seekTo(position);
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }








    /**
     * 根据位置打开对应的音频,并且播放
     */
    private void openAudio(int position){
        this.position = position;
        if (mediaItems!=null&mediaItems.size()>0){
             mediaItem = mediaItems.get(position);
             if (mediaPlayer!=null){
                 mediaPlayer.release();//释放
                 mediaPlayer.reset();
             }
            try {
                mediaPlayer = new android.media.MediaPlayer();
                //设置监听，播放出错，播放完成，准备好
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());//准备好
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener()); //播放完成
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());//播放出错
                mediaPlayer.setDataSource(mediaItem.getData());//设置播放地址
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(MusicPlayerService.this, "还没有数据", Toast.LENGTH_SHORT).show();
        }
    }

    //设置监听：准备好
    class MyOnPreparedListener implements android.media.MediaPlayer.OnPreparedListener{

        @Override
        public void onPrepared(android.media.MediaPlayer mp) {
            //通知Activity来获取信息--广播
            notifyChange(OPENAUDIO);
            start();
        }
    }

    /**根据动作发广播
     * @param action
     */
    private void notifyChange(String action) {
            Intent intent = new Intent(action);
            sendBroadcast(intent);
    }


    //播放完成
    class MyOnCompletionListener implements android.media.MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(android.media.MediaPlayer mp) {
            next();
        }
    }


    //播放出错的监听
    class MyOnErrorListener implements android.media.MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(android.media.MediaPlayer mp, int what, int extra) {
            LogUtil.e("播放出错"   +  what);
            next();//播放下一个
            return true;
        }
    }
    /**
     *播放音乐
     */
    private void start(){
            mediaPlayer.start();//播放
    }

    /**
     * 暂停播放
     */
    private  void pause(){
        mediaPlayer.pause();//暂停
    }

    /**
     * 停止播放
     */
    private void stop(){

    }

    /**得到当前播放进度
     * @return
     */
    private int getCurrentPosition(){

        return mediaPlayer.getCurrentPosition();
    }

    /**得到当前音频的总时长
     * @return
     */
    private int getDuration(){

        return mediaPlayer.getDuration();
    }

    /**得到播放歌曲的艺术家（演唱者）
     * @return
     */
    private String getArtist(){

        return mediaItem.getArtist();
    }

    /**得到播放歌曲的名称
     * @return
     */
    private String getName(){

        return mediaItem.getName();
    }


    /**得到播放歌曲的路径
     * @return
     */
    private String getAudioPath(){

        return "";
    }

    /**
     * 播放下一个视频
     */
    private void next(){

    }

    /**
     * 播放上一个视频
     */
    private void pre(){

    }

    /**设置播放模式
     * @param playmode
     */
    private void setPlayMode(int playmode){

    }

    /**得到播放模式
     * @return
     */
    private  int getPlayMode(){
        return 0;
    }

    /**是否在播放音频
     * @return
     */
    private boolean isPlaying(){
        return mediaPlayer.isPlaying();//这个对象的方法是已经封装好的方法，直接调用即可
    }
}

package com.example.liangjiacheng.mobileplayer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liangjiacheng.mobileplayer.R;

import com.example.liangjiacheng.mobileplayer.View.VitamioVideoView;
import com.example.liangjiacheng.mobileplayer.domain.MediaItem;
import com.example.liangjiacheng.mobileplayer.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

/**
 * create by liangjiacheng on 2018/10/25  10:22
 * 这个类的作用是：系统播放器
 */
public class VitamioVideoPlayer extends Activity implements View.OnClickListener {

    private boolean isUseSystem = true;//用户选择使用什么样的卡顿方式
    private int PrecurrentPosition;//上一次的播放进度


    private static final int PROGRESS = 1;//视频更新进度
    private static final int HIDE_MEDIA_CONTORLLER = 2;//隐藏控制面板
    private static final int FULL_SRCEEN = 1;//全屏
    private static final int DEFAULT_SRCEEN = 2;//默认屏幕
    private static final int SHOW_SPEED = 3;//显示网速
    /**
     * 视频进度的更新
     */
    private VitamioVideoView videoview;
    private Uri uri;
    private boolean isShowMediaContorller = false;//是否显示控制面板
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVoice;
    private SeekBar seekBarVoice;
    private Button btnSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekBarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSwitchSrceen;
    private RelativeLayout media_controller;
    private Utils utils;

    private TextView tv_buffer_netspeed;//网速
    private LinearLayout ll_buffer;//整个显示网速以及缓冲的布局

    private TextView tv_loading_netspeed;
    private LinearLayout ll_loading;

    //监听电量变化广播
    private MyReceiver receiver;

    private ArrayList<MediaItem> mediaItems; //传入进来的视频列表
    private int position;//要播放的列表中的具体位置

    /**
     * 1.定义手势识别器
     * 2.实例化手势识别器，并且重写双击，单击，长按等方法
     * 3.onTouchEvent方法把事件传递给手势识别器，否则不能执行手势识别器的相关操作
     */
    private GestureDetector detector;
    private boolean isFullScreen = false;  //是否全屏

    private int ScreenWidth = 0;//屏幕宽
    private int ScreenHeight = 0;//屏幕高
    private int videoWidth;//真实视频的宽
    private int videoHeight;

    private AudioManager am;//调节声音
    private int currenVoice;//当前音量
    private int MaxVoice; //最大音量 0-15
    private boolean isMute = false;//是否是静音
    private boolean isNetUri; //判断是否联网缓冲的数据


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-10-27 16:10:10 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        videoview = findViewById(R.id.videoview);
        media_controller = findViewById(R.id.media_controller);//控制面板整个布局
        llTop = (LinearLayout) findViewById(R.id.ll_top);//顶部布局
        tvName = (TextView) findViewById(R.id.tv_name);//视频名称
        ivBattery = (ImageView) findViewById(R.id.iv_battery);//电池
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);//系统时间
        btnVoice = (Button) findViewById(R.id.btn_voice);//声音
        seekBarVoice = (SeekBar) findViewById(R.id.seekBar_voice);//声音进度条
        btnSwitchPlayer = (Button) findViewById(R.id.btn_switch_player);//切换万能播放器的按钮
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);//底部布局
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);//当前时间
        seekBarVideo = (SeekBar) findViewById(R.id.seekBar_video);//视频播放进度
        tvDuration = (TextView) findViewById(R.id.tv_duration);//视频总时间
        btnExit = (Button) findViewById(R.id.btn_exit);//退出播放
        btnVideoPre = (Button) findViewById(R.id.btn_video_pre);//前一个视频
        btnVideoStartPause = (Button) findViewById(R.id.btn_video_start_pause);//暂停或开始
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);//下一个视频
        btnVideoSwitchSrceen = (Button) findViewById(R.id.btn_video_switch_srceen);//全屏显示或者默认显示

        tv_buffer_netspeed = findViewById(R.id.tv_buffer_netspeed);
        ll_buffer = findViewById(R.id.ll_buffer);

        tv_loading_netspeed = findViewById(R.id.tv_loading_netspeed);//加载中的显示网速的文本
        ll_loading = findViewById(R.id.ll_loading);

        btnVoice.setOnClickListener(this);
        btnSwitchPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSwitchSrceen.setOnClickListener(this);

        //关联
        seekBarVoice.setMax(MaxVoice);//设置音量的最大值
        seekBarVoice.setProgress(currenVoice);//当前音量

        //开始更新网速
        handler.sendEmptyMessage(SHOW_SPEED);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-10-27 16:10:10 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            isMute = !isMute;
            // Handle clicks for btnVoice
            updataVoice(currenVoice, isMute);
        } else if (v == btnSwitchPlayer) {
            showSwitchPlayerDialog();//调用系统播放器
            // Handle clicks for btnSwitchPlayer
        } else if (v == btnExit) {
            finish();
            // Handle clicks for btnExit
        } else if (v == btnVideoPre) {
            playPreVideo();
            // Handle clicks for btnVideoPre
        } else if (v == btnVideoStartPause) {
            StartAndPause();
            // Handle clicks for btnVideoStartPause
        } else if (v == btnVideoNext) {
            // Handle clicks for btnVideoNext
            playNextVideo();
        } else if (v == btnVideoSwitchSrceen) {
            // Handle clicks for btnVideoSwitchSrceen
            setFullScreenAndDefault();//设置播放屏幕大小
        }
        //先移除旧的消息，再设置新的消息，这样就保证了在点击的是时候，控制面板不会自己动隐藏
        handler.removeMessages(HIDE_MEDIA_CONTORLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTORLLER, 4000);
    }


    //切换播放器对话框
    private void showSwitchPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当您播放视频有一个视频，有花屏额时候，可以尝试使用系统播放器播放");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StartSystemPlayer();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();/**这一步容易忘记~！*/
    }

    //调用系统播放器
    private void StartSystemPlayer() {
        if (videoview!=null){
            videoview.stopPlayback();//停止播放并且释放资源
        }
        Intent intent = new Intent(this, SystemViedoPlayer.class);/**调用自己定义的播放器*/
        if (mediaItems != null && mediaItems.size() > 0) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
        } else if (uri != null) {
            intent.setData(uri);
        }
        startActivity(intent);
        finish();//关闭当前页面
    }


    private void StartAndPause() {
        if (videoview.isPlaying()) {//.isPlaying()是类包装的方法
            //视频在播放-设置暂停
            videoview.pause();//.pause()是类包装的方法

            /***按钮设置状态播放    开始或者播放这个按钮，在状态不同是时候设置不同的图片选择器*/
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        } else {
            //视频播放
            videoview.start();
            //按钮状态设置暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);//更改选择器
        }
    }

    /**
     * 播放上一个视频
     */
    private void playPreVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //播放上 一个
            position--;
            if (position >= 0) {

                ll_loading.setVisibility(View.VISIBLE);//加载页面的显示

                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());  //判断是否联网缓冲的数据
                videoview.setVideoPath(mediaItem.getData());
                //设置按钮状态
                setButtonState();
            }
        } else if (uri != null) {
            //设置按钮状态---上一个和下一个按钮设置灰色，并且不可以点击
            setButtonState();
        }
    }

    /**
     * 播放下一个视频
     */
    private void playNextVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //播放下一个
            position++;
            if (position < mediaItems.size()) {
                ll_loading.setVisibility(View.VISIBLE);//加载页面的显示

                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());  //判断是否联网缓冲的数据
                videoview.setVideoPath(mediaItem.getData());
                //设置按钮状态
                setButtonState();
            }
        } else if (uri != null) {
            //设置按钮状态---上一个和下一个按钮设置灰色，并且不可以点击
            setButtonState();
        }
    }

    //设置Button状态
    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0) {
            if (mediaItems.size() == 1) {
                //两个按钮都是设置灰色
                btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnVideoPre.setEnabled(false);//不可以点击
                btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnVideoNext.setEnabled(false);//不可以点击
            } else if (mediaItems.size() == 2) {
                if (position == 0) {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);//不可以点击
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);//不可以点击
                } else if (position == mediaItems.size() - 1) {
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);//不可以点击
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);//不可以点击
                } else {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);//不可以点击
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);//不可以点击
                }
            } else {
                if (position == 0) {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);//不可以点击
                } else if (position == mediaItems.size() - 1) {
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);//不可以点击
                } else {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);//不可以点击
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);//不可以点击
                }

            }
        } else if (uri != null) {
            //两个按钮都是设置灰色
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);//不可以点击
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);//不可以点击
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_SPEED://显示网速
                    String netSpeed = utils.getNetSpeed(VitamioVideoPlayer.this); //得到网速
                    //显示网速
                    tv_loading_netspeed.setText("玩命加载中..." + netSpeed);
                    tv_buffer_netspeed.setText("缓冲中..." + netSpeed);
                    //两秒调用一次
                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED, 2000);
                    break;
                case HIDE_MEDIA_CONTORLLER: //控制面板
                    HideMediaContorller();//隐藏
                    break;
                case PROGRESS:
                    // 1.得到当前播放视频的进度
                    int currentPosition = (int) videoview.getCurrentPosition();

                    // 2.设置seekBar.setProgress（当前进度）
                    seekBarVideo.setProgress(currentPosition);

                    //更新播放进度的时间TextView内容
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    //设置系统时间
                    tvSystemTime.setText(getSystemTime());

                    //缓冲进度的更新，只有网络视频资源才有缓冲效果，本地的没有
                    if (isNetUri) {/**下面是一个基于源码的算法，通过看源码得到*/
                        int buffer = videoview.getBufferPercentage();//缓冲  0-100
                        int totalBuffer = buffer * seekBarVideo.getMax();
                        int secondaryProgress = totalBuffer / 100;
                        seekBarVideo.setSecondaryProgress(secondaryProgress);
                    } else {
                        seekBarVideo.setSecondaryProgress(0);
                    }

                    //监听卡顿
                    if (!isUseSystem && videoview.isPlaying()) {
                        if (videoview.isPlaying()) {
                            int buffer = currentPosition - PrecurrentPosition;
                            if (buffer < 500) {
                                //视频卡顿
                                ll_buffer.setVisibility(View.VISIBLE);
                            } else {
                                //视频不卡了
                                ll_buffer.setVisibility(View.GONE);
                            }
                        } else {
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }
                    PrecurrentPosition = currentPosition;


                    // 3.每秒更新一次
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };

    /**
     * 得到系统时间
     */
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());//没有被引用，所以这个对象很快就会被释放
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(this);  /** 初始化Vitamio   */
        setContentView(R.layout.activity_vitamio_video_player);
        initData();//初始化数据
        findViews();
        SetListent(); //设置监听
        GetData();   //得到播放地址
        SetData();  //设置播放地址


        /**设置控制面板    自定义控制面板后就不需要这段代码 */
//        videoview.setMediaController(new MediaController(this));
    }

    //设置播放地址
    private void SetData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());//设置视频的名称
            isNetUri = utils.isNetUri(mediaItem.getData());  //判断是否联网缓冲的数据
            videoview.setVideoPath(mediaItem.getData());//播放地址
        } else if (uri != null) {
            tvName.setText(uri.toString());//设置视频的名称
            isNetUri = utils.isNetUri(uri.toString());  //判断是否联网缓冲的数据
            videoview.setVideoURI(uri);
        } else {
            Toast.makeText(this, "您没有传递数据~", Toast.LENGTH_SHORT).show();
        }
        setButtonState();
    }

    private void GetData() {
        //得到播放地址
        uri = getIntent().getData();

        /**   videolist是键值对中的键*/
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);

    }


    //初始化数据
    private void initData() {
        //1.注册电量广播
        receiver = new MyReceiver();
        IntentFilter intentFiler = new IntentFilter();

        //当电量变化的时候
        intentFiler.addAction(Intent.ACTION_BATTERY_CHANGED);//ACTION_BATTERY_CHANGED：电路变化常量
        registerReceiver(receiver, intentFiler);
        utils = new Utils();

        //2.实例化手势识别器，并且重写双击，单击，长按等方法
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            /**长按
             * @param e
             */
            @Override
            public void onLongPress(MotionEvent e) {
                StartAndPause();//播放和暂停的方法
                super.onLongPress(e);
            }

            /** 双击
             * @param e
             * @return
             */
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                setFullScreenAndDefault();
                return super.onDoubleTap(e);
            }

            /**     单击
             * @param e
             * @return
             */
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isShowMediaContorller) {
                    //如果当前是显示的，单击就实现隐藏效果
                    HideMediaContorller();
                    //把隐藏消息移除
                    handler.removeMessages(HIDE_MEDIA_CONTORLLER);
                } else {
                    ShowMediaContorller();//显示控制面板
                    //发消息隐藏
                    handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTORLLER, 4000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });

        //得到屏幕的宽和高
        /**方法1.....过时的方法*/
//        ScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
//        ScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
        /**方法2.推荐。。。新的方式*/
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ScreenWidth = displayMetrics.widthPixels;
        ScreenHeight = displayMetrics.heightPixels;

        //得到音量
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currenVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        MaxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    }

    //设置视频播放过程中的屏幕大小
    private void setFullScreenAndDefault() {
        if (isFullScreen) {
            //设置默认
            setVideoType(DEFAULT_SRCEEN);
        } else {
            //设置全屏
            setVideoType(FULL_SRCEEN);
        }
    }

    //设置屏幕大小
    private void setVideoType(int defaultSrceen) {
        switch (defaultSrceen) {
            case FULL_SRCEEN: //全屏
                //1.设置视频页面的大小
                videoview.setVideoSize(ScreenWidth, ScreenHeight);
                //2.设置按钮的状态--默认
                btnSwitchPlayer.setBackgroundResource(R.drawable.btn_video_switch_srceen_default_selector);
                isFullScreen = true;

                break;
            case DEFAULT_SRCEEN://默认
                //1.设置视频页面的大小
                int mVideoWidth = videoWidth;//视频真实的宽
                int mVideoHeight = videoHeight;//视频真实的高
                int width = ScreenWidth;//当前手机屏幕的宽
                int height = ScreenHeight;//当前屏幕的宽

                /** for compatibility, we adjust size based on aspect ratio  这是从源码中获取的*/
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                videoview.setVideoSize(width, height);
                //2.设置按钮的状态--全屏
                btnSwitchPlayer.setBackgroundResource(R.drawable.btn_video_switch_srceen_full_selector);
                isFullScreen = false;
                break;
        }
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0); //电量0-100
            setBattery(level); // 设置电量的方法
        }
    }

    //电池
    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    /**
     * 设置监听
     */
    private void SetListent() {
        //准备好的监听
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoWidth = mp.getVideoWidth();//获取视频的宽和高
                videoHeight = mp.getVideoHeight();
                /***下面这行代码的作用是循环播放，这时候就不会回调播放完成这个方法*/
                // mp.setLooping(true);
                videoview.start();//开始播放.
                /**    1. 获得视频总时长，并且与seekBar关联起来*/
                int duration = (int) videoview.getDuration();
                seekBarVideo.setMax(duration);
                tvDuration.setText(utils.stringForTime(duration));//设置文本框的内容
                HideMediaContorller();//默认是隐藏控制面板

                /**    2.发送消息*/
                handler.sendEmptyMessage(PROGRESS);
//                videoview.setVideoSize(mp.getVideoWidth(),mp.getVideoHeight());    //得到视频真正的高和宽
                setVideoType(DEFAULT_SRCEEN);//默认播放屏幕大小

                //消除加载页面效果：
                ll_loading.setVisibility(View.GONE);

//                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                    /**  拖动完成的监听
//                     * @param mp
//                     */
//                    @Override
//                    public void onSeekComplete(MediaPlayer mp) {
//
//                    }
//                });

            }
        });

        //播放出错的监听
        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i("错误的媒体播放器", mp.toString());
                Log.i("发生的错误类型：", String.valueOf(what));
                Log.i("一个额外的代码，特定于错误", String.valueOf(extra));
//                Toast.makeText(SystemViedoPlayer.this, "播放出错", Toast.LENGTH_SHORT).show();
                //1.播放的视频格式不支持  ---这种情况可以栗农万能播放器，也就是跳转到万能播放器继续播放器

                //2.播放网络视频的时候，网络中断-----1.如果网络确实断了，可以提示用户网络断了。2.网络断断续续的，重新播放

                //3播放视频中间有缺省（空白），也就是没下载好-----下载做完成

                showErrowDialog();
                return true; // return false的时候，当播放出错的时候会自动弹出对话框
            }
        });

        //播放完成监听
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNextVideo();//自动播放下一个视频
//                Toast.makeText(SystemViedoPlayer.this, "播放完成", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        //设置SeekBar状态变化的监听
        seekBarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());//视频播放进度
        seekBarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());//声音

        //设置监听网络视频播放的卡顿：前提播放视频的时候，网络比较慢的时候监听卡     setOnInfoListener():监听卡   系统API
        if (isUseSystem) {
            //监听视频播放的卡-系统的Api
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {//判断sdk版本
                videoview.setOnInfoListener(new MyOnInfoListener());
            }
        }

    }
    //弹出错误的对话框
    private void showErrowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("抱歉，无法播放该视频...");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();//关闭当前播放器页面
            }
        });
        builder.show();/**这一步容易忘记~！*/
    }



    class MyOnInfoListener implements MediaPlayer.OnInfoListener {
        /**
         * @param mp
         * @param what
         * @param extra
         * @return
         */
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://视频卡了，拖动卡了
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END://视频卡结束，拖动卡结束了
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }


    class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (progress > 0) {
                    isMute = false;
                } else {
                    isMute = true;
                }
                updataVoice(progress, false);//更新音量
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIA_CONTORLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTORLLER, 4000);
        }
    }

    //更新音量
    private void updataVoice(int progress, boolean isMute) {
        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0); //如果1调用系统，0则不调用
            seekBarVoice.setProgress(0);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0); //如果1调用系统，0则不调用
            seekBarVoice.setProgress(progress);
            currenVoice = progress;
        }
    }


    /**
     *
     */
    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 当手指滑动的时候会引起SeekBar进度变化会回调这个方法
         *
         * @param seekBar
         * @param progress
         * @param fromUser 如果用户因为改变，则为true 。不是用户引起的：比如自动更新进度则返回false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                videoview.seekTo(progress);
            }
        }


        /**
         * 当手指触碰的时候回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIA_CONTORLLER);

        }


        /**
         * 手指离开的时候回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTORLLER, 4000);
        }
    }

    /**
     * 活动implements View.OnClickListener  回调的方法
     */
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    protected void onDestroy() {
        //  super.onDestroy();的作用  ：释放资源的时候，先释放子类，再释放父类。如果先释放父类就会报空指针
        if (receiver != null) {
            unregisterReceiver(receiver);//取消注册广播
            receiver = null;
            super.onDestroy();
        }
    }


    private float startY;
    private float touchRang;//屏幕高
    private int mVol;//当一按下的音量

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //3.把事件传递给手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: //手指按下
                //按下记录值
                startY = event.getY();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);//当前声音
                touchRang = Math.min(ScreenHeight, ScreenWidth);//屏幕高
                handler.removeMessages(HIDE_MEDIA_CONTORLLER);//移除消息

                break;
            case MotionEvent.ACTION_MOVE: //手指移动
                float endY = event.getY();
                float distanceY = startY - endY; //改变的距离

                //改变的声音 = （滑动屏幕的距离：总距离）*音量最大值
                float delta = (distanceY / touchRang) * MaxVoice;
                int voice = (int) Math.min(Math.max(mVol + delta, 0), MaxVoice);//mVol+delta要大于零，但不能超出最大值
                if (delta != 0) {
                    isMute = false;
                    updataVoice(voice, isMute);
                }
                break;
            case MotionEvent.ACTION_UP://手指移开
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTORLLER, 4000);
                break;
        }
        return super.onTouchEvent(event);
    }


    public void ShowMediaContorller() {
        //显示控制面板
        media_controller.setVisibility(View.VISIBLE);
        isShowMediaContorller = true;
    }

    public void HideMediaContorller() {
        //隐藏控制面板
        media_controller.setVisibility(View.GONE);
        isShowMediaContorller = false;
    }

    /**
     * 监听手机物理键，实现声音的调节大小
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currenVoice--;
            updataVoice(currenVoice, false);
            handler.removeMessages(HIDE_MEDIA_CONTORLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTORLLER, 4000);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currenVoice++;
            updataVoice(currenVoice, false);
            updataVoice(currenVoice, false);
            handler.removeMessages(HIDE_MEDIA_CONTORLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTORLLER, 4000);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}

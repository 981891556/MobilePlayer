package com.example.liangjiacheng.mobileplayer.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.liangjiacheng.mobileplayer.IMusicPlayerService;
import com.example.liangjiacheng.mobileplayer.R;
import com.example.liangjiacheng.mobileplayer.Service.MusicPlayerService;
import com.example.liangjiacheng.mobileplayer.utils.Utils;

/**
 * create by liangjiacheng on 2018/11/16  20:41
 * 这个类的作用是：
 */
public class AudioPlayerActivity extends Activity implements View.OnClickListener {
    private static final int PROGERSS = 1;//进度更新
    private ImageView ivIcon;//跳动图片
    private int position;//得到播放位置
    private IMusicPlayerService service;  //服务的代理类，通过它可以调用服务的方法（字段）

    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekBarAudio;
    private Button audioPlayermode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnLyrc;

    private MyReceiver receiver;

    private Utils utils;//转换时间工具

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-11-17 12:56:38 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_audioplayer);

        //跳动动画
        ivIcon = findViewById(R.id.iv_icon);
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) ivIcon.getBackground();
        animationDrawable.start();


        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekBarAudio = (SeekBar) findViewById(R.id.seekBar_audio);
        audioPlayermode = (Button) findViewById(R.id.audio_playermode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioStartPause = (Button) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnLyrc = (Button) findViewById(R.id.btn_lyrc);

        audioPlayermode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnLyrc.setOnClickListener(this);

        //设置进度条的拖动
        seekBarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener() );
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                //拖动进度
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-11-17 12:56:38 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == audioPlayermode) {
            // Handle clicks for audioPlayermode
        } else if (v == btnAudioPre) {
            // Handle clicks for btnAudioPre
        } else if (v == btnAudioStartPause) {
            if (service != null) {
                try {
                    if (service.isPlaying()) {
                        //如果现在播放，那么音乐暂停，并设置按钮为播放状态
                        service.pause();
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                    } else {
                        //音乐暂停，按钮设置为播放状态
                        service.start();
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            // Handle clicks for btnAudioStartPause
        } else if (v == btnAudioNext) {
            // Handle clicks for btnAudioNext
        } else if (v == btnLyrc) {
            // Handle clicks for btnLyrc
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGERSS:
                    try {
                        //1.得到当前进度
                        int currentPosition = service.getCurrentPosition();

                        //2.设置SeekBar.setProgress（进度）
                        seekBarAudio.setProgress(currentPosition);
                        //3.时间进度更新
                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getDuration()));
                        //4.每秒更新一次
                        handler.removeMessages(PROGERSS);
                        handler.sendEmptyMessageDelayed(PROGERSS,1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();//获取控件id
        getData();
        BindAndStartService();//绑定和开启服务
    }

    private void initData() {
        utils = new Utils();
        //注册广播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
        registerReceiver(receiver, intentFilter);
    }

    private ServiceConnection con = new ServiceConnection() {
        /**    当连接成功的时候回调这个方法
         * @param name
         * @param Ibinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder Ibinder) {
            service = IMusicPlayerService.Stub.asInterface(Ibinder);
            if (service != null) {
                try {
                    service.openAudio(position);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**  当断开连接的时候回调这个方法:这个方法只是当你断开连接的时候才会回调，当你退出这个Activity不会回调这个方法
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                if (service != null) {
                    service.stop();
                    service = null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            showViewData();//显示数据
        }
    }

    /**
     * 显示数据
     */
    private void showViewData() {
        try {
            tvArtist.setText(service.getArtist());
            tvName.setText(service.getName());
            seekBarAudio.setMax(service.getDuration());//设置进度条最大值
            /**  发消息给Handler*/
            handler.sendEmptyMessage(PROGERSS);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 绑定和开启服务
     */
    private void BindAndStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.liangjiacheng.mobileplayer_OPENAUDIO");
        bindService(intent, con, Context.BIND_AUTO_CREATE);//BIND_AUTO_CREATE绑定创建
        startService(intent);//不至于实例化多个服务，只是启动一次
    }

    /**
     * 得到数据
     */
    private void getData() {
        position = getIntent().getIntExtra("position", 0);/**跳转页面，从上个页面携带的名称为position的值在这个类中得到*/
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        //取消注册广播
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        super.onDestroy();

    }
}

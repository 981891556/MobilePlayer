package com.example.liangjiacheng.mobileplayer.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.example.liangjiacheng.mobileplayer.R;

public class SplashActvity extends Activity {
    private Handler handler = new Handler();
    private boolean isStartMain = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //在主线程中进行
                startMainActivity();
            }
        }, 2000);
    }

    private void startMainActivity() {
        if (!isStartMain) {
            Intent intent = new Intent(SplashActvity.this, MainActivity.class);
            startActivity(intent);
            //关闭启动页面
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);//移除消息
    }
}

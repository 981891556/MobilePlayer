package com.example.startallplayer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    /*****
     *
     *  intent.setDataAndType(Uri.parse("视频地址"),"video/*");      调用网络视频
     *
     * */
    public void startAllPlayer(View view){
        Intent intent = new Intent();
//        intent.setDataAndType(Uri.parse("http://192.168.0.109:8080/ch7_1.mp4"),"video/*");
        intent.setDataAndType(Uri.parse("http://vfx.mtime.cn/Video/2016/07/19/mp4/160719095812990469.mp4"),"video/*");

        startActivity(intent);
    }
}

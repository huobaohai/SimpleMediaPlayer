package com.example.hbh.mymediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.hbh.mymediaplayer.utils.FormatTime;

import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

public class VideoPlayerActivity extends Activity {

    private final static int PROGRESS = 0;
    private VideoView videoView;

    private TextView start_time;
    private TextView end_time;
    private Button start_btn;
    private Button switch_btn;
    private SeekBar seekBar;
    private LinearLayout controller_layout;
    private RelativeLayout screen;
    private Uri uri;

    private boolean isPlaying = true;
    private boolean isDestroy = false;
    private boolean isFullScreen = false;
    private FormatTime formatTime;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PROGRESS:
                    int currentPos = videoView.getCurrentPosition();
                    start_time.setText(formatTime.stringForTime(currentPos));
                    seekBar.setProgress(videoView.getCurrentPosition());
                    end_time.setText(formatTime.stringForTime(videoView.getDuration()));

                    if (!isDestroy){
                        handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    }
                    break;
            }
        }
    };

    private boolean showDanmu = true;
    private Button switchDanmu;
    private EditText saySomething;
    private Button sendDanmu;
    private DanmakuView danmakuView;
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // 初始化数据
        initData();

        // 设置监听器
        setListener();

//        videoView.setMediaController(new MediaController(this));
    }

    private void setDanmuku() {

        danmakuView.enableDanmakuDrawingCache(true);
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {

                if (showDanmu){
                    danmakuView.start();
                    Log.i(".............", "prepared: ");
                    // 增加一些自定义弹幕
                    generateDanmu();
                }
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        danmakuContext = DanmakuContext.create();
        danmakuView.prepare(parser,danmakuContext);

    }

    private void generateDanmu() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (showDanmu) {
                    int time = new Random().nextInt(300);
                    String content = "" + time + time;
                    addDanmaku(content, false);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // 转换单位
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private void addDanmaku(String content, boolean b) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 10;
        danmaku.textColor = Color.WHITE;
        danmaku.textSize = sp2px(20);
        danmaku.setTime(danmakuView.getCurrentTime());
        if (b){
            danmaku.borderColor = Color.RED;
        }
        danmakuView.addDanmaku(danmaku);
    }

    private void setListener() {

        screen.setOnClickListener(myClickListener);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();

                // 管理弹幕
                setDanmuku();

                isPlaying = videoView.isPlaying();
                if (isPlaying){
                    start_btn.setBackgroundResource(R.drawable.pause_con);
                }else {
                    start_btn.setBackgroundResource(R.drawable.start_con);
                }

                seekBar.setMax(videoView.getDuration());

                handler.sendEmptyMessage(PROGRESS);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(getApplicationContext(),"over...",Toast.LENGTH_LONG).show();
                finish();
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getApplicationContext(),"error...",Toast.LENGTH_LONG).show();
                return true;
            }
        });

        start_btn.setOnClickListener(myClickListener);
        switch_btn.setOnClickListener(myClickListener);
        switchDanmu.setOnClickListener(myClickListener);

        sendDanmu.setOnClickListener(myClickListener);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private View.OnClickListener myClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.con_btn_start:
                    if (isPlaying){
                        videoView.pause();
                        pauseDanmu();
                        start_btn.setBackgroundResource(R.drawable.start_con);
                    }else{
                        videoView.start();
                        startDanmu();
                        start_btn.setBackgroundResource(R.drawable.pause_con);
                    }
                    isPlaying = !isPlaying;
                    break;
                case R.id.con_btn_switch:
                    //调用本地播放器
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(uri.toString()), "video/*");
                    startActivity(intent);
                    break;
                case R.id.screen_layout:
                    isFullScreen = !isFullScreen;
                    controller();
                    break;
                case R.id.show_danmu:
                    showDanmu = !showDanmu;
                    controller();
                    break;

                case R.id.send_danmu:
                    videoView.start();
                    startDanmu();
                    addDanmaku(saySomething.getText().toString(), true);
                    saySomething.setText("");
                    break;
            }
        }
    };

    private void startDanmu() {
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }

    private void pauseDanmu() {
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    private void initData() {
        videoView = findViewById(R.id.vv_player);
        start_btn = findViewById(R.id.con_btn_start);
        switch_btn = findViewById(R.id.con_btn_switch);
        seekBar = findViewById(R.id.con_seekBar);
        start_time = findViewById(R.id.con_start_time);
        end_time = findViewById(R.id.con_end_time);

        controller_layout = findViewById(R.id.controller_layout);
        screen = findViewById(R.id.screen_layout);
        saySomething = findViewById(R.id.say_some);
        danmakuView = findViewById(R.id.danmu_ku);
        switchDanmu = findViewById(R.id.show_danmu);
        switchDanmu.setText("关闭弹幕");
        sendDanmu = findViewById(R.id.send_danmu);
        controller();

        uri = getIntent().getData();
        videoView.setVideoURI(uri);
        formatTime = new FormatTime();


    }

    private void controller(){
        if (!isFullScreen){
            controller_layout.setVisibility(View.VISIBLE);
        }else {
            controller_layout.setVisibility(View.GONE);
        }
        if (showDanmu){
            danmakuView.show();
            switchDanmu.setText("关闭弹幕");
        }else {
            danmakuView.hide();
            switchDanmu.setText("开启弹幕");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseDanmu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startDanmu();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        showDanmu = false;
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }
}

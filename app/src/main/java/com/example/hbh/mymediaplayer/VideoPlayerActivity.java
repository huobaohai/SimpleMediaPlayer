package com.example.hbh.mymediaplayer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.hbh.mymediaplayer.utils.FormatTime;

public class VideoPlayerActivity extends Activity {

    private final static int PROGRESS = 0;
    private VideoView videoView;

    private TextView start_time;
    private TextView end_time;
    private Button start_btn;
    private Button switch_btn;
    private SeekBar seekBar;
    private Uri uri;

    private boolean isPlaying = true;
    private boolean isDestroy = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        initData();

        setListener();



//        videoView.setMediaController(new MediaController(this));
    }

    private void setListener() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
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
                        start_btn.setBackgroundResource(R.drawable.start_con);
                    }else{
                        videoView.start();
                        start_btn.setBackgroundResource(R.drawable.pause_con);
                    }
                    isPlaying = !isPlaying;
                    break;
            }
        }
    };

    private void initData() {
        videoView = findViewById(R.id.vv_player);
        start_btn = findViewById(R.id.con_btn_start);
        switch_btn = findViewById(R.id.con_btn_switch);
        seekBar = findViewById(R.id.con_seekBar);
        start_time = findViewById(R.id.con_start_time);
        end_time = findViewById(R.id.con_end_time);

        uri = getIntent().getData();
        videoView.setVideoURI(uri);
        formatTime = new FormatTime();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }
}

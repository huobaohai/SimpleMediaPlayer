package com.example.hbh.mymediaplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hbh.mymediaplayer.Dao.VideoItem;
import com.example.hbh.mymediaplayer.utils.FormatTime;

import java.util.ArrayList;
import java.util.List;

public class MediaSearchActivity extends Activity {

    private ListView listView_video;
    private TextView text_no;

    private List<VideoItem> videoList ;

    private FormatTime formatTime = new FormatTime();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if (videoList != null && videoList.size()>0) {
                        text_no.setVisibility(View.GONE);
                        listView_video.setAdapter(new mdAdapter());
                    }else {
                        text_no.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_search);
        initView();

        searchVideo();

        setListener();
    }

    /**
     * 设置点击监听
     * @author hbh
     * @time 2019/3/11 14:31
     */
    private void setListener() {
        listView_video.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoItem item = videoList.get(position);

                // 调用自己的播放器
                Intent intent = new Intent(MediaSearchActivity.this,VideoPlayerActivity.class);
                intent.setData(Uri.parse(item.getData()));
                startActivity(intent);
            }
        });
    }

    private void searchVideo() {
        new Thread(){
            @Override
            public void run() {

                videoList = new ArrayList<>();

                checkReadPermission();

                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] projection = {
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DURATION
                };
                Cursor cursor = getContentResolver().query(uri,projection,null,null,null);

                while (cursor.moveToNext()){
                    VideoItem item = new VideoItem();
                    item.setName(cursor.getString(0));
                    item.setData(cursor.getString(1));
                    item.setSize(cursor.getLong(2));
                    item.setDuration(formatTime.stringForTime((int) cursor.getLong(3)));

                    videoList.add(item);
                }

                cursor.close();

                handler.sendEmptyMessage(1);
            }
        }.start();
    }

    private void checkReadPermission() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MediaSearchActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }

    private void initView() {
        listView_video = findViewById(R.id.lv_search_media);
        text_no = findViewById(R.id.tv_no);
    }

    private class mdAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return videoList.size();
        }

        @Override
        public Object getItem(int position) {
            return videoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            TextView textView = new TextView(MediaSearchActivity.this);
//            textView.setText(getItem(position).toString());
            View view;
            ViewHolder holder;
            if (convertView!=null){
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else{
                view = View.inflate(MediaSearchActivity.this,R.layout.list_items,null);
                holder = new ViewHolder();
                holder.tv_name = view.findViewById(R.id.item_tv_name);
                holder.tv_size = view.findViewById(R.id.item_tv_size);
                holder.tv_duration = view.findViewById(R.id.item_tv_duration);
                view.setTag(holder);
            }

            holder.tv_name.setText(videoList.get(position).getName());
            holder.tv_size.setText(Formatter.formatFileSize(MediaSearchActivity.this,videoList.get(position).getSize()));
            holder.tv_duration.setText(String.valueOf(videoList.get(position).getDuration()));

            return view;
        }
    }

    private static class ViewHolder {
        TextView tv_name;
        TextView tv_size;
        TextView tv_duration;
    }
}

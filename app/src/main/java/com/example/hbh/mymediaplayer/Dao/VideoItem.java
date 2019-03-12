package com.example.hbh.mymediaplayer.Dao;

public class VideoItem {

    private String name;
    private String data;
    private String duration;
    private long size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String toString(){
        return "Video : " + name + ", size= " + size + ", duration= " + duration + ", data= " + data;
    }
}

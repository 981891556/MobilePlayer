package com.example.liangjiacheng.mobileplayer.domain;

import java.io.Serializable;

/**
 * create by liangjiacheng on 2018/10/24  8:17
 * 这个Bean类的作用是：代表一个视频和音频      也是解析json 数据的bean类
 *  实现Serializable这个接口就可以实现序列表，并不用实现任何方法，简单。
 */
public class MediaItem  implements Serializable {

    private String name; //名字
    private long duration;//时长
    private long size;//大小
    private String data;//内容
    private String artist;//作者

    private String desc;//网络视频图片的描述信息
    private String imageUrl;//网络视频图片的地址
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", artist='" + artist + '\'' +
                ", desc='" + desc + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}

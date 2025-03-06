package com.moesee.moeseedemo.pojo;

public class Video {
    private long aid;
    private int duration;
    private String title;
    private String tname;     // 第一个标签，如“搞笑”
    private String tnamev2;   // 第二个标签，如“剧情演绎”
    private String short_link_v2; // 视频 URL
    private Stat stat;        // 播放量信息

    // Getter 和 Setter
    public long getAid() {
        return aid;
    }
    public void setAid(long aid) {
        this.aid = aid;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTname() {
        return tname;
    }
    public void setTname(String tname) {
        this.tname = tname;
    }
    public String getTnamev2() {
        return tnamev2;
    }
    public void setTnamev2(String tnamev2) {
        this.tnamev2 = tnamev2;
    }
    public String getShort_link_v2() {
        return short_link_v2;
    }
    public void setShort_link_v2(String short_link_v2) {
        this.short_link_v2 = short_link_v2;
    }
    public Stat getStat() {
        return stat;
    }
    public void setStat(Stat stat) {
        this.stat = stat;
    }
}


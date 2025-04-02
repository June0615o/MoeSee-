package com.moesee.moeseedemo.pojo;

public class Video {
    private int videoId;
    private String videoTitle;
    private String videoTags;
    private String videoUrl;
    private int videoViews;
    private int videoLiked; // 视频 URL
    private int videoDuration;
    private String videoCoverImage;
    private int videoClusterId;
    private int videoFutureViews;
    private double videoHeat;

    public int getVideoFutureViews() {
        return videoFutureViews;
    }

    public void setVideoFutureViews(int videoFutureView) {
        this.videoFutureViews = videoFutureView;
    }

    public double getVideoHeat() {
        return videoHeat;
    }

    public void setVideoHeat(double videoHeat) {
        this.videoHeat = videoHeat;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoTags() {
        return videoTags;
    }

    public void setVideoTags(String videoTags) {
        this.videoTags = videoTags;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getVideoViews() {
        return videoViews;
    }

    public void setVideoViews(int videoViews) {
        this.videoViews = videoViews;
    }

    public int getVideoLiked() {
        return videoLiked;
    }

    public void setVideoLiked(int videoLiked) {
        this.videoLiked = videoLiked;
    }

    public int getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(int videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getVideoCoverImage() {
        return videoCoverImage;
    }

    public void setVideoCoverImage(String videoCoverImage) {
        this.videoCoverImage = videoCoverImage;
    }

    public int getVideoClusterId() {
        return videoClusterId;
    }

    public void setVideoClusterId(int videoClusterId) {
        this.videoClusterId = videoClusterId;
    }
}


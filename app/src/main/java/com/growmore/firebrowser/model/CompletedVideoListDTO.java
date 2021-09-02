package com.growmore.firebrowser.model;

public class CompletedVideoListDTO {
    public enum ITEM_TYPE {
        IS_HEADER, IS_VIDEO
    }
    private ITEM_TYPE type;
    private HeaderModel headerModel;
    private VideoModel videoModel;
    private String time;

    public ITEM_TYPE getType() {
        return type;
    }
    public void setType(ITEM_TYPE type) {
        this.type = type;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getTime() {
        return time;
    }
    public HeaderModel getHeaderModel() {
        return headerModel;
    }
    public void setHeaderModel(HeaderModel headerModel) {
        this.headerModel = headerModel;
    }
    public VideoModel getVideoModel() {
        return videoModel;
    }
    public void setVideoModel(VideoModel videoModel) {
        this.videoModel = videoModel;
    }
}

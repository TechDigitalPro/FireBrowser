package com.growmore.firebrowser.model;

public class FireVideoModel {

    public FireVideoModel(){}

    private int id;
    private String name;
    private String url, offlineUrl;
    private String duration;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getOfflineUrl() {
        return offlineUrl;
    }
    public void setOfflineUrl(String offlineUrl) {
        this.offlineUrl = offlineUrl;
    }
    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
}

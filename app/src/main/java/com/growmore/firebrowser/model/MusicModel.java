package com.growmore.firebrowser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MusicModel {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("playUrl")
    @Expose
    private String playUrl;
    @SerializedName("coverThumb")
    @Expose
    private String coverThumb;
    @SerializedName("coverMedium")
    @Expose
    private String coverMedium;
    @SerializedName("authorName")
    @Expose
    private String authorName;
    @SerializedName("original")
    @Expose
    private boolean original;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    public String getCoverMedium() {
        return coverMedium;
    }
    public void setCoverMedium(String coverMedium) {
        this.coverMedium = coverMedium;
    }
    public String getCoverThumb() {
        return coverThumb;
    }
    public void setCoverThumb(String coverThumb) {
        this.coverThumb = coverThumb;
    }
    public String getPlayUrl() {
        return playUrl;
    }
    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }
    public boolean isOriginal() {
        return original;
    }
    public void setOriginal(boolean original) {
        this.original = original;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    private String name;
    private String urlOffline;
    private boolean isPlaying;
    private int progress;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrlOffline() {
        return urlOffline;
    }
    public void setUrlOffline(String urlOffline) {
        this.urlOffline = urlOffline;
    }
    public boolean isPlaying() {
        return isPlaying;
    }
    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }
    public int getProgress() {
        return progress;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }
}

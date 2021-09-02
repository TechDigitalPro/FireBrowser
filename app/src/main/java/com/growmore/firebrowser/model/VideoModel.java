package com.growmore.firebrowser.model;

import com.downloader.Progress;

public class VideoModel {
    private String id;
    private int downloadId;
    private String title, onlineUrl, offlineUrl, thumbUrl, siteName;
    private String time, duration;
    private long totalSize, downloadSize, downloadSpeed;
    private boolean isFavorite, isPrivate, isNew;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getDownloadId() {
        return downloadId;
    }
    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getOnlineUrl() {
        return onlineUrl;
    }
    public void setOnlineUrl(String onlineUrl) {
        this.onlineUrl = onlineUrl;
    }
    public String getOfflineUrl() {
        return offlineUrl;
    }
    public void setOfflineUrl(String offlineUrl) {
        this.offlineUrl = offlineUrl;
    }
    public String getThumbUrl() {
        return thumbUrl;
    }
    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }
    public String getSiteName() {
        return siteName;
    }
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
    public long getTotalSize() {
        return totalSize;
    }
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
    public long getDownloadSize() {
        return downloadSize;
    }
    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }
    public long getDownloadSpeed() {
        return downloadSpeed;
    }
    public void setDownloadSpeed(long downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }
    public boolean isFavorite() {
        return isFavorite;
    }
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    public boolean isPrivate() {
        return isPrivate;
    }
    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
    public boolean isNew() {
        return isNew;
    }
    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    //for progress bar
    private Progress process;
    public Progress getProcess() {
        return process;
    }
    public void setProcess(Progress process) {
        this.process = process;
    }
    private int  download_status = 0;
    /*
       0 : not download
       1 : start download
       2 : cancel download
       3 : finish download
       4 : pause download
    */
    public void setDownload_status(int download_status) {
        this.download_status = download_status;
    }
    public int getDownload_status(){
        return this.download_status;
    }
}

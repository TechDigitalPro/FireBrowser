package com.growmore.firebrowser.model;

public class DownloadSettingModel {
    private boolean isPrivate, isWifiOnly, isRingtone, isVibration;
    private boolean isDefaultBrowser, isAutoConnect;

    public DownloadSettingModel(){
        isPrivate = true;
        isWifiOnly = isRingtone = isVibration = false;
        isDefaultBrowser = isAutoConnect = false;
    }

    public boolean isPrivate() {
        return isPrivate;
    }
    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
    public boolean isWifiOnly() {
        return isWifiOnly;
    }
    public void setWifiOnly(boolean wifiOnly) {
        isWifiOnly = wifiOnly;
    }
    public boolean isRingtone() {
        return isRingtone;
    }
    public void setRingtone(boolean ringtone) {
        isRingtone = ringtone;
    }
    public boolean isVibration() {
        return isVibration;
    }
    public void setVibration(boolean vibration) {
        isVibration = vibration;
    }
    public boolean isDefaultBrowser() {
        return isDefaultBrowser;
    }
    public void setDefaultBrowser(boolean defaultBrowser) {
        isDefaultBrowser = defaultBrowser;
    }
    public boolean isAutoConnect() {
        return isAutoConnect;
    }
    public void setAutoConnect(boolean autoConnect) {
        isAutoConnect = autoConnect;
    }
}

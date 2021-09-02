package com.growmore.firebrowser.model;

public class EventChangeVolume {
    private boolean isMute;

    public EventChangeVolume(boolean volumeActive) {
        this.isMute = volumeActive;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }
}

package com.growmore.firebrowser.player;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.growmore.firebrowser.model.MusicModel;

import java.io.File;
import java.util.ArrayList;

public class SimpleMusicPlayer {
    OnPlayerEventListener mListener;
    Activity mActivity;

    public ArrayList<MusicModel> songs = null;
    public boolean isPaused = false;
    public int currentPosition = 0;
    public int currentDuration = 0;

    public static MediaPlayer player;

    public SimpleMusicPlayer(Activity ma, OnPlayerEventListener listener) {
        mActivity = ma;
        mListener = listener;
    }

    public void init(ArrayList<MusicModel> _songs) {
        songs = _songs;
        currentPosition = 0;

        if (player == null) {
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    player.stop();
                    player.reset();
                    //nextSong();
                    mListener.onPlayerSongComplete(currentPosition);
                }
            });
        }
    }

    public void setCurrentPosition(int position){
        currentPosition = position;
    }

    public void stop() {
        if (player != null) {
            if (player.isPlaying())
                player.stop();
            player.reset();
        }
    }

    public void pause() {
        if (!isPaused && player != null) {
            player.pause();
            isPaused = true;
        }
    }

    public boolean isPlaying(){
        if (player != null && player.isPlaying()) {
            return true;
        }else {
            return false;
        }
    }

    public void play() {
        if (player != null) {
            if (!isPaused && !player.isPlaying()) {
                if (songs != null) {
                    if (songs.size() > 0) {
                        try {
                            Uri u = Uri.fromFile(new File(songs.get(currentPosition).getUrlOffline()));

                            player.setDataSource(mActivity, u);
                            player.prepare();
                            currentDuration = player.getDuration();

                            player.start();
                            mListener.onPlayerSongStart(currentPosition);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    player.start();
                    isPaused = false;
                }
            }
        }
    }

    public void nextSong() {
        if (player != null) {
            if (isPaused)
                isPaused = false;

            if (player.isPlaying())
                player.stop();

            player.reset();

            if ((currentPosition + 1) == songs.size())
                currentPosition = 0;
            else
                currentPosition = currentPosition + 1;

            play();
        }
    }

    public void previousSong() {
        if (player != null) {
            if (isPaused)
                isPaused = false;

            if (player.isPlaying())
                player.stop();

            player.reset();

            if (currentPosition - 1 < 0)
                currentPosition = songs.size();
            else
                currentPosition = currentPosition - 1;

            play();
        }
    }

    public void setSeekPosition(int msec) {
        if (player != null)
            player.seekTo(msec);
    }

    public int getSeekPosition() {
        if (player != null)
            return player.getCurrentPosition();
        else
            return -1;
    }

    public interface OnPlayerEventListener {
        void onPlayerSongComplete(int songPosition);

        void onPlayerSongStart(int songPosition);
    }
}



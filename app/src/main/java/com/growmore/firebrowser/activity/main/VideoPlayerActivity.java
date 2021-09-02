package com.growmore.firebrowser.activity.main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.LocaleList;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.CaptionStyleCompat;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.material.snackbar.Snackbar;
import com.growmore.firebrowser.R;
import com.growmore.firebrowser.base.BaseActivity;
import com.growmore.firebrowser.model.EventChangeVolume;
import com.growmore.firebrowser.model.VideoModel;
import com.growmore.firebrowser.player.BrightnessControl;
import com.growmore.firebrowser.player.CustomDefaultTimeBar;
import com.growmore.firebrowser.player.CustomStyledPlayerView;
import com.growmore.firebrowser.player.Prefs;
import com.growmore.firebrowser.player.Utils;
import com.growmore.firebrowser.utils.CommonUtils;
import com.growmore.firebrowser.utils.Config;
import com.growmore.firebrowser.utils.GsonUtils;
import com.growmore.firebrowser.utils.ShareUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoPlayerActivity extends BaseActivity {
    private static final String TAG = "VideoActivity";

    private PlaybackStateListener playbackStateListener;
    private BroadcastReceiver mReceiver;
    private AudioManager mAudioManager;
    private MediaSessionCompat mediaSession;
    private DefaultTrackSelector trackSelector;
    public static LoudnessEnhancer loudnessEnhancer;

    private CustomStyledPlayerView playerView;
    public static SimpleExoPlayer player;

    private Prefs mPrefs;
    public static BrightnessControl mBrightnessControl;
    public static boolean haveMedia;
    private boolean setTracks;
    public static boolean controllerVisible;
    public static boolean controllerVisibleFully;
    public static Snackbar snackbar;
    private ExoPlaybackException errorToShow;
    public static int boostLevel = 0;
    private static final int REQUEST_CHOOSER_VIDEO = 1;
    private static final int REQUEST_CHOOSER_SUBTITLE = 2;
    private static final int REQUEST_CHOOSER_SCOPE_DIR = 10;
    public static final int CONTROLLER_TIMEOUT = 3500;
    private CoordinatorLayout coordinatorLayout;
    //private TextView titleView;
    private ImageButton buttonAspectRatio;
    private ImageButton exoPlayPause;
    private ProgressBar loadingProgressBar;

    private boolean restoreOrientationLock;
    private boolean restorePlayState;
    private boolean play;
    private float subtitlesScale;
    private boolean isScrubbing;
    private boolean scrubbingNoticeable;
    private long scrubbingStart;
    public boolean frameRendered;
    private boolean alive;

    private String mVideoPath = "";
    private VideoModel mVideoModel;
    @BindView(R.id.lvTopToolbar) LinearLayout lvTopToolbar;
    @BindView(R.id.btBack) ImageView btBack;
    @BindView(R.id.btCopy) ImageView btCopy;
    @BindView(R.id.btShare) ImageView btShare;
    @BindView(R.id.btVolume) ImageView btVolume;
    @BindView(R.id.btForward_5) ImageView btForward_5;
    @BindView(R.id.btRewind_5) ImageView btRewind_5;
    @BindView(R.id.exo_controls_background) FrameLayout videoControllerView;
    @BindView(R.id.btPlayerUnLock) ImageView btPlayerUnLock;
    @BindView(R.id.lvUnLokView) RelativeLayout lvUnLokView;
    @BindView(R.id.lvPlayerGuide) RelativeLayout lvPlayerGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = new Prefs(this);
        Utils.setOrientation(this, mPrefs.orientation);
        setContentView(R.layout.activity_video_player);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_color));
        }
        initView();
    }

    @OnClick(R.id.btBack)
    public void onClickBack(){
        onBackPressed();
    }
    @OnClick(R.id.btCopy)
    public void onClickCopy(){
        CommonUtils.setClipboard(this, mVideoModel.getOnlineUrl());
        showMessages(getString(R.string.txt_url_copied_to_clipboard));
    }

    @OnClick(R.id.btShare)
    public void onClickShare(){
        ShareUtils.shareVideo(this, mVideoModel.getOfflineUrl());
    }
    @OnClick(R.id.btVolume)
    public void onClickVolume(){
        if(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0){
            btVolume.setImageResource(R.drawable.ic_volume_off);
            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }else {
            btVolume.setImageResource(R.drawable.ic_volume_down);
            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        }
    }
    @OnClick(R.id.btPlayerUnLock)
    public void onClickPlayerUnLock(){
        videoControllerView.setVisibility(View.VISIBLE);
        btPlayerUnLock.setVisibility(View.GONE);
        lvUnLokView.setVisibility(View.GONE);
        Utils.setOrientation(VideoPlayerActivity.this, mPrefs.orientation);
    }
    @OnClick(R.id.btForward_5)
    public void onClickForward(){
        if (player != null){
            if (player.getCurrentPosition() + 5000 < player.getDuration()){
                player.seekTo(player.getCurrentWindowIndex(), player.getCurrentPosition() + 5000);
            }
        }
    }
    @OnClick(R.id.btRewind_5)
    public void onClickRewind(){
        if (player != null){
            if (player.getCurrentPosition() > 5000){
                player.seekTo(player.getCurrentWindowIndex(), player.getCurrentPosition() - 5000);
            }else {
                player.seekTo(player.getCurrentWindowIndex(), 0);
            }
        }
    }

    private void initView() {
        String modelStr = getIntent().getStringExtra(Config.PLAYER_VIDEO_MODEL);
        mVideoModel = GsonUtils.getInstance().fromJson(modelStr, VideoModel.class);
        mVideoPath = mVideoModel.getOfflineUrl();

        mPrefs.updateMedia(Uri.parse(mVideoPath), MimeTypes.VIDEO_MP4);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        playerView = findViewById(R.id.video_view);
        exoPlayPause = findViewById(R.id.exo_play_pause);
        loadingProgressBar = findViewById(R.id.loading);

        playerView.setShowNextButton(false);
        playerView.setShowPreviousButton(false);
        playerView.setShowFastForwardButton(false);
        playerView.setShowRewindButton(false);
        playerView.setControllerHideOnTouch(true);
        playerView.setControllerAutoShow(true);

        CustomDefaultTimeBar timeBar = playerView.findViewById(R.id.exo_progress);
        timeBar.setPlayedColor(getResources().getColor(R.color.colorAccent));
        timeBar.setUnplayedColor(getResources().getColor(R.color.grey_light));
        timeBar.setScrubberColor(getResources().getColor(R.color.colorAccent));
        timeBar.setBufferedColor(getResources().getColor(R.color.grey_light));
        timeBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(TimeBar timeBar, long position) {
                restorePlayState = player.isPlaying();
                if (restorePlayState) {
                    player.pause();
                }
                scrubbingNoticeable = false;
                isScrubbing = true;
                frameRendered = true;
                playerView.setControllerShowTimeoutMs(-1);
                scrubbingStart = player.getCurrentPosition();
                player.setSeekParameters(SeekParameters.CLOSEST_SYNC);
                reportScrubbing(position);
            }

            @Override
            public void onScrubMove(TimeBar timeBar, long position) {
                reportScrubbing(position);
            }

            @Override
            public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                playerView.setCustomErrorMessage(null);
                isScrubbing = false;
                if (restorePlayState) {
                    restorePlayState = false;
                    playerView.setControllerShowTimeoutMs(VideoPlayerActivity.CONTROLLER_TIMEOUT);
                    player.setPlayWhenReady(true);
                }
            }
        });

        ImageView buttonRotation = findViewById(R.id.exo_bt_rotate);
        buttonRotation.setOnClickListener(view -> {
            mPrefs.orientation = Utils.getNextOrientation(mPrefs.orientation);
            Utils.setOrientation(VideoPlayerActivity.this, mPrefs.orientation);
            Utils.showText(playerView, getString(mPrefs.orientation.description), 2500);
            resetHideCallbacks();
        });
        ImageView buttonLock = findViewById(R.id.exo_bt_lock);
        buttonLock.setOnClickListener(view -> {
            videoControllerView.setVisibility(View.GONE);
            btPlayerUnLock.setVisibility(View.VISIBLE);
            lvUnLokView.setVisibility(View.VISIBLE);
            Utils.setOrientationLock(VideoPlayerActivity.this, true);
        });

        final StyledPlayerControlView controlView = playerView.findViewById(R.id.exo_controller);
        controlView.setOnApplyWindowInsetsListener((view, windowInsets) -> {
            if (windowInsets != null) {
                view.setPadding(0, windowInsets.getSystemWindowInsetTop(),
                        0, windowInsets.getSystemWindowInsetBottom());

                int insetLeft = windowInsets.getSystemWindowInsetLeft();
                int insetRight = windowInsets.getSystemWindowInsetRight();

                int paddingLeft = 0;
                int marginLeft = insetLeft;

                int paddingRight = 0;
                int marginRight = insetRight;

                if (Build.VERSION.SDK_INT >= 28 && windowInsets.getDisplayCutout() != null) {
                    if (windowInsets.getDisplayCutout().getSafeInsetLeft() == insetLeft) {
                        paddingLeft = insetLeft;
                        marginLeft = 0;
                    }
                    if (windowInsets.getDisplayCutout().getSafeInsetRight() == insetRight) {
                        paddingRight = insetRight;
                        marginRight = 0;
                    }
                }

                findViewById(R.id.exo_progress).setPadding(windowInsets.getSystemWindowInsetLeft(), 0,
                        windowInsets.getSystemWindowInsetRight(), 0);

                windowInsets.consumeSystemWindowInsets();
            }
            return windowInsets;
        });

        findViewById(R.id.delete).setOnClickListener(view -> {
            releasePlayer();
            deleteMedia();
            haveMedia = false;
        });

        playbackStateListener = new PlaybackStateListener();

        mBrightnessControl = new BrightnessControl(this);
        if (mPrefs.brightness >= 0) {
            mBrightnessControl.currentBrightnessLevel = mPrefs.brightness;
            mBrightnessControl.setScreenBrightness(mBrightnessControl.levelToBrightness(mBrightnessControl.currentBrightnessLevel));
        }

        playerView.setControllerVisibilityListener(new StyledPlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                controllerVisible = visibility == View.VISIBLE;
                controllerVisibleFully = playerView.isControllerFullyVisible();

                if (visibility == View.VISIBLE) {
                    Utils.showSystemUi(playerView);
                    findViewById(R.id.exo_play_pause).requestFocus();
                } else {
                    Utils.hideSystemUi(playerView);
                }

                if (mPrefs.firstRun){
                    lvPlayerGuide.setVisibility(View.VISIBLE);
                }else {
                    lvPlayerGuide.setVisibility(View.GONE);
                }


            }
        });
        lvPlayerGuide.setOnClickListener(view -> {
            mPrefs.markFirstRun();
            lvPlayerGuide.setVisibility(View.GONE);
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        alive = true;
        //updateSubtitleStyle();
        initializePlayer();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        alive = false;
        releasePlayer();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventVolumeChange(EventChangeVolume evt){
        if (evt.isMute()){
            btVolume.setImageResource(R.drawable.ic_volume_down);
        }else {
            btVolume.setImageResource(R.drawable.ic_volume_off);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null) {
            mPrefs.updateMedia(intent.getData(), intent.getType());
            initializePlayer();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                playerView.removeCallbacks(playerView.textClearRunnable);
                Utils.adjustVolume(mAudioManager, playerView, keyCode == KeyEvent.KEYCODE_VOLUME_UP, event.getRepeatCount() == 0);
                return true;
            case KeyEvent.KEYCODE_BUTTON_SELECT:
            case KeyEvent.KEYCODE_BUTTON_START:
            case KeyEvent.KEYCODE_BUTTON_A:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
            case KeyEvent.KEYCODE_SPACE:
                if (!controllerVisibleFully) {
                    if (player.isPlaying()) {
                        player.pause();
                    } else {
                        player.play();
                    }
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_BUTTON_L2:
                if (!controllerVisibleFully) {
                    playerView.removeCallbacks(playerView.textClearRunnable);
                    long seekTo = player.getCurrentPosition() - 10_000;
                    if (seekTo < 0)
                        seekTo = 0;
                    player.setSeekParameters(SeekParameters.PREVIOUS_SYNC);
                    player.seekTo(seekTo);
                    playerView.setCustomErrorMessage(Utils.formatMilis(seekTo));
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_BUTTON_R2:
                if (!controllerVisibleFully) {
                    playerView.removeCallbacks(playerView.textClearRunnable);
                    long seekTo = player.getCurrentPosition() + 10_000;
                    long seekMax = player.getDuration();
                    if (seekMax != C.TIME_UNSET && seekTo > seekMax)
                        seekTo = seekMax;
                    VideoPlayerActivity.player.setSeekParameters(SeekParameters.NEXT_SYNC);
                    player.seekTo(seekTo);
                    playerView.setCustomErrorMessage(Utils.formatMilis(seekTo));
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                break;
            default:
                if (!controllerVisibleFully) {
                    playerView.showController();
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                playerView.postDelayed(playerView.textClearRunnable, CustomStyledPlayerView.MESSAGE_TIMEOUT_KEY);
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_BUTTON_L2:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_BUTTON_R2:
                playerView.postDelayed(playerView.textClearRunnable, CustomStyledPlayerView.MESSAGE_TIMEOUT_KEY);
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (restoreOrientationLock) {
                Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
                restoreOrientationLock = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && alive) {
            initializePlayer();
        }
    }

    private void initializePlayer() {
        haveMedia = Uri.parse(mVideoPath) != null;

        if (player == null) {
            trackSelector = new DefaultTrackSelector(this);
            if (Build.VERSION.SDK_INT >= 24) {
                final LocaleList localeList = Resources.getSystem().getConfiguration().getLocales();
                final List<String> locales = new ArrayList<>();
                for (int i = 0; i < localeList.size(); i++) {
                    locales.add(localeList.get(i).getISO3Language());
                }
                trackSelector.setParameters(trackSelector.buildUponParameters()
                        .setPreferredAudioLanguages(locales.toArray(new String[0]))
                );
            } else {
                final Locale locale = Resources.getSystem().getConfiguration().locale;
                trackSelector.setParameters(trackSelector.buildUponParameters()
                        .setPreferredAudioLanguage(locale.getISO3Language())
                );
            }
            RenderersFactory renderersFactory = new DefaultRenderersFactory(this)
                    .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);
            final DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory()
                    .setTsExtractorTimestampSearchBytes(1500 * TsExtractor.TS_PACKET_SIZE);
            player = new SimpleExoPlayer.Builder(this, renderersFactory)
                    .setTrackSelector(trackSelector)
                    .setMediaSourceFactory(new DefaultMediaSourceFactory(this, extractorsFactory))
                    .build();
            final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.CONTENT_TYPE_MOVIE)
                    .build();
            player.setAudioAttributes(audioAttributes, true);
        }

        playerView.setPlayer(player);

        mediaSession = new MediaSessionCompat(this, getString(R.string.app_name));
        MediaSessionConnector mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setPlayer(player);

        mediaSessionConnector.setMediaMetadataProvider(player -> {
            final String title = mVideoModel.getTitle();
            if (title == null)
                return null;
            else
                return new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .build();
        });

        playerView.setControllerShowTimeoutMs(-1);

        if (haveMedia) {
            playerView.setResizeMode(mPrefs.resizeMode);

            if (mPrefs.resizeMode == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) {
                playerView.setScale(mPrefs.scale);
            } else {
                playerView.setScale(1.f);
            }

            MediaItem.Builder mediaItemBuilder = new MediaItem.Builder()
                    .setUri(Uri.parse(mVideoPath))
                    .setMimeType(MimeTypes.VIDEO_MP4);
            player.setMediaItem(mediaItemBuilder.build());

            if (loudnessEnhancer != null) {
                loudnessEnhancer.release();
            }
            try {
                int audioSessionId = C.generateAudioSessionIdV21(this);
                loudnessEnhancer = new LoudnessEnhancer(audioSessionId);
                player.setAudioSessionId(audioSessionId);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            player.addAudioListener(new AudioListener() {
                @Override
                public void onAudioSessionIdChanged(int audioSessionId) {
                    if (loudnessEnhancer != null) {
                        loudnessEnhancer.release();
                    }
                    try {
                        loudnessEnhancer = new LoudnessEnhancer(audioSessionId);
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
            });

            setTracks = true;

            updateLoading(true);

            play = mPrefs.getPosition() == 0L;
            player.setPlayWhenReady(play);

            player.seekTo(mPrefs.getPosition());

            player.setHandleAudioBecomingNoisy(true);
            mediaSession.setActive(true);
        } else {
            playerView.showController();
        }

        player.addListener(playbackStateListener);
        player.prepare();

        if (restorePlayState) {
            restorePlayState = false;
            playerView.showController();
            player.play();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            mediaSession.setActive(false);
            mediaSession.release();
            mPrefs.updateBrightness(mBrightnessControl.currentBrightnessLevel);
            mPrefs.updateOrientation();
            
            if (player.isPlaying()) {
                restorePlayState = true;
            }
            player.removeListener(playbackStateListener);
            player.clearMediaItems();
            player.release();
            player = null;
        }
    }

    private class PlaybackStateListener implements Player.EventListener{
        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            playerView.setKeepScreenOn(isPlaying);
            if (!isScrubbing) {
                if (isPlaying) {
                    playerView.setControllerShowTimeoutMs(CONTROLLER_TIMEOUT);
                } else {
                    playerView.setControllerShowTimeoutMs(-1);
                }
            }
        }

        @Override
        public void onPlaybackStateChanged(int state) {
            boolean isNearEnd = false;
            final long duration = player.getDuration();
            if (duration != C.TIME_UNSET) {
                final long position = player.getCurrentPosition();
                if (position + 4000 >= duration) {
                    isNearEnd = true;
                }
            }

            if (state == Player.STATE_READY) {
                frameRendered = true;
                final Format format = player.getVideoFormat();
                if (format != null) {
                    if (mPrefs.orientation == Utils.Orientation.VIDEO) {
                        if (Utils.isPortrait(format)) {
                            VideoPlayerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                        } else {
                            VideoPlayerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                        }
                    }
                }

                if (play) {
                    play = false;
                    playerView.hideController();
                }
            }

            if (setTracks && state == Player.STATE_READY) {
                setTracks = false;
                updateLoading(false);
                if (mPrefs.audioTrack != -1 && mPrefs.audioTrackFfmpeg != -1) {
                    setSelectedTrackAudio(mPrefs.audioTrack, false);
                    setSelectedTrackAudio(mPrefs.audioTrackFfmpeg, true);
                }
                if (mPrefs.subtitleTrack != -1 && (mPrefs.subtitleTrack < getTrackCountSubtitle() || mPrefs.subtitleTrack == Integer.MIN_VALUE))
                    setSelectedTrackSubtitle(mPrefs.subtitleTrack);
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            updateLoading(false);
            if (controllerVisible && controllerVisibleFully) {
                showError(error);
            } else {
                errorToShow = error;
            }
        }
    }

    private void enableRotation() {
        try {
            if (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 0) {
                Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
                restoreOrientationLock = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFile(Uri pickerInitialUri) {
        enableRotation();

        final Intent intent = createBaseFileIntent(Intent.ACTION_OPEN_DOCUMENT, pickerInitialUri);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");

        safelyStartActivityForResult(intent, REQUEST_CHOOSER_VIDEO);
    }

    private void loadSubtitleFile(Uri pickerInitialUri) {
        Toast.makeText(VideoPlayerActivity.this, R.string.open_subtitles, Toast.LENGTH_SHORT).show();
        enableRotation();

        final Intent intent = createBaseFileIntent(Intent.ACTION_OPEN_DOCUMENT, pickerInitialUri);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        final String[] supportedMimeTypes = {
                MimeTypes.APPLICATION_SUBRIP,
                MimeTypes.TEXT_SSA,
                MimeTypes.TEXT_VTT,
                MimeTypes.APPLICATION_TTML,
                "text/*",
                "application/octet-stream"
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes);

        safelyStartActivityForResult(intent, REQUEST_CHOOSER_SUBTITLE);
    }

    private void requestDirectoryAccess() {
        enableRotation();

        Uri initialUri = null;

        if (Build.VERSION.SDK_INT >= 26) {
            final String authority = "com.android.externalstorage.documents";
            final String documentId = "primary:" + Environment.DIRECTORY_MOVIES;
            initialUri = DocumentsContract.buildDocumentUri(authority, documentId);
        }

        final Intent intent = createBaseFileIntent(Intent.ACTION_OPEN_DOCUMENT_TREE, initialUri);

        safelyStartActivityForResult(intent, REQUEST_CHOOSER_SCOPE_DIR);
    }

    private Intent createBaseFileIntent(final String action, final Uri initialUri) {
        final Intent intent = new Intent(action);

        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);

        if (Build.VERSION.SDK_INT >= 26 && initialUri != null) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri);
        }

        return intent;
    }

    void safelyStartActivityForResult(final Intent intent, final int code) {
        if (intent.resolveActivity(getPackageManager()) == null)
            showSnack(getText(R.string.error_files_missing).toString(), intent.toString());
        else
            startActivityIfNeeded(intent, code);
    }

    public void setSelectedTrackSubtitle(final int trackIndex) {
        final MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            final DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
            final DefaultTrackSelector.ParametersBuilder parametersBuilder = parameters.buildUpon();
            for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                if (mappedTrackInfo.getRendererType(rendererIndex) == C.TRACK_TYPE_TEXT) {
                    if (trackIndex == Integer.MIN_VALUE) {
                        parametersBuilder.setRendererDisabled(rendererIndex, true);
                    } else {
                        parametersBuilder.setRendererDisabled(rendererIndex, false);
                        if (trackIndex == -1) {
                            parametersBuilder.clearSelectionOverrides(rendererIndex);
                        } else {
                            final int [] tracks = {0};
                            final DefaultTrackSelector.SelectionOverride selectionOverride = new DefaultTrackSelector.SelectionOverride(trackIndex, tracks);
                            parametersBuilder.setSelectionOverride(rendererIndex, mappedTrackInfo.getTrackGroups(rendererIndex), selectionOverride);
                        }
                    }
                }
            }
            trackSelector.setParameters(parametersBuilder);
        }
    }

    public int getSelectedTrackSubtitle() {
        if (trackSelector != null) {
            final MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                    if (mappedTrackInfo.getRendererType(rendererIndex) == C.TRACK_TYPE_TEXT) {
                        final TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
                        final DefaultTrackSelector.SelectionOverride selectionOverride = trackSelector.getParameters().getSelectionOverride(rendererIndex, trackGroups);
                        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
                        if (parameters.getRendererDisabled(rendererIndex)) {
                            return Integer.MIN_VALUE;
                        }
                        if (selectionOverride == null) {
                            return -1;
                        }
                        return selectionOverride.groupIndex;
                    }
                }
            }
        }
        return -1;
    }

    public int getSelectedTrackAudio(final boolean ffmpeg) {
        if (trackSelector != null) {
            final MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                    if (mappedTrackInfo.getRendererType(rendererIndex) == C.TRACK_TYPE_AUDIO) {
                        final String rendererName = mappedTrackInfo.getRendererName(rendererIndex);
                        if ((rendererName.toLowerCase().contains("ffmpeg") && !ffmpeg) ||
                                (!rendererName.toLowerCase().contains("ffmpeg") && ffmpeg))
                            continue;
                        final TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
                        final DefaultTrackSelector.SelectionOverride selectionOverride = trackSelector.getParameters().getSelectionOverride(rendererIndex, trackGroups);
                        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
                        if (parameters.getRendererDisabled(rendererIndex)) {
                            return Integer.MIN_VALUE;
                        }
                        if (selectionOverride == null) {
                            return -1;
                        }
                        return selectionOverride.groupIndex;
                    }
                }
            }
        }
        return -1;
    }

    public void setSelectedTrackAudio(final int trackIndex, final boolean ffmpeg) {
        final MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            final DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
            final DefaultTrackSelector.ParametersBuilder parametersBuilder = parameters.buildUpon();
            for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                if (mappedTrackInfo.getRendererType(rendererIndex) == C.TRACK_TYPE_AUDIO) {
                    final String rendererName = mappedTrackInfo.getRendererName(rendererIndex);
                    if ((rendererName.toLowerCase().contains("ffmpeg") && !ffmpeg) ||
                            (!rendererName.toLowerCase().contains("ffmpeg") && ffmpeg))
                        continue;
                    if (trackIndex == Integer.MIN_VALUE) {
                        parametersBuilder.setRendererDisabled(rendererIndex, true);
                    } else {
                        parametersBuilder.setRendererDisabled(rendererIndex, false);
                        if (trackIndex == -1) {
                            parametersBuilder.clearSelectionOverrides(rendererIndex);
                        } else {
                            final int [] tracks = {0};
                            final DefaultTrackSelector.SelectionOverride selectionOverride = new DefaultTrackSelector.SelectionOverride(trackIndex, tracks);
                            parametersBuilder.setSelectionOverride(rendererIndex, mappedTrackInfo.getTrackGroups(rendererIndex), selectionOverride);
                        }
                    }
                }
            }
            trackSelector.setParameters(parametersBuilder);
        }
    }

    public int getTrackCountSubtitle() {
        if (trackSelector != null) {
            final MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                    if (mappedTrackInfo.getRendererType(rendererIndex) == C.TRACK_TYPE_TEXT) {
                        final TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
                        return trackGroups.length;
                    }
                }
            }
        }
        return 0;
    }

    void setSubtitleTextSize() {
        setSubtitleTextSize(getResources().getConfiguration().orientation);
    }

    void setSubtitleTextSize(final int orientation) {
        // Tweak text size as fraction size doesn't work well in portrait
        final SubtitleView subtitleView = playerView.getSubtitleView();
        if (subtitleView != null) {
            final float size;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                size = SubtitleView.DEFAULT_TEXT_SIZE_FRACTION * subtitlesScale;
            } else {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                float ratio = ((float)metrics.heightPixels / (float)metrics.widthPixels);
                if (ratio < 1)
                    ratio = 1 / ratio;
                size = SubtitleView.DEFAULT_TEXT_SIZE_FRACTION * subtitlesScale / ratio;
            }

            subtitleView.setFractionalTextSize(size);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    void showError(ExoPlaybackException error) {
        final String errorGeneral = error.getLocalizedMessage();
        String errorDetailed;

        switch (error.type) {
            case ExoPlaybackException.TYPE_SOURCE:
                errorDetailed = error.getSourceException().getLocalizedMessage();
                break;
            case ExoPlaybackException.TYPE_RENDERER:
                errorDetailed = error.getRendererException().getLocalizedMessage();
                break;
            case ExoPlaybackException.TYPE_UNEXPECTED:
                errorDetailed = error.getUnexpectedException().getLocalizedMessage();
                break;
            case ExoPlaybackException.TYPE_REMOTE:
            default:
                errorDetailed = errorGeneral;
                break;
        }

        showSnack(errorGeneral, errorDetailed);
    }

    void showSnack(final String textPrimary, final String textSecondary) {
        snackbar = Snackbar.make(coordinatorLayout, textPrimary, Snackbar.LENGTH_LONG);
        if (textSecondary != null) {
            snackbar.setAction(R.string.error_details, v -> {
                final AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this);
                builder.setMessage(textSecondary);
                builder.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss());
                final AlertDialog dialog = builder.create();
                dialog.show();
            });
        }
    }

    void reportScrubbing(long position) {
        final long diff = position - scrubbingStart;
        if (Math.abs(diff) > 1000) {
            scrubbingNoticeable = true;
        }
        if (scrubbingNoticeable) {
            playerView.clearIcon();
            playerView.setCustomErrorMessage(Utils.formatMilisSign(diff));
        }
        if (frameRendered) {
            frameRendered = false;
            player.seekTo(position);
        }
    }

    void updateSubtitleStyle() {
        final CaptioningManager captioningManager = (CaptioningManager) getSystemService(Context.CAPTIONING_SERVICE);
        final SubtitleView subtitleView = playerView.getSubtitleView();
        if (!captioningManager.isEnabled()) {
            subtitlesScale = 1.05f;
            final CaptionStyleCompat captionStyle = new CaptionStyleCompat(Color.WHITE, Color.TRANSPARENT, Color.TRANSPARENT, CaptionStyleCompat.EDGE_TYPE_OUTLINE, Color.BLACK, Typeface.DEFAULT_BOLD);
            if (subtitleView != null) {
                subtitleView.setStyle(captionStyle);
                subtitleView.setApplyEmbeddedStyles(true);
            }
        } else {
            subtitlesScale = captioningManager.getFontScale();
            if (subtitleView != null) {
                subtitleView.setUserDefaultStyle();
                subtitleView.setApplyEmbeddedStyles(false);
            }
        }

        if (subtitleView != null)
            subtitleView.setBottomPaddingFraction(SubtitleView.DEFAULT_BOTTOM_PADDING_FRACTION * 2f / 3f);

        setSubtitleTextSize();
    }

    void askForScope() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this);
        builder.setMessage(String.format(getString(R.string.subtitles_scope), getString(R.string.app_name)));
        builder.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    requestDirectoryAccess();
                }
        );
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> mPrefs.markScopeAsked());
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    void resetHideCallbacks() {
        if (haveMedia && player.isPlaying()) {
            // Keep controller UI visible - alternative to resetHideCallbacks()
            playerView.setControllerShowTimeoutMs(VideoPlayerActivity.CONTROLLER_TIMEOUT);
        }
    }

    private void updateLoading(final boolean enableLoading) {
        if (enableLoading) {
            exoPlayPause.setVisibility(View.GONE);
            loadingProgressBar.setVisibility(View.VISIBLE);
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            exoPlayPause.setVisibility(View.VISIBLE);
        }
    }

    void setDeleteVisible(boolean visible) {
        final int visibility = (visible && haveMedia && Utils.isDeletable(this, Uri.parse(mVideoPath))) ? View.VISIBLE : View.GONE;
        findViewById(R.id.delete).setVisibility(visibility);
        findViewById(R.id.dummy).setVisibility(visibility);
    }

    void deleteMedia() {
        try {
            DocumentsContract.deleteDocument(getContentResolver(), Uri.parse(mVideoPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
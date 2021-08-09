package com.growmore.firebrowser.model;

public class AdModel {
    private boolean isInterstitialAds, isInterstitialAdsSplash, isInterstitialAdsDownload, isInterstitialAdsTrending, isInterstitialAdsSpin;
    private boolean isNativeAds, isNativeAdsHome, isNativeAdsVideo, isNativeAdsMp3, isNativeAdsExit, isBannerAdsSplash;
    private boolean isRepair, isShowingWhatsapp, isShowingSpin, isShowingGift, isShowingFrequency;
    private int keyFrequencyCount;
    private String whatsapp, appVersion, keyInterstitial, keyNative, keyBanner, keyRewardedVideo;

    public AdModel(){}
    public AdModel(boolean isInterstitialAds, boolean isNativeAds, String whatsapp, boolean isRepair,
                   boolean isInterstitialAdsSplash, boolean isInterstitialAdsDownload, boolean isInterstitialAdsTrending, boolean isInterstitialAdsSpin,
                   boolean isNativeAdsHome, boolean isNativeAdsVideo, boolean isNativeAdsMp3, boolean isNativeAdsExit, boolean isNativeAdsSplash,
                   boolean isShowingWhatsapp, boolean isShowingSpin, boolean isShowingGift, boolean isShowingFrequency, int keyFrequencyCount, String appVersion,
                   String keyInterstitial, String keyNative, String keyBanner, String keyRewardedVideo){
        this.isInterstitialAds = isInterstitialAds;
        this.isNativeAds = isNativeAds;
        this.whatsapp = whatsapp;
        this.isRepair = isRepair;
        this.isInterstitialAdsSplash = isInterstitialAdsSplash;
        this.isInterstitialAdsDownload = isInterstitialAdsDownload;
        this.isInterstitialAdsTrending = isInterstitialAdsTrending;
        this.isInterstitialAdsSpin = isInterstitialAdsSpin;
        this.isNativeAdsHome = isNativeAdsHome;
        this.isNativeAdsVideo = isNativeAdsVideo;
        this.isNativeAdsMp3 = isNativeAdsMp3;
        this.isNativeAdsExit = isNativeAdsExit;
        this.isBannerAdsSplash = isNativeAdsSplash;
        this.isShowingWhatsapp = isShowingWhatsapp;
        this.isShowingSpin = isShowingSpin;
        this.isShowingGift = isShowingGift;
        this.isShowingFrequency = isShowingFrequency;
        this.appVersion = appVersion;
        this.keyFrequencyCount = keyFrequencyCount;
        this.keyInterstitial = keyInterstitial;
        this.keyNative = keyNative;
        this.keyBanner = keyBanner;
        this.keyRewardedVideo = keyRewardedVideo;
    }

    public String getKeyRewardedVideo() {
        return keyRewardedVideo;
    }
    public void setKeyRewardedVideo(String keyRewardedVideo) {
        this.keyRewardedVideo = keyRewardedVideo;
    }
    public String getKeyInterstitial() {
        return keyInterstitial;
    }
    public void setKeyInterstitial(String keyInterstitial) {
        this.keyInterstitial = keyInterstitial;
    }
    public String getKeyBanner() {
        return keyBanner;
    }
    public void setKeyBanner(String keyBanner) {
        this.keyBanner = keyBanner;
    }
    public String getKeyNative() {
        return keyNative;
    }
    public void setKeyNative(String keyNative) {
        this.keyNative = keyNative;
    }
    public boolean isInterstitialAds() {
        return isInterstitialAds;
    }
    public boolean isNativeAds() {
        return isNativeAds;
    }
    public String getWhatsapp() {
        return whatsapp;
    }
    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }
    public void setNativeAds(boolean nativeAds) {
        isNativeAds = nativeAds;
    }
    public void setInterstitialAds(boolean interstitialAds) {
        isInterstitialAds = interstitialAds;
    }
    public boolean isInterstitialAdsSplash() {
        return isInterstitialAdsSplash;
    }
    public void setInterstitialAdsSplash(boolean interstitialAdsSplash) {
        this.isInterstitialAdsSplash = interstitialAdsSplash;
    }
    public boolean isInterstitialAdsDownload() {
        return isInterstitialAdsDownload;
    }
    public void setInterstitialAdsDownload(boolean interstitialAdsDownload) {
        isInterstitialAdsDownload = interstitialAdsDownload;
    }
    public boolean isInterstitialAdsTrending() {
        return isInterstitialAdsTrending;
    }
    public void setInterstitialAdsTrending(boolean interstitialAdsTrending) {
        isInterstitialAdsTrending = interstitialAdsTrending;
    }
    public boolean isInterstitialAdsSpin() {
        return isInterstitialAdsSpin;
    }
    public void setInterstitialAdsSpin(boolean interstitialAdsSpin) {
        isInterstitialAdsSpin = interstitialAdsSpin;
    }
    public boolean isNativeAdsExit() {
        return isNativeAdsExit;
    }
    public void setNativeAdsExit(boolean nativeAdsExit) {
        isNativeAdsExit = nativeAdsExit;
    }
    public boolean isNativeAdsHome() {
        return isNativeAdsHome;
    }
    public void setNativeAdsHome(boolean nativeAdsHome) {
        isNativeAdsHome = nativeAdsHome;
    }
    public boolean isNativeAdsMp3() {
        return isNativeAdsMp3;
    }
    public void setNativeAdsMp3(boolean nativeAdsMp3) {
        isNativeAdsMp3 = nativeAdsMp3;
    }
    public boolean isNativeAdsVideo() {
        return isNativeAdsVideo;
    }
    public void setNativeAdsVideo(boolean nativeAdsVideo) {
        isNativeAdsVideo = nativeAdsVideo;
    }
    public boolean isBannerAdsSplash() {
        return isBannerAdsSplash;
    }
    public void setBannerAdsSplash(boolean bannerAdsSplash) {
        isBannerAdsSplash = bannerAdsSplash;
    }
    public boolean isRepair() {
        return isRepair;
    }
    public void setRepair(boolean repair) {
        isRepair = repair;
    }
    public boolean isShowingWhatsapp() {
        return isShowingWhatsapp;
    }
    public void setShowingWhatsapp(boolean showingWhatsapp) {
        isShowingWhatsapp = showingWhatsapp;
    }
    public boolean isShowingSpin() {
        return isShowingSpin;
    }
    public void setShowingSpin(boolean showingSpin) {
        isShowingSpin = showingSpin;
    }
    public boolean isShowingGift() {
        return isShowingGift;
    }
    public void setShowingGift(boolean showingGift) {
        isShowingGift = showingGift;
    }
    public boolean isShowingFrequency() {
        return isShowingFrequency;
    }
    public void setShowingFrequency(boolean showingFrequency) {
        isShowingFrequency = showingFrequency;
    }
    public int getKeyFrequencyCount() {
        return keyFrequencyCount;
    }
    public void setKeyFrequencyCount(int keyFrequencyCount) {
        this.keyFrequencyCount = keyFrequencyCount;
    }
    public String getAppVersion() {
        return appVersion;
    }
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public static final String FIRE_isInterstitialAds = "isInterstitialAds";
    public static final String FIRE_isInterstitialAdsSplash = "isInterstitialAdsSplash";
    public static final String FIRE_isInterstitialAdsDownload = "isInterstitialAdsDownload";
    public static final String FIRE_isInterstitialAdsTrending = "isInterstitialAdsTrending";
    public static final String FIRE_isInterstitialAdsSpin = "isInterstitialAdsSpin";
    public static final String FIRE_isNativeAds = "isNativeAds";
    public static final String FIRE_isNativeAdsHome = "isNativeAdsHome";
    public static final String FIRE_isNativeAdsVideo = "isNativeAdsVideo";
    public static final String FIRE_isNativeAdsMp3 = "isNativeAdsMp3";
    public static final String FIRE_isNativeAdsExit = "isNativeAdsExit";
    public static final String FIRE_isBannerAdsSplash = "isBannerAdsSplash";
    public static final String FIRE_isRepair = "isRepair";
    public static final String FIRE_isShowingWhatsapp = "isShowingWhatsapp";
    public static final String FIRE_isShowingSpin = "isShowingSpin";
    public static final String FIRE_isShowingGift = "isShowingGift";
    public static final String FIRE_isShowingFrequency = "isShowingFrequency";
    public static final String FIRE_keyFrequencyCount = "keyFrequencyCount";
    public static final String FIRE_keyInterstitial = "keyInterstitial";
    public static final String FIRE_keyNative = "keyNative";
    public static final String FIRE_keyBanner = "keyBanner";
    public static final String FIRE_keyRewardedVideo = "keyRewardedVideo";
    public static final String FIRE_whatsapp = "whatsapp";
    public static final String FIRE_appVersion = "appVersion";
}

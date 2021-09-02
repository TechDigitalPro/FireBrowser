package com.growmore.firebrowser.model;

public class BrowserSettingModel {
    private String searchEngine, userAgent;
    private boolean clearQuit ,clearCache ,clearCookie ,clearHistory ,clearIndexedDB;
    private boolean isDesktop, isInvert, isCloseTab, isOpenLastTab, isIncognitoMode;
    private boolean autoFill, isJavaScript, isRemote, isCookie, isAdBlock, isImages, isCloseBrowser, isSaveData;

    public BrowserSettingModel(){
        searchEngine = "0";
        userAgent = "";
        clearQuit = clearCache = clearCookie = clearHistory = clearIndexedDB = false;
        isDesktop = isInvert = isCloseTab = isOpenLastTab = isIncognitoMode = false;
        autoFill = isJavaScript = isRemote = isCookie = isAdBlock = isImages = isCloseBrowser = isSaveData = true;
    }

    public String getSearchEngine() {
        return searchEngine;
    }
    public void setSearchEngine(String searchEngine) {
        this.searchEngine = searchEngine;
    }
    public String getUserAgent() {
        return userAgent;
    }
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    public boolean isClearQuit() {
        return clearQuit;
    }
    public void setClearQuit(boolean clearQuit) {
        this.clearQuit = clearQuit;
    }
    public boolean isClearCache() {
        return clearCache;
    }
    public void setClearCache(boolean clearCache) {
        this.clearCache = clearCache;
    }
    public boolean isClearCookie() {
        return clearCookie;
    }
    public void setClearCookie(boolean clearCookie) {
        this.clearCookie = clearCookie;
    }
    public boolean isClearHistory() {
        return clearHistory;
    }
    public void setClearHistory(boolean clearHistory) {
        this.clearHistory = clearHistory;
    }
    public boolean isClearIndexedDB() {
        return clearIndexedDB;
    }
    public void setClearIndexedDB(boolean clearIndexedDB) {
        this.clearIndexedDB = clearIndexedDB;
    }
    public boolean isDesktop() {
        return isDesktop;
    }
    public void setDesktop(boolean desktop) {
        isDesktop = desktop;
    }
    public boolean isInvert() {
        return isInvert;
    }
    public void setInvert(boolean invert) {
        isInvert = invert;
    }
    public boolean isCloseTab() {
        return isCloseTab;
    }
    public void setCloseTab(boolean closeTab) {
        isCloseTab = closeTab;
    }
    public boolean isOpenLastTab() {
        return isOpenLastTab;
    }
    public void setOpenLastTab(boolean openLastTab) {
        isOpenLastTab = openLastTab;
    }
    public boolean isIncognitoMode() {
        return isIncognitoMode;
    }
    public void setIncognitoMode(boolean incognitoMode) {
        isIncognitoMode = incognitoMode;
    }
    public boolean isAutoFill() {
        return autoFill;
    }
    public void setAutoFill(boolean autoFill) {
        this.autoFill = autoFill;
    }
    public boolean isJavaScript() {
        return isJavaScript;
    }
    public void setJavaScript(boolean javaScript) {
        isJavaScript = javaScript;
    }
    public boolean isRemote() {
        return isRemote;
    }
    public void setRemote(boolean remote) {
        isRemote = remote;
    }
    public boolean isCookie() {
        return isCookie;
    }
    public void setCookie(boolean cookie) {
        isCookie = cookie;
    }
    public boolean isAdBlock() {
        return isAdBlock;
    }
    public void setAdBlock(boolean adBlock) {
        isAdBlock = adBlock;
    }
    public boolean isCloseBrowser() {
        return isCloseBrowser;
    }
    public void setCloseBrowser(boolean closeBrowser) {
        isCloseBrowser = closeBrowser;
    }
    public boolean isImages() {
        return isImages;
    }
    public void setImages(boolean images) {
        isImages = images;
    }
    public boolean isSaveData() {
        return isSaveData;
    }
    public void setSaveData(boolean saveData) {
        isSaveData = saveData;
    }
}

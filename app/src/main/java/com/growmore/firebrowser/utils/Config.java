package com.growmore.firebrowser.utils;


import android.view.View;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.model.SearchEngineModel;
import com.growmore.firebrowser.model.SiteLinkModel;
import com.growmore.firebrowser.utils.browser.database.Record;
import com.growmore.firebrowser.utils.browser.unit.BrowserUnit;

import java.io.Serializable;
import java.util.ArrayList;

public class Config {
    public static final String SEARCH_TEXT = "SEARCH_TEXT";
    public static final String BROWSER_SHOW_FOR_RESULT_DATA = "BROWSER_SHOW_FOR_RESULT_DATA";
    public static final String BROWSER_SHOW_BOOKMARK_DATA = "BROWSER_SHOW_BOOKMARK_DATA";
    public static final String BROWSER_SHOW_HISTORY_DATA = "BROWSER_SHOW_BOOKMARK_DATA";

    public static final String BROWSER_VIDEO_MODE = "BROWSER_VIDEO_MODE";
    public static final String BROWSER_VIDEO_PRIVATE = "BROWSER_VIDEO_PRIVATE";
    public static final String BROWSER_VIDEO_PUBLIC = "BROWSER_VIDEO_PUBLIC";
    public static final String BROWSER_SETTING_MODEL = "BROWSER_SETTING_MODEL";
    public static final String DOWNLOAD_SETTING_MODEL = "DOWNLOAD_SETTING_MODEL";
    public static final String DOWNLOAD_VIDEO_MOVE = "DOWNLOAD_VIDEO_MOVE";
    public static final String VIDEO_SORT_BY = "VIDEO_SORT_BY";

    public static final String PLAYER_VIDEO_MODEL = "PLAYER_VIDEO_MODEL";

    public static final int SPEECH_REQUEST_CODE = 1000;
    public static final int BROWSER_SHOW_FOR_RESULT = 1001;
    public static final int BROWSER_SHOW_BOOKMARK = 1002;
    public static final int BROWSER_SHOW_HISTORY = 1003;
    public static final int BROWSER_SHOW_SETTING = 1004;
    public static final int BROWSER_SHOW_PLAYER = 1006;
    public static final int PERMISSION_READ_WRITE = 1005;
    public static final int FOLDER_PICKER_CODE = 1007;
    public static final int BROWSER_SETTING_DEFAULT = 1008;
    public static final String FIRE_PREF = "fire_";
    public static final String FIRE_EXT = ".mp4";

    public static ArrayList<View> openedTabList = new ArrayList<>();
    public static ArrayList<Integer> downloadingVideosList = new ArrayList<>();

    public static ArrayList<SiteLinkModel> getSiteList(){
        ArrayList<SiteLinkModel> list = new ArrayList<>();
        list.add(new SiteLinkModel(0, R.drawable.ic_google, R.string.txt_google, "https://www.google.com/"));
        list.add(new SiteLinkModel(1, R.drawable.ic_vimeo, R.string.txt_vimeo, "https://vimeo.com/watch"));
        list.add(new SiteLinkModel(2, R.drawable.ic_facebook, R.string.txt_facebook, "https://m.facebook.com/"));
        list.add(new SiteLinkModel(3, R.drawable.ic_instagram, R.string.txt_instagram, "https://www.instagram.com/"));
        list.add(new SiteLinkModel(4, R.drawable.ic_twitter, R.string.txt_twitter, "https://mobile.twitter.com/explore"));
        list.add(new SiteLinkModel(5, R.drawable.ic_daily_motion, R.string.txt_daily_motion, "https://www.dailymotion.com/th"));
        return list;
    }

    public static ArrayList<SearchEngineModel> getSearchEngineList(){
        ArrayList<SearchEngineModel> list = new ArrayList<>();
        list.add(new SearchEngineModel("0", "Google", "https://www.google.com/search?q="));
        list.add(new SearchEngineModel("1", "DuckDuckGo", "https://duckduckgo.com/?q="));
        list.add(new SearchEngineModel("2", "Start Page", "https://startpage.com/do/search?query="));
        list.add(new SearchEngineModel("3", "Bing", "https://www.bing.com/search?q="));
        list.add(new SearchEngineModel("4", "Baidu", "https://www.baidu.com/s?wd="));
        list.add(new SearchEngineModel("5", "Qwant", "https://www.qwant.com/?q="));
        list.add(new SearchEngineModel("6", "Ecosia", "https://www.ecosia.org/search?q="));
        list.add(new SearchEngineModel("7", "Metager", "https://metager.org/meta/meta.ger3?eingabe="));
        return list;
    }
}

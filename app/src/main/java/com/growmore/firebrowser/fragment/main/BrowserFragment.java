package com.growmore.firebrowser.fragment.main;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.activity.main.MainActivity;
import com.growmore.firebrowser.adapter.HomeBrowseSiteLinkAdapter;
import com.growmore.firebrowser.base.BaseFragment;
import com.growmore.firebrowser.model.SiteLinkModel;
import com.growmore.firebrowser.utils.Config;
import com.growmore.firebrowser.utils.MenuItemInformation;
import com.growmore.firebrowser.utils.browser.browser.AlbumController;
import com.growmore.firebrowser.utils.browser.browser.BrowserContainer;
import com.growmore.firebrowser.utils.browser.browser.BrowserController;
import com.growmore.firebrowser.utils.browser.browser.Cookie;
import com.growmore.firebrowser.utils.browser.browser.DOM;
import com.growmore.firebrowser.utils.browser.browser.Javascript;
import com.growmore.firebrowser.utils.browser.unit.BrowserUnit;
import com.growmore.firebrowser.utils.browser.view.NinjaWebView;
import com.growmore.firebrowser.utils.browser.view.SwipeTouchListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BrowserFragment extends BaseFragment implements BrowserController {
    private View mFragView;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.mainContent) FrameLayout mainContent;
    @BindView(R.id.homeContent) LinearLayout homeContent;
    @BindView(R.id.toolbarHeader) LinearLayout toolbarHeader;
    @BindView(R.id.toolbarBrowse) LinearLayout toolbarBrowse;
    @BindView(R.id.toolbarView) LinearLayout toolbarView;
    @BindView(R.id.toolbarBrowseBack) ImageView toolbarBrowseBack;
    @BindView(R.id.toolbarBrowseForward) ImageView toolbarBrowseForward;
    @BindView(R.id.toolbarBrowseSearchReload) ImageView toolbarBrowseSearchReload;
    @BindView(R.id.toolbarTabs) ImageView toolbarTabs;
    @BindView(R.id.toolbarMore) ImageView toolbarMore;
    @BindView(R.id.btVoice) ImageView btVoice;
    @BindView(R.id.mainSearch) TextView mainSearch;
    @BindView(R.id.toolbarBrowseSearch) AppCompatAutoCompleteTextView toolbarBrowseSearch;
    @BindView(R.id.lvCoverView) RelativeLayout lvCoverView;

    private MenuItemInformation itemBookmark, itemHistory, itemIncognito, itemShareWebpage, itemCopyLink, itemDesktopSite, itemSetting;
    private MenuItemInformation itemTranslate, itemShareApp, itemHelpFeedback, itemHowTo, itemExit;
    private PopupWindow popupWindowMenu;

    private HomeBrowseSiteLinkAdapter recyclerAdapter;
    private static final int SPEECH_REQUEST_CODE = 1000;
    private boolean isHome = true;
    private static final String SEARCH_STR = "SEARCH STR";
    private String searchStr;

    private NinjaWebView ninjaWebView;
    private AlbumController currentAlbumController = null;
    private SharedPreferences sp;
    private Javascript javaHosts;
    private Cookie cookieHosts;
    private DOM mDom;

    public BrowserFragment() {
    }

    public static BrowserFragment newInstance() {
        BrowserFragment fragment = new BrowserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && (getActivity()!=null)) {
            initView();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (!menuVisible){
            try {
                closePopupWindow();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragView = inflater.inflate(R.layout.fragment_browser, container, false);
        ButterKnife.bind(this, mFragView);
        initView();
        initRecycler();
        return mFragView;
    }


    private void initView() {
        toolbarHeader.setVisibility(View.VISIBLE);
        toolbarBrowse.setVisibility(View.GONE);
        homeContent.setVisibility(View.VISIBLE);
        mainContent.setVisibility(View.GONE);
        toolbarView.setBackgroundResource(R.color.transparent_color);
        ((MainActivity) getActivity()).setStatusView(true);
        isHome = true;
        lvCoverView.setVisibility(View.GONE);
    }

    //region --------------------------  Browser Home  ---------------------------//
    private void initRecycler() {
        ArrayList<SiteLinkModel> list = Config.getSiteList();
        recyclerAdapter = new HomeBrowseSiteLinkAdapter(getActivity(), list, model -> {

        });
        recyclerView.setAdapter(recyclerAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 5);
        recyclerView.setLayoutManager(layoutManager);
    }

    @OnClick(R.id.mainSearch)
    public void onClickSearchView() {
        toolbarHeader.setVisibility(View.GONE);
        toolbarBrowse.setVisibility(View.VISIBLE);
        homeContent.setVisibility(View.GONE);
        mainContent.setVisibility(View.VISIBLE);
        toolbarView.setBackgroundResource(R.color.colorPrimaryDark);
        ((MainActivity) getActivity()).setStatusView(false);
        isHome = false;
    }

    @OnClick(R.id.btVoice)
    public void onClickVoice() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {

        }
    }

    @OnClick(R.id.toolbarMore)
    public void onClickMenu() {
        showDropDown(toolbarMore, isHome);
    }

    @OnClick(R.id.lvCoverView)
    public void onClickCloseDropDown() {
        closePopupWindow();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (popupWindowMenu != null && popupWindowMenu.isShowing()) {
                        lvCoverView.setVisibility(View.GONE);
                        popupWindowMenu.dismiss();
                    } else {
                        if (!isHome) {
                            initView();
                        } else {
                            getActivity().onBackPressed();
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void closePopupWindow() {
        lvCoverView.setVisibility(View.GONE);
        if (popupWindowMenu != null && popupWindowMenu.isShowing()) {
            popupWindowMenu.dismiss();
        }
    }

    private void showDropDown(View view, boolean isHome) {
        View sortView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dropdown_browser_home, null);
        itemBookmark = sortView.findViewById(R.id.itemBookmark);
        itemHistory = sortView.findViewById(R.id.itemHistory);
        itemIncognito = sortView.findViewById(R.id.itemIncognito);
        itemShareWebpage = sortView.findViewById(R.id.itemShareWebpage);
        itemCopyLink = sortView.findViewById(R.id.itemCopyLink);
        itemDesktopSite = sortView.findViewById(R.id.itemDesktopSite);
        itemSetting = sortView.findViewById(R.id.itemSetting);
        itemTranslate = sortView.findViewById(R.id.itemTranslate);
        itemShareApp = sortView.findViewById(R.id.itemShareApp);
        itemHelpFeedback = sortView.findViewById(R.id.itemHelpFeedback);
        itemHowTo = sortView.findViewById(R.id.itemHowTo);
        itemExit = sortView.findViewById(R.id.itemExit);

        if (isHome) {
            itemShareWebpage.setVisibility(View.GONE);
            itemCopyLink.setVisibility(View.GONE);
            itemTranslate.setVisibility(View.GONE);
        }

        popupWindowMenu = new PopupWindow(sortView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindowMenu.setContentView(sortView);
        popupWindowMenu.setBackgroundDrawable(new BitmapDrawable());
        popupWindowMenu.setOutsideTouchable(true);
        popupWindowMenu.showAsDropDown(view, -100, 0);
        lvCoverView.setVisibility(View.VISIBLE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addAlbum(String title, final String url, final boolean foreground) {
        ninjaWebView = new NinjaWebView(getActivity());
        ninjaWebView.setBrowserController(this);
        ninjaWebView.setAlbumTitle(title);

        ninjaWebView.setOnScrollChangeListener((scrollY, oldScrollY) -> {

            if (scrollY == 0) {
                ninjaWebView.setOnTouchListener((new SwipeTouchListener(getActivity()) {
                    public void onSwipeBottom() {
                        if (ninjaWebView.getScrollY() == 0) {
                            ninjaWebView.reload();
                        }
                    }

                    public void onSwipeTop() {

                    }
                }));
            } else {
                ninjaWebView.setOnTouchListener(null);
            }
        });

        if (url.isEmpty() || url.equals("about:blank")) {
            ninjaWebView.loadData("about:blank", "", "");
        } else {
            ninjaWebView.loadUrl(url);
        }

        if (currentAlbumController != null) {
            int index = BrowserContainer.indexOf(currentAlbumController) + 1;
            BrowserContainer.add(ninjaWebView, index);
        } else {
            BrowserContainer.add(ninjaWebView);
        }

        if (!foreground) {
            ninjaWebView.deactivate();
        } else {
            ninjaWebView.activate();
            showAlbum(ninjaWebView);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                ninjaWebView.initPreferences(ninjaWebView.getUrl());
                ninjaWebView.reload();
            }
        }
    }

    @Override
    public void updateAutoComplete() {

    }

    @Override
    public void updateProgress(int progress) {

    }

    @Override
    public void showAlbum(AlbumController albumController) {
        View view = (View) albumController;

        if (currentAlbumController != null) {
            currentAlbumController.deactivate();
        }

        currentAlbumController = albumController;
        currentAlbumController.activate();

        mainContent.removeAllViews();
        mainContent.addView(view);
    }

    @Override
    public void removeAlbum(AlbumController albumController) {

    }

    @Override
    public void showFileChooser(ValueCallback<Uri[]> filePathCallback) {

    }

    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {

    }

    @Override
    public void hideTabView() {

    }

    @Override
    public void onHideCustomView() {

    }

    @Override
    public void onDestroy() {
        if (popupWindowMenu != null && popupWindowMenu.isShowing()) {
            popupWindowMenu.dismiss();
        }

        if (sp.getBoolean("sp_clear_quit", false)) {

            boolean clearCache = sp.getBoolean("sp_clear_cache", false);
            boolean clearCookie = sp.getBoolean("sp_clear_cookie", false);
            boolean clearHistory = sp.getBoolean("sp_clear_history", false);
            boolean clearIndexedDB = sp.getBoolean("sp_clearIndexedDB", false);

            if (clearCache) {
                BrowserUnit.clearCache(getActivity());
            }
            if (clearCookie) {
                BrowserUnit.clearCookie();
            }
            if (clearHistory) {
                BrowserUnit.clearHistory(getActivity());
            }
            if (clearIndexedDB) {
                BrowserUnit.clearIndexedDB(getActivity());
                WebStorage.getInstance().deleteAllData();
            }
        }
        BrowserContainer.clear();
        super.onDestroy();
    }
}
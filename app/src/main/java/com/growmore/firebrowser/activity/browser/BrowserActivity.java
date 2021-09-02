package com.growmore.firebrowser.activity.browser;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.preference.PreferenceManager;

import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.growmore.firebrowser.R;
import com.growmore.firebrowser.activity.main.FeedbackActivity;
import com.growmore.firebrowser.adapter.TabBrowserAdapter;
import com.growmore.firebrowser.application.App;
import com.growmore.firebrowser.base.BaseActivity;
import com.growmore.firebrowser.dialog.DialogHowToDownload;
import com.growmore.firebrowser.model.BrowserSettingModel;
import com.growmore.firebrowser.model.EventBadgeCount;
import com.growmore.firebrowser.model.VideoModel;
import com.growmore.firebrowser.service.NotificationService;
import com.growmore.firebrowser.utils.CommonUtils;
import com.growmore.firebrowser.utils.Config;
import com.growmore.firebrowser.utils.MenuItemInformation;
import com.growmore.firebrowser.utils.MovableFloatingActionButton;
import com.growmore.firebrowser.utils.ShareUtils;
import com.growmore.firebrowser.utils.browser.browser.AdBlock;
import com.growmore.firebrowser.utils.browser.browser.AlbumController;
import com.growmore.firebrowser.utils.browser.browser.BrowserContainer;
import com.growmore.firebrowser.utils.browser.browser.BrowserController;
import com.growmore.firebrowser.utils.browser.browser.Cookie;
import com.growmore.firebrowser.utils.browser.browser.DOM;
import com.growmore.firebrowser.utils.browser.browser.Javascript;
import com.growmore.firebrowser.utils.browser.database.FaviconHelper;
import com.growmore.firebrowser.utils.browser.database.Record;
import com.growmore.firebrowser.utils.browser.database.RecordAction;
import com.growmore.firebrowser.utils.browser.unit.BrowserUnit;
import com.growmore.firebrowser.utils.browser.unit.HelperUnit;
import com.growmore.firebrowser.utils.browser.unit.RecordUnit;
import com.growmore.firebrowser.utils.browser.view.NinjaToast;
import com.growmore.firebrowser.utils.browser.view.NinjaWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BrowserActivity extends BaseActivity implements BrowserController {
    @BindView(R.id.statusView)
    RelativeLayout statusView;
    @BindView(R.id.lvBottomImageBrowse)
    ImageView lvBottomImageBrowse;
    @BindView(R.id.lvBottomImageDownload)
    ImageView lvBottomImageDownload;
    @BindView(R.id.lvBottomImageCompleted)
    ImageView lvBottomImageCompleted;
    @BindView(R.id.lvBottomImageVpn)
    ImageView lvBottomImageVpn;
    @BindView(R.id.lvBottomTextBrowse)
    TextView lvBottomTextBrowse;
    @BindView(R.id.lvBottomTextDownload)
    TextView lvBottomTextDownload;
    @BindView(R.id.lvBottomTextCompleted)
    TextView lvBottomTextCompleted;
    @BindView(R.id.lvBottomTextVpn)
    TextView lvBottomTextVpn;
    @BindView(R.id.lvBottomActiveBrowse)
    RelativeLayout lvBottomActiveBrowse;
    @BindView(R.id.lvBottomActiveDownload)
    RelativeLayout lvBottomActiveDownload;
    @BindView(R.id.lvBottomActiveCompleted)
    RelativeLayout lvBottomActiveCompleted;
    @BindView(R.id.lvBottomActiveVpn)
    RelativeLayout lvBottomActiveVpn;

    @BindView(R.id.toolbarTabs)
    ImageView toolbarTabs;
    @BindView(R.id.toolbarTabSingle)
    ImageView toolbarTabSingle;
    @BindView(R.id.toolbarTabsCount)
    TextView toolbarTabsCount;
    @BindView(R.id.toolbarTabBtn)
    RelativeLayout toolbarTabBtn;
    @BindView(R.id.toolbarMore)
    ImageView toolbarMore;
    @BindView(R.id.toolbarBrowseSearchReload)
    ImageView toolbarBrowseSearchReload;
    @BindView(R.id.toolbarBrowseBack)
    ImageView toolbarBrowseBack;
    @BindView(R.id.toolbarBrowseForward)
    ImageView toolbarBrowseForward;
    @BindView(R.id.toolbarBrowseSearch)
    AppCompatAutoCompleteTextView toolbarBrowseSearch;
    @BindView(R.id.btDownload)
    MovableFloatingActionButton btDownload;
    @BindView(R.id.tvBadgeDownload)
    TextView tvBadgeDownload;

    @BindView(R.id.mainContent)
    FrameLayout mainContent;
    @BindView(R.id.lvCoverView)
    RelativeLayout lvCoverView;

    private MenuItemInformation itemBookmark, itemHistory, itemIncognito, itemShareWebpage, itemCopyLink, itemDesktopSite, itemSetting;
    private MenuItemInformation itemTranslate, itemShareApp, itemHelpFeedback, itemHowTo, itemExit;
    private PopupWindow popupWindowMenu;

    private ImageView btTabDelete, btTabAdd, btTabBack;
    private DragDropSwipeRecyclerView tabRecyclerView;
    private TabBrowserAdapter tabRecyclerAdapter;
    private LinearLayout tab_container;
    private View tabView;

    private String searchText = "";
    private NinjaWebView ninjaWebView;
    private AlbumController currentAlbumController = null;
    private SharedPreferences sp;
    private Javascript javaHosts;
    private Cookie cookieHosts;
    private DOM mDom;
    private KeyListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        lvCoverView.setVisibility(View.GONE);
        onClickBottomHome();

        searchText = getIntent().getStringExtra(Config.SEARCH_TEXT);
        if (getIntent().getData() != null) {
            searchText = getIntent().getData().toString();
        }

        toolbarBrowseSearch.setText(searchText);
        new AdBlock(this);
        new Javascript(this);
        new Cookie(this);
        new DOM(this);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        initSearch();
        initDownloadButton();


        //tabRecyclerAdapter = new TabBrowserAdapter(this);
        tabView = LayoutInflater.from(this).inflate(R.layout.layout_popup_tab_test, null);
        setUpTabView();

        addAlbum(getString(R.string.app_name), searchText, true);
    }

    private void initDownloadButton() {
        btDownload.setVisibility(View.VISIBLE);
        btDownload.setOnClickListener(v -> {
            if (App.readDownloadSetting().isWifiOnly()) {
                if (!CommonUtils.getWifiStatus(BrowserActivity.this)) {
                    showMessages(getString(R.string.txt_you_wanted_download_on_wifi));
                    return;
                }
            }
            VideoModel model = new VideoModel();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            String timeStamp = sdf.format(new Date());
            String file = Config.FIRE_PREF + timeStamp;
            String fileName = file + Config.FIRE_EXT;
            model.setTitle(fileName);

            Random random = new Random();
            int randomNum = random.nextInt((3 - 1) + 1) + 1;
            String num = randomNum * 10 + "";

            model.setOnlineUrl(("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"/*"https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_"+ num + "MB.mp4"*/));
            model.setThumbUrl(("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg"));
            model.setDuration(CommonUtils.getFormatDurationFromMilli(60000));
            model.setId(timeStamp);
            model.setTime(timeStamp);
            model.setPrivate(App.readDownloadSetting().isPrivate());
            model.setNew(true);

            App.downloadVideo(model);
            startService(new Intent(BrowserActivity.this, NotificationService.class));
        });
    }

    @Override
    public void onBackPressed() {
        if (popupWindowMenu != null && popupWindowMenu.isShowing()) {
            lvCoverView.setVisibility(View.GONE);
            popupWindowMenu.dismiss();
        } else {
            if (ninjaWebView.canGoBack()) {
                WebBackForwardList mWebBackForwardList = ninjaWebView.copyBackForwardList();
                String historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
                ninjaWebView.initPreferences(historyUrl);
                ninjaWebView.goBack();
            } else {
                removeAlbum(currentAlbumController);
            }
        }

    }

    @OnClick(R.id.lvBottomBrowse)
    public void onClickBottomHome() {
        clearBottomBarStyle();
        statusView.setVisibility(View.VISIBLE);
        lvBottomImageBrowse.setImageResource(R.drawable.ic_bottom_browse_active);
        lvBottomTextBrowse.setTextColor(getResources().getColor(R.color.active_text_color));
        lvBottomActiveBrowse.setVisibility(View.VISIBLE);
    }

    private void clearBottomBarStyle() {
        lvBottomImageBrowse.setImageResource(R.drawable.ic_bottom_browse_inactive);
        lvBottomImageDownload.setImageResource(R.drawable.ic_bottom_download_inactive);
        lvBottomImageCompleted.setImageResource(R.drawable.ic_bottom_complete_inactive);
        lvBottomImageVpn.setImageResource(R.drawable.ic_bottom_vpn_inactive);
        lvBottomTextBrowse.setTextColor(getResources().getColor(R.color.inactive_text_color));
        lvBottomTextDownload.setTextColor(getResources().getColor(R.color.inactive_text_color));
        lvBottomTextCompleted.setTextColor(getResources().getColor(R.color.inactive_text_color));
        lvBottomTextVpn.setTextColor(getResources().getColor(R.color.inactive_text_color));
        lvBottomActiveBrowse.setVisibility(View.INVISIBLE);
        lvBottomActiveDownload.setVisibility(View.INVISIBLE);
        lvBottomActiveCompleted.setVisibility(View.INVISIBLE);
        lvBottomActiveVpn.setVisibility(View.INVISIBLE);
    }

    public void onFinishResult(int id) {
        Intent intent = new Intent();
        intent.putExtra(Config.BROWSER_SHOW_FOR_RESULT_DATA, id);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(0, 0);
        try {
            if (id == 0) {
                CommonUtils.lastUrl = "";
            } else {
                if (ninjaWebView != null && !TextUtils.isEmpty(ninjaWebView.getUrl())) {
                    CommonUtils.lastUrl = ninjaWebView.getUrl();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.lvBottomDownload)
    public void onClickBottomDownload() {
        onFinishResult(1);
    }

    @OnClick(R.id.lvBottomCompleted)
    public void onClickBottomCompleted() {
        onFinishResult(2);
    }

    @OnClick(R.id.lvBottomVpn)
    public void onClickBottomVpn() {
        onFinishResult(3);
    }

    @OnClick(R.id.toolbarBrowseBack)
    public void onClickBack() {
        showAlbum(nextAlbumController(false));
    }

    @OnClick(R.id.toolbarBrowseForward)
    public void onClickForward() {
        showAlbum(nextAlbumController(true));
    }

    @OnClick(R.id.toolbarMore)
    public void onClickMenu() {
        showDropDown(toolbarMore);
    }

    @OnClick(R.id.toolbarTabBtn)
    public void onClickTab() {
        showTabView();
    }


    @OnClick(R.id.toolbarBrowseSearchReload)
    public void onClickReload() {
        initView();
    }

    @OnClick(R.id.lvCoverView)
    public void onClickClose() {
        closePopupWindow();
    }

    private void setUpTabView() {
        btTabDelete = tabView.findViewById(R.id.btTabDelete);
        btTabAdd = tabView.findViewById(R.id.btTabAdd);
        btTabBack = tabView.findViewById(R.id.btTabBack);
        btTabBack.setOnClickListener(v -> {
            popupWindowMenu.dismiss();
            lvCoverView.setVisibility(View.GONE);
        });
        btTabAdd.setOnClickListener(v -> {
            addAlbum(getString(R.string.app_name), "", true);
        });
        btTabDelete.setOnClickListener(v -> {
            onFinishResult(0);
        });
        tab_container = tabView.findViewById(R.id.tab_container);
    }

    private void showTabView() {
//        tabRecyclerView = tabView.findViewById(R.id.tabRecyclerView);
//        tabRecyclerAdapter.setDataSet(Config.openedTabList);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
//        tabRecyclerView.setLayoutManager(gridLayoutManager);
//        tabRecyclerView.setOrientation(DragDropSwipeRecyclerView.ListOrientation.GRID_LIST_WITH_HORIZONTAL_SWIPING);
//        tabRecyclerView.setReduceItemAlphaOnSwiping(true);
//        tabRecyclerView.setLongPressToStartDragging(true);
//        tabRecyclerView.setAdapter(tabRecyclerAdapter);
        popupWindowMenu = new PopupWindow(tabView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindowMenu.setWidth(statusView.getWidth());
        popupWindowMenu.setContentView(tabView);
        popupWindowMenu.setBackgroundDrawable(new BitmapDrawable());
        popupWindowMenu.setOutsideTouchable(true);
        popupWindowMenu.showAsDropDown(statusView);
        lvCoverView.setVisibility(View.VISIBLE);
    }

    private void closePopupWindow() {
        lvCoverView.setVisibility(View.GONE);
        if (popupWindowMenu != null && popupWindowMenu.isShowing()) {
            popupWindowMenu.dismiss();
        }
    }

    private void showDropDown(View view) {
        View sortView = LayoutInflater.from(this).inflate(R.layout.layout_dropdown_browser_home, null);
        LinearLayout lvSortHome = sortView.findViewById(R.id.lvSortHome);
        LinearLayout lvSortBrowser = sortView.findViewById(R.id.lvSortBrowser);

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

        itemDesktopSite.setSwChecked(App.readBrowserSetting().isDesktop());
        itemIncognito.setSwChecked(App.readBrowserSetting().isIncognitoMode());

        lvSortBrowser.setVisibility(View.VISIBLE);
        lvSortHome.setVisibility(View.GONE);

        ImageView btAddBookmark = sortView.findViewById(R.id.btAddBookmark);
        ImageView btHome = sortView.findViewById(R.id.btHome);

        btAddBookmark.setOnClickListener(v -> {
            saveBookmark();
            closePopupWindow();
        });
        btHome.setOnClickListener(v -> {
            closePopupWindow();
        });
        itemBookmark.setOnClickListener(v -> {
            Intent intent = new Intent(BrowserActivity.this, BookmarkActivity.class);
            startActivityForResult(intent, Config.BROWSER_SHOW_BOOKMARK);
            closePopupWindow();
        });
        itemHistory.setOnClickListener(v -> {
            Intent intent = new Intent(BrowserActivity.this, HistoryActivity.class);
            startActivityForResult(intent, Config.BROWSER_SHOW_HISTORY);
            closePopupWindow();
        });
        itemShareWebpage.setOnClickListener(v -> {
            shareLink();
            closePopupWindow();
        });
        itemCopyLink.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", ninjaWebView.getUrl());
            Objects.requireNonNull(clipboard).setPrimaryClip(clip);
            NinjaToast.show(this, R.string.toast_copy_successful);
            closePopupWindow();
        });
        itemIncognito.setmActionListener(new MenuItemInformation.ActionListener() {
            @Override
            public void onCheckedListener(boolean isChecked) {
                BrowserSettingModel model = App.readBrowserSetting();
                model.setIncognitoMode(isChecked);
                App.saveBrowserSetting(model);
                if (isChecked) {
                    BrowserUnit.clearCache(BrowserActivity.this);
                    BrowserUnit.clearHistory(BrowserActivity.this);
                    BrowserUnit.clearCookie();
                }
            }
        });
        itemDesktopSite.setmActionListener(new MenuItemInformation.ActionListener() {
            @Override
            public void onCheckedListener(boolean isChecked) {
                BrowserSettingModel model = App.readBrowserSetting();
                model.setDesktop(isChecked);
                App.saveBrowserSetting(model);

                ninjaWebView.toggleDesktopMode(isChecked);
                closePopupWindow();
            }
        });
        itemSetting.setOnClickListener(v -> {
            Intent intent = new Intent(BrowserActivity.this, BrowseSettingActivity.class);
            startActivityForResult(intent, Config.BROWSER_SHOW_SETTING);
            closePopupWindow();
        });
        itemShareApp.setOnClickListener(v -> {
            ShareUtils.shareApp(BrowserActivity.this);
            closePopupWindow();
        });
        itemHelpFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(BrowserActivity.this, FeedbackActivity.class);
            startActivity(intent);
            closePopupWindow();
        });
        itemHowTo.setOnClickListener(v -> {
            DialogHowToDownload download = new DialogHowToDownload(this);
            download.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            download.setCancelable(true);
            download.show();
            closePopupWindow();
        });
        itemExit.setOnClickListener(v -> {
            try {
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        popupWindowMenu = new PopupWindow(sortView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindowMenu.setContentView(sortView);
        popupWindowMenu.setBackgroundDrawable(new BitmapDrawable());
        popupWindowMenu.setOutsideTouchable(true);
        popupWindowMenu.showAsDropDown(view);
        lvCoverView.setVisibility(View.VISIBLE);
    }

    private void addAlbum(String title, final String url, final boolean foreground) {
        ninjaWebView = new NinjaWebView(this);
        ninjaWebView.setBrowserController(this);
        ninjaWebView.setAlbumTitle(title);
        registerForContextMenu(ninjaWebView);

        ninjaWebView.setOnScrollChangeListener((scrollY, oldScrollY) -> {

        });

        if (TextUtils.isEmpty(url) || url.equals("about:blank")) {
            String id = App.readBrowserSetting().getSearchEngine();
            String _url = "";
            for (int i = 0; i < Config.getSearchEngineList().size(); i++) {
                if (Config.getSearchEngineList().get(i).getId().equalsIgnoreCase(id)) {
                    _url = Config.getSearchEngineList().get(i).getLink();
                    break;
                }
            }
            ninjaWebView.loadUrl(_url);
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

        /*Config.openedTabList.add(ninjaWebView.getAlbumView());
        if (tabRecyclerAdapter != null){
            tabRecyclerAdapter.setDataSet(Config.openedTabList);
        }*/
        View albumView = ninjaWebView.getAlbumView();
        tab_container.addView(albumView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        updateToolbarTab();
        updateToolbar();
    }

    private void updateToolbarTab() {
        if (tab_container == null) return;
        if (tab_container.getChildCount() > 1) {
            toolbarTabSingle.setVisibility(View.GONE);
            toolbarTabs.setVisibility(View.VISIBLE);
        } else {
            toolbarTabSingle.setVisibility(View.VISIBLE);
            toolbarTabs.setVisibility(View.GONE);
        }
        toolbarTabsCount.setText(String.valueOf(tab_container.getChildCount()));
    }

    private void initSearch() {
        listener = toolbarBrowseSearch.getKeyListener();
        toolbarBrowseSearch.setKeyListener(null);
        toolbarBrowseSearch.setEllipsize(TextUtils.TruncateAt.START);
        toolbarBrowseSearch.setOnEditorActionListener((v, actionId, event) -> {
            String query = toolbarBrowseSearch.getText().toString().trim();
            ninjaWebView.loadUrl(query);
            return false;
        });
        toolbarBrowseSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (toolbarBrowseSearch.hasFocus()) {
                String url = ninjaWebView.getUrl();
                ninjaWebView.stopLoading();
                toolbarBrowseSearch.setKeyListener(listener);

                if (TextUtils.isEmpty(url)) {
                    toolbarBrowseSearch.setText("");
                    return;
                }

                if (url.contains("about:blank")) {
                    toolbarBrowseSearch.setText("");
                } else {
                    toolbarBrowseSearch.setText(url);
                }
                toolbarBrowseSearch.selectAll();
            } else {
                toolbarBrowseSearch.setKeyListener(null);
                toolbarBrowseSearch.setEllipsize(TextUtils.TruncateAt.END);
                toolbarBrowseSearch.setText(ninjaWebView.getTitle());
                updateToolbar();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showBadgeView(EventBadgeCount evt) {
        try {
            tvBadgeDownload.setText(String.valueOf(evt.getCount()));
            if (evt.getCount() > 0) {
                tvBadgeDownload.setVisibility(View.VISIBLE);
            } else {
                tvBadgeDownload.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateToolbar() {
        toolbarBrowseSearch.clearFocus();

        ninjaWebView = (NinjaWebView) currentAlbumController;
        this.cookieHosts = new Cookie(this);
        CookieManager manager = CookieManager.getInstance();

        String url = ninjaWebView.getUrl();
        if (url != null) {

            if (cookieHosts.isWhite(url) || App.readBrowserSetting().isCookie()) {
                manager.setAcceptCookie(true);
                manager.getCookie(url);
            } else {
                manager.setAcceptCookie(false);
            }

            if (Objects.requireNonNull(ninjaWebView.getTitle()).isEmpty()) {
                toolbarBrowseSearch.setText(url);
            } else {
                toolbarBrowseSearch.setText(ninjaWebView.getTitle());
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
        if (view.getParent() != null) {
            ViewGroup parentViewGroup = (ViewGroup) view.getParent();
            parentViewGroup.removeAllViews();
        }
        mainContent.addView(view);

        updateToolbar();
        HelperUnit.initRendering(ninjaWebView, this);
    }

    @Override
    public void removeAlbum(AlbumController albumController) {
        if (BrowserContainer.size() <= 1) {
            onFinishResult(0);
        } else {
            tab_container.removeView(albumController.getAlbumView());
            updateToolbarTab();

            int index = BrowserContainer.indexOf(albumController);
            BrowserContainer.remove(albumController);
            if (index >= BrowserContainer.size()) {
                index = BrowserContainer.size() - 1;
            }
            if (BrowserContainer.get(index) != null) {
                showAlbum(BrowserContainer.get(index));
            }
        }
    }

    private void closeTabConfirmation(final Runnable okAction) {
        if (!App.readBrowserSetting().isCloseBrowser()) {
            okAction.run();
        } else {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setMessage(R.string.toast_quit);
            builder.setPositiveButton(R.string.app_ok, (dialog, whichButton) -> okAction.run());
            builder.setNegativeButton(R.string.app_cancel, (dialog, whichButton) -> dialog.cancel());
            AlertDialog dialog = builder.create();
            dialog.show();
            Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.BOTTOM);
        }
    }

    private void shareLink() {
        try {
            String title = ninjaWebView.getTitle();
            String url = ninjaWebView.getUrl();

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, url);
            startActivity(Intent.createChooser(sharingIntent, (getString(R.string.menu_share_link))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveBookmark() {
        FaviconHelper faviconHelper = new FaviconHelper(this);
        faviconHelper.addFavicon(ninjaWebView.getUrl(), ninjaWebView.getFavicon());
        RecordAction action = new RecordAction(this);
        action.open(true);
        if (action.checkUrl(ninjaWebView.getUrl(), RecordUnit.TABLE_BOOKMARK)) {
            NinjaToast.show(this, R.string.app_error);
        } else {

            // Bookmark time is used for color, desktop mode, javascript, and DOM content
            // bit 0..3  color
            // bit 4: 1 = Desktop Mode
            // bit 5: 0 = JavaScript (0 due backward compatibility)
            // bit 6: 0 = DOM Content allowed (0 due to backward compatibility)

            long value = 11 + (long) (ninjaWebView.isDesktopMode() ? 16 : 0) + (long) (ninjaWebView.getSettings().getJavaScriptEnabled() ? 0 : 32) + (long) (ninjaWebView.getSettings().getDomStorageEnabled() ? 0 : 64);
            action.addBookmark(new Record(ninjaWebView.getTitle(), ninjaWebView.getUrl(), value, 0, 2));

            NinjaToast.show(this, R.string.app_done);
        }
        action.close();
    }

    @Override
    public void showFileChooser(ValueCallback<Uri[]> filePathCallback) {

    }

    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {

    }

    @Override
    public void hideTabView() {
        closePopupWindow();
    }

    @Override
    public void onHideCustomView() {

    }

    private AlbumController nextAlbumController(boolean next) {
        if (BrowserContainer.size() <= 1) {
            return currentAlbumController;
        }
        List<AlbumController> list = BrowserContainer.list();
        int index = list.indexOf(currentAlbumController);
        if (next) {
            index++;
            if (index >= list.size()) {
                index = 0;
            }
        } else {
            index--;
            if (index < 0) {
                index = list.size() - 1;
            }
        }
        return list.get(index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Config.BROWSER_SHOW_BOOKMARK) {
                int position = data.getIntExtra(Config.BROWSER_SHOW_BOOKMARK_DATA, 0);

                RecordAction action = new RecordAction(this);
                action.open(false);
                final List<Record> list;
                list = action.listBookmark(this, false, 0);
                action.close();

                if (((list.get(position).getTime() & 16) == 16) != ninjaWebView.isDesktopMode()) ninjaWebView.toggleDesktopMode(false);
                ninjaWebView.setJavaScript(!((list.get(position).getTime() & 32) == 32));
                ninjaWebView.setDomStorage(!((list.get(position).getTime() & 64) == 64));
                ninjaWebView.setOldDomain(list.get(position).getURL());
                ninjaWebView.loadUrl(list.get(position).getURL());
                toolbarBrowseSearch.setText(list.get(position).getURL());
            } else if (requestCode == Config.BROWSER_SHOW_HISTORY) {
                int position = data.getIntExtra(Config.BROWSER_SHOW_HISTORY_DATA, 0);

                RecordAction action = new RecordAction(this);
                action.open(false);
                final List<Record> list;
                list = action.listHistory();
                action.close();
                ninjaWebView.loadUrl(list.get(position).getURL());
                toolbarBrowseSearch.setText(list.get(position).getURL());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
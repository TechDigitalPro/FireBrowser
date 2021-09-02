package com.growmore.firebrowser.fragment.main;

import static android.app.Activity.RESULT_OK;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.activity.browser.BookmarkActivity;
import com.growmore.firebrowser.activity.browser.BrowseSettingActivity;
import com.growmore.firebrowser.activity.browser.BrowserActivity;
import com.growmore.firebrowser.activity.browser.HistoryActivity;
import com.growmore.firebrowser.activity.main.FeedbackActivity;
import com.growmore.firebrowser.activity.main.MainActivity;
import com.growmore.firebrowser.adapter.HomeBrowseSiteLinkAdapter;
import com.growmore.firebrowser.application.App;
import com.growmore.firebrowser.base.BaseFragment;
import com.growmore.firebrowser.dialog.DialogHowToDownload;
import com.growmore.firebrowser.model.BrowserSettingModel;
import com.growmore.firebrowser.model.SiteLinkModel;
import com.growmore.firebrowser.utils.Config;
import com.growmore.firebrowser.utils.MenuItemInformation;
import com.growmore.firebrowser.utils.ShareUtils;
import com.growmore.firebrowser.utils.browser.database.Record;
import com.growmore.firebrowser.utils.browser.database.RecordAction;
import com.growmore.firebrowser.utils.browser.unit.BrowserUnit;
import com.growmore.firebrowser.utils.browser.view.NinjaToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BrowserHomeFragment extends BaseFragment {
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


    private boolean isHome = true;

    public BrowserHomeFragment() { }

    public static BrowserHomeFragment newInstance() {
        BrowserHomeFragment fragment = new BrowserHomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (!menuVisible && getActivity() != null){
            try {
                closePopupWindow();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragView = inflater.inflate(R.layout.fragment_browser_home, container, false);
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
        ((MainActivity)getActivity()).setStatusView(true);
        isHome = true;
        lvCoverView.setVisibility(View.GONE);


    }

    private void initRecycler(){
        ArrayList<SiteLinkModel> list = Config.getSiteList();
        recyclerAdapter = new HomeBrowseSiteLinkAdapter(getActivity(), list, model -> {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(Config.SEARCH_TEXT, model.getLink());
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, Config.BROWSER_SHOW_FOR_RESULT);
            getActivity().overridePendingTransition(0,0);
        });
        recyclerView.setAdapter(recyclerAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 5);
        recyclerView.setLayoutManager(layoutManager);
    }

    @OnClick(R.id.mainSearch)
    public void onClickSearchView(){
        /*toolbarHeader.setVisibility(View.GONE);
        toolbarBrowse.setVisibility(View.VISIBLE);
        homeContent.setVisibility(View.GONE);
        mainContent.setVisibility(View.VISIBLE);
        toolbarView.setBackgroundResource(R.color.colorPrimaryDark);
        ((MainActivity)getActivity()).setStatusView(false);
        isHome = false;*/
        gotoBrowserActivity("");
    }

    public void gotoBrowserActivity(String url){
        Intent intent = new Intent(getActivity(), BrowserActivity.class);
        intent.putExtra(Config.SEARCH_TEXT, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, Config.BROWSER_SHOW_FOR_RESULT);
        getActivity().overridePendingTransition(0,0);
    }

    @OnClick(R.id.btVoice)
    public void onClickVoice(){
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(intent, Config.SPEECH_REQUEST_CODE);
        }catch (Exception e){

        }
    }

    @OnClick(R.id.toolbarMore)
    public void onClickMenu(){
        showDropDown(toolbarMore, isHome);
    }

    @OnClick(R.id.lvCoverView)
    public void onClickCloseDropDown(){
        closePopupWindow();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if (requestCode == Config.SPEECH_REQUEST_CODE) {
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String spokenText = results.get(0);
                gotoBrowserPage(spokenText);
            }else if (requestCode == Config.BROWSER_SHOW_FOR_RESULT){
                int tabId = data.getIntExtra(Config.BROWSER_SHOW_FOR_RESULT_DATA, 0);
                ((MainActivity)getActivity()).onClickTab(tabId);
            }else if (requestCode == Config.BROWSER_SHOW_BOOKMARK){
                int position = data.getIntExtra(Config.BROWSER_SHOW_BOOKMARK_DATA, 0);

                RecordAction action = new RecordAction(getActivity());
                action.open(false);
                final List<Record> list;
                list = action.listBookmark(getActivity(), false, 0);
                action.close();

                gotoBrowserPage(list.get(position).getURL());
            } else if (requestCode == Config.BROWSER_SHOW_HISTORY){
                int position = data.getIntExtra(Config.BROWSER_SHOW_HISTORY_DATA, 0);

                RecordAction action = new RecordAction(getActivity());
                action.open(false);
                final List<Record> list;
                list = action.listHistory();
                action.close();

                gotoBrowserPage(list.get(position).getURL());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( popupWindowMenu!=null && popupWindowMenu.isShowing() ){
            popupWindowMenu.dismiss();
        }
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
                    }else {
                        if (!isHome){
                            initView();
                        }else {
                            getActivity().onBackPressed();
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void gotoBrowserPage(String text){
        Intent intent = new Intent(getActivity(), BrowserActivity.class);
        intent.putExtra(Config.SEARCH_TEXT, text);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, Config.BROWSER_SHOW_FOR_RESULT);
        getActivity().overridePendingTransition(0,0);
    }

    private void closePopupWindow() {
        lvCoverView.setVisibility(View.GONE);
        if (popupWindowMenu != null && popupWindowMenu.isShowing()) {
            popupWindowMenu.dismiss();
        }
    }

    private void showDropDown(View view, boolean isHome){
        View sortView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dropdown_browser_home, null);
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


            lvSortBrowser.setVisibility(View.GONE);
            lvSortHome.setVisibility(View.VISIBLE);

        itemBookmark.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BookmarkActivity.class);
            startActivityForResult(intent, Config.BROWSER_SHOW_BOOKMARK);
            closePopupWindow();
        });
        itemHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HistoryActivity.class);
            startActivityForResult(intent, Config.BROWSER_SHOW_HISTORY);
            closePopupWindow();
        });
        itemIncognito.setmActionListener(new MenuItemInformation.ActionListener() {
            @Override
            public void onCheckedListener(boolean isChecked) {
                BrowserSettingModel model = App.readBrowserSetting();
                model.setIncognitoMode(isChecked);
                App.saveBrowserSetting(model);
                if (isChecked){
                    BrowserUnit.clearCache(getActivity());
                    BrowserUnit.clearHistory(getActivity());
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
                closePopupWindow();
            }
        });
        itemSetting.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BrowseSettingActivity.class);
            startActivityForResult(intent, Config.BROWSER_SHOW_SETTING);
            closePopupWindow();
        });
        itemShareApp.setOnClickListener(v -> {
            ShareUtils.shareApp(getActivity());
            closePopupWindow();
        });
        itemHelpFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FeedbackActivity.class);
            startActivity(intent);
            closePopupWindow();
        });
        itemHowTo.setOnClickListener(v -> {
            DialogHowToDownload download = new DialogHowToDownload(getActivity());
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
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        popupWindowMenu = new PopupWindow(sortView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindowMenu.setContentView(sortView);
        popupWindowMenu.setBackgroundDrawable(new BitmapDrawable());
        popupWindowMenu.setOutsideTouchable(true);
        popupWindowMenu.showAsDropDown(view, 0, 0);
        lvCoverView.setVisibility(View.VISIBLE);
    }
}
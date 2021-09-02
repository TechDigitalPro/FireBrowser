package com.growmore.firebrowser.fragment.main;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.growmore.firebrowser.R;
import com.growmore.firebrowser.activity.browser.BrowserActivity;
import com.growmore.firebrowser.activity.download.DownloadSettingsActivity;
import com.growmore.firebrowser.activity.main.SelectDownloadPathActivity;
import com.growmore.firebrowser.activity.main.VideoPlayerActivity;
import com.growmore.firebrowser.adapter.CompletedVideoAdapter;
import com.growmore.firebrowser.adapter.MainActivityPagerAdapter;
import com.growmore.firebrowser.adapter.MultiSelectableVideoAdapter;
import com.growmore.firebrowser.base.BaseFragment;
import com.growmore.firebrowser.db.DBController;
import com.growmore.firebrowser.dialog.DialogMove;
import com.growmore.firebrowser.dialog.DialogRename;
import com.growmore.firebrowser.fragment.completed.FavoritesFragment;
import com.growmore.firebrowser.fragment.completed.VideosFragment;
import com.growmore.firebrowser.model.CompletedVideoListDTO;
import com.growmore.firebrowser.model.EventSortType;
import com.growmore.firebrowser.model.MultiSelectableVideoListDTO;
import com.growmore.firebrowser.model.VideoModel;
import com.growmore.firebrowser.utils.CommonUtils;
import com.growmore.firebrowser.utils.Config;
import com.growmore.firebrowser.utils.DialogUtils;
import com.growmore.firebrowser.utils.GsonUtils;
import com.growmore.firebrowser.utils.MenuItemInformation;
import com.growmore.firebrowser.utils.RtlViewPager;
import com.growmore.firebrowser.utils.ShareUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CompletedFragment extends BaseFragment {
    private View mFragView;

    @BindView(R.id.viewPager) RtlViewPager viewPager;
    @BindView(R.id.toolbarSearch) ImageView toolbarSearch;
    @BindView(R.id.toolbarSettings) ImageView toolbarSettings;
    @BindView(R.id.toolbarMore) ImageView toolbarMore;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.lvCoverView) RelativeLayout lvCoverView;
    @BindView(R.id.lvBadgeNew) LinearLayout lvBadgeNew;
    @BindView(R.id.tvBadgeNew) TextView tvBadgeNew;

    @BindView(R.id.lvSearch) LinearLayout lvSearch;
    @BindView(R.id.searchBar) AppCompatAutoCompleteTextView searchBar;
    @BindView(R.id.tvSearchResult) TextView tvSearchResult;
    @BindView(R.id.btSearchClear) ImageView btSearchClear;
    @BindView(R.id.btSearchClose) ImageView btSearchClose;
    @BindView(R.id.recyclerViewSearch) RecyclerView recyclerViewSearch;

    @BindView(R.id.lvDelete) LinearLayout lvDelete;
    @BindView(R.id.tvDeleteSelected) TextView tvDeleteSelected;
    @BindView(R.id.btDelete) ImageView btDelete;
    @BindView(R.id.btDeleteClose) ImageView btDeleteClose;
    @BindView(R.id.chkDeleteAll) CheckBox chkDeleteAll;
    @BindView(R.id.recyclerViewDelete) RecyclerView recyclerViewDelete;

    @BindView(R.id.lvMove) LinearLayout lvMove;
    @BindView(R.id.tvMoveSelected) TextView tvMoveSelected;
    @BindView(R.id.btMove) ImageView btMove;
    @BindView(R.id.btMoveClose) ImageView btMoveClose;
    @BindView(R.id.chkMoveAll) CheckBox chkMoveAll;
    @BindView(R.id.recyclerViewMove) RecyclerView recyclerViewMove;

    private MultiSelectableVideoAdapter recyclerAdapter;
    private CompletedVideoAdapter recyclerSearchAdapter;
    private ArrayList<MultiSelectableVideoListDTO> mTempList = new ArrayList<>();

    private Fragment mVideoFragment, mFavoriteFragment/*, mOtherFragment*/;
    private MainActivityPagerAdapter viewPagerAdapter;
    private PopupWindow popupWindowMenu;
    private MenuItemInformation itemDelete, itemMove, itemSort;
    private int sortType = 0;

    public CompletedFragment() { }

    public static CompletedFragment newInstance() {
        CompletedFragment fragment = new CompletedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        if (!menuVisible && getActivity() != null){
            try {
                closePopupWindow();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if (menuVisible && getActivity()!= null){
            showBadgeView();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragView = inflater.inflate(R.layout.fragment_completed, container, false);
        ButterKnife.bind(this, mFragView);
        //initView();
        return mFragView;
    }

    private void initView() {
        lvCoverView.setVisibility(View.GONE);
        lvBadgeNew.setVisibility(View.GONE);

        mVideoFragment = VideosFragment.newInstance();
        mFavoriteFragment = FavoritesFragment.newInstance();
        viewPagerAdapter = new MainActivityPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFrag(mVideoFragment, getString(R.string.txt_videos));
        viewPagerAdapter.addFrag(mFavoriteFragment, getString(R.string.txt_favorites));
        //mOtherFragment = OthersFragment.newInstance();
        //viewPagerAdapter.addFrag(mOtherFragment, getString(R.string.txt_other));
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager, true);
        for (int i = 0; i < viewPagerAdapter.getCount(); i++){
            tabLayout.getTabAt(i).setText(viewPagerAdapter.getPageTitle(i));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        try {
            tabLayout.getTabAt(0).select();
        }catch (Exception e){
            e.printStackTrace();
        }
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
                    }
                    if (lvSearch.getVisibility() == View.VISIBLE){
                        lvSearch.setVisibility(View.GONE);
                    }
                    if (lvDelete.getVisibility() == View.VISIBLE){
                        lvDelete.setVisibility(View.GONE);
                    }
                    if (lvMove.getVisibility() == View.VISIBLE){
                        lvMove.setVisibility(View.GONE);
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

    private void showDropDown(){
        View sortView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dropdown_completed, null);
        itemDelete = sortView.findViewById(R.id.itemDelete);
        itemMove = sortView.findViewById(R.id.itemMove);
        itemSort = sortView.findViewById(R.id.itemSort);
        LinearLayout lvSortBy = sortView.findViewById(R.id.lvSortBy);
        LinearLayout lvByDate = sortView.findViewById(R.id.lvByDate);
        LinearLayout lvByName = sortView.findViewById(R.id.lvByName);
        LinearLayout lvBySize = sortView.findViewById(R.id.lvBySize);
        ImageView chkByDate = sortView.findViewById(R.id.chkByDate);
        ImageView chkByName = sortView.findViewById(R.id.chkByName);
        ImageView chkBySize = sortView.findViewById(R.id.chkBySize);
        if (sortType == 0){
            chkByDate.setVisibility(View.VISIBLE);
        }else if (sortType == 1){
            chkByName.setVisibility(View.VISIBLE);
        }else if (sortType == 2){
            chkBySize.setVisibility(View.VISIBLE);
        }

        itemDelete.setOnClickListener(v -> {
            showDeleteView();
        });

        itemMove.setOnClickListener(v -> {
            showMoveView();
        });

        lvByDate.setOnClickListener(v -> {
            chkByDate.setVisibility(View.VISIBLE);
            chkByName.setVisibility(View.INVISIBLE);
            chkBySize.setVisibility(View.INVISIBLE);

            sortType = 0;
            EventBus.getDefault().post(new EventSortType(0));
            closePopupWindow();
        });
        lvByName.setOnClickListener(v -> {
            chkByDate.setVisibility(View.INVISIBLE);
            chkByName.setVisibility(View.VISIBLE);
            chkBySize.setVisibility(View.INVISIBLE);

            sortType = 1;
            EventBus.getDefault().post(new EventSortType(1));
            closePopupWindow();
        });
        lvBySize.setOnClickListener(v -> {
            chkByDate.setVisibility(View.INVISIBLE);
            chkByName.setVisibility(View.INVISIBLE);
            chkBySize.setVisibility(View.VISIBLE);

            sortType = 2;
            EventBus.getDefault().post(new EventSortType(2));
            closePopupWindow();
        });

        itemSort.setOnClickListener(v -> {
            if (lvSortBy.getVisibility() == View.VISIBLE){
                lvSortBy.setVisibility(View.GONE);
            }else {
                lvSortBy.setVisibility(View.VISIBLE);
            }
        });

        popupWindowMenu = new PopupWindow(sortView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindowMenu.setContentView(sortView);
        popupWindowMenu.setBackgroundDrawable(new BitmapDrawable());
        popupWindowMenu.setOutsideTouchable(true);
        popupWindowMenu.showAsDropDown(toolbarMore, 0, 0);
        lvCoverView.setVisibility(View.VISIBLE);

        ((VideosFragment) mVideoFragment).onClickCover();
        ((FavoritesFragment) mFavoriteFragment).onClickCover();
        //((OthersFragment) mOtherFragment).onClickCover();
    }
    public void finishedDownload(String video_id) {
        try {
            tabLayout.getTabAt(0).select();
            ((VideosFragment) mVideoFragment).finishedDownload(video_id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnClick(R.id.toolbarSettings)
    public void onClickSettings(){
        Intent intent = new Intent(getActivity(), DownloadSettingsActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.toolbarMore)
    public void onClickMore(){
        showDropDown();
    }

    @OnClick(R.id.lvCoverView)
    public void onClickCover(){
        closePopupWindow();
    }

    public void showBadgeView(){
        ArrayList<VideoModel> tempList = DBController.shared().getAllVideoData();
        int count = 0;
        for (VideoModel model:tempList){
            if (model.isNew()){
                count++;
            }
        }
        if (count > 0){
            lvBadgeNew.setVisibility(View.VISIBLE);
            tvBadgeNew.setText(String.valueOf(count));
        }else {
            lvBadgeNew.setVisibility(View.GONE);
        }
    }

    //region ------------------------------------------- SearchView ----------------------------------------------------//
    @OnClick(R.id.toolbarSearch)
    public void onClickSearchBtn(){
        lvSearch.setVisibility(View.VISIBLE);
        searchBar.setText("");
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchBar.getText().toString().trim();
            return false;
        });
        searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable searchText) {
                if (recyclerSearchAdapter != null){
                    recyclerSearchAdapter.listFilter(searchText.toString());
                    tvSearchResult.setText(getString(R.string.txt_search_result_d, recyclerSearchAdapter.getItemCount()));
                }
            }
        });
        initRecyclerView(0);
    }

    @OnClick(R.id.btSearchClose)
    public void onClickSearchClose(){
        lvSearch.setVisibility(View.GONE);
    }
    @OnClick(R.id.btSearchClear)
    public void onClickSearchClear(){
        searchBar.setText("");
    }
    //endregion ------------------------------------------- SearchView ----------------------------------------------------//

    //region ------------------------------------------- DeleteView ----------------------------------------------------//
    public void showDeleteView(){
        closePopupWindow();
        lvDelete.setVisibility(View.VISIBLE);
        mTempList.clear();
        initRecyclerView(1);
    }
    @OnClick(R.id.btDeleteClose)
    public void onClickDeleteClose(){
        lvDelete.setVisibility(View.GONE);
        initView();
    }
    @OnClick(R.id.btDelete)
    public void onClickDelete(){
        if (mTempList.size() == 0){
            showMessages(getString(R.string.txt_please_select_at_least_1_item));
        }else {
            DialogUtils.showConfirmDialogWithListener(getActivity(), getString(R.string.txt_delete_selected_items), getString(R.string.txt_delete_these_items, mTempList.size()), getString(R.string.txt_delete), getString(R.string.txt_cancel),
                    (dialog, which) -> {
                        for (int i = 0; i < mTempList.size(); i++){
                            DBController.shared().deleteVideoData(mTempList.get(i).getVideoModel());
                            CommonUtils.deleteVideo(mTempList.get(i).getVideoModel());
                        }
                        dialog.dismiss();
                        showDeleteView();
                    },
                    (dialog, which) -> {
                        dialog.dismiss();
                    });
        }
    }
    @OnClick(R.id.chkDeleteAll)
    public void onDeleteAllChecked(){
        if (recyclerAdapter != null){
            ArrayList<MultiSelectableVideoListDTO> _list = new ArrayList<>();
            _list.addAll(recyclerAdapter.getData());
            if (chkDeleteAll.isChecked()){
                for (MultiSelectableVideoListDTO data : _list){
                    data.setChecked(true);
                }
            }else {
                for (MultiSelectableVideoListDTO data : _list){
                    data.setChecked(false);
                }
            }
            recyclerAdapter.setData(_list);
        }
    }
    //endregion ------------------------------------------- DeleteView ----------------------------------------------------//

    //region ------------------------------------------- MoveView ----------------------------------------------------//
    public void showMoveView(){
        closePopupWindow();
        lvMove.setVisibility(View.VISIBLE);
        mTempList.clear();
        initRecyclerView(2);
    }
    @OnClick(R.id.btMoveClose)
    public void onClickMoveClose(){
        lvMove.setVisibility(View.GONE);
    }
    @OnClick(R.id.btMove)
    public void onClickMove(){
        if (mTempList.size() == 0){
            showMessages(getString(R.string.txt_please_select_at_least_1_item));
        }else {
            Intent intent = new Intent(getActivity(), SelectDownloadPathActivity.class);
            startActivityForResult(intent, Config.FOLDER_PICKER_CODE);
        }

    }
    @OnClick(R.id.chkMoveAll)
    public void onMoveAllChecked(){
        if (recyclerAdapter != null){
            ArrayList<MultiSelectableVideoListDTO> _list = new ArrayList<>();
            _list.addAll(recyclerAdapter.getData());
            if (chkMoveAll.isChecked()){
                for (MultiSelectableVideoListDTO data : _list){
                    data.setChecked(true);
                }
            }else {
                for (MultiSelectableVideoListDTO data : _list){
                    data.setChecked(false);
                }
            }
            recyclerAdapter.setData(_list);
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == Config.FOLDER_PICKER_CODE) {
            if (resultCode == Activity.RESULT_OK && intent.hasExtra(Config.DOWNLOAD_VIDEO_MOVE)) {
                String folderLocation = intent.getExtras().getString(Config.DOWNLOAD_VIDEO_MOVE);
                if (TextUtils.isEmpty(folderLocation)) return;
                String finalFolderLocation = folderLocation + File.separator;
                DialogUtils.showConfirmDialogWithListener(getActivity(), getString(R.string.txt_move_selected_items), getString(R.string.txt_move_these_items, mTempList.size()), getString(R.string.txt_move), getString(R.string.txt_cancel),
                        (dialog, which) -> {
                            ArrayList<File> fileArrayList = new ArrayList<>();
                            for (int i = 0; i < mTempList.size(); i++){
                                fileArrayList.add(new File(mTempList.get(i).getVideoModel().getOfflineUrl()));

                                VideoModel model = mTempList.get(i).getVideoModel();
                                model.setOfflineUrl(finalFolderLocation + model.getTitle());
                                DBController.shared().updateVideo(model);
                            }
                            dialog.dismiss();

                            DialogMove dialogMove = new DialogMove(getActivity());
                            dialogMove.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogMove.setCancelable(false);
                            dialogMove.show();
                            CommonUtils.moveFiles(fileArrayList, new File(finalFolderLocation));
                        },
                        (dialog, which) -> {
                            dialog.dismiss();
                        });
            }
        }
    }
    //endregion ------------------------------------------- MoveView ----------------------------------------------------//

    private void initRecyclerView(int type){
        // 0:Search, 1:Delete, 2:Move
        RecyclerView.ItemAnimator animator = null;

        ArrayList<VideoModel> tempList = DBController.shared().getAllVideoData();
        ArrayList<VideoModel> videoList = new ArrayList<>();
        switch (type){
            case 0:
                recyclerSearchAdapter = new CompletedVideoAdapter(getActivity(), new ArrayList<>(), completedVideoListener);
                videoList.addAll(tempList);
                tvSearchResult.setText(getString(R.string.txt_search_result_d, videoList.size()));
                animator = recyclerViewSearch.getItemAnimator();
                recyclerViewSearch.setAdapter(recyclerSearchAdapter);
                recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getActivity()));
                break;
            case 1:
                recyclerAdapter = new MultiSelectableVideoAdapter(getActivity(), new ArrayList<>(), multiSelectableVideoListener);
                videoList.addAll(tempList);
                tvDeleteSelected.setText(getString(R.string.txt_d_selected, 0));
                animator = recyclerViewDelete.getItemAnimator();
                recyclerViewDelete.setAdapter(recyclerAdapter);
                recyclerViewDelete.setLayoutManager(new LinearLayoutManager(getActivity()));
                break;
            case 2:
                recyclerAdapter = new MultiSelectableVideoAdapter(getActivity(), new ArrayList<>(), multiSelectableVideoListener);
                for (VideoModel model:tempList){
                    if (!model.isPrivate()){
                        videoList.add(model);
                    }
                }
                tvMoveSelected.setText(getString(R.string.txt_d_selected, 0));
                animator = recyclerViewMove.getItemAnimator();
                recyclerViewMove.setAdapter(recyclerAdapter);
                recyclerViewMove.setLayoutManager(new LinearLayoutManager(getActivity()));
                break;
        }
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        if (type == 0){
            ArrayList<CompletedVideoListDTO> list = new ArrayList<>();
            for (int i = 0; i < videoList.size(); i++){
                CompletedVideoListDTO dto = new CompletedVideoListDTO();
                dto.setTime(videoList.get(i).getTime());
                dto.setVideoModel(videoList.get(i));
                dto.setType(CompletedVideoListDTO.ITEM_TYPE.IS_VIDEO);
                list.add(dto);
            }

            recyclerSearchAdapter.setData(list);
        }else {
            ArrayList<MultiSelectableVideoListDTO> list = new ArrayList<>();
            for (int i = 0; i < videoList.size(); i++){
                MultiSelectableVideoListDTO dto = new MultiSelectableVideoListDTO();
                dto.setTime(videoList.get(i).getTime());
                dto.setVideoModel(videoList.get(i));
                dto.setType(MultiSelectableVideoListDTO.ITEM_TYPE.IS_VIDEO);
                dto.setChecked(false);
                list.add(dto);
            }

            recyclerAdapter.setData(list);
        }


    }

    MultiSelectableVideoAdapter.MultiSelectableVideoListener multiSelectableVideoListener = new MultiSelectableVideoAdapter.MultiSelectableVideoListener() {
        @Override
        public void onItemClicked(VideoModel model, int pos) {
            recyclerAdapter.onItemChecked(pos);
            mTempList.clear();
            for (int i = 0; i < recyclerAdapter.getData().size(); i++){
                if (recyclerAdapter.getData().get(i).isChecked()){
                    mTempList.add(recyclerAdapter.getData().get(i));
                }
            }
            tvDeleteSelected.setText(getString(R.string.txt_d_selected, mTempList.size()));
            tvMoveSelected.setText(getString(R.string.txt_d_selected, mTempList.size()));
        }

        @Override
        public void onItemChecked(VideoModel model, int pos) {
            mTempList.clear();
            for (int i = 0; i < recyclerAdapter.getData().size(); i++){
                if (recyclerAdapter.getData().get(i).isChecked()){
                    mTempList.add(recyclerAdapter.getData().get(i));
                }
            }
            tvDeleteSelected.setText(getString(R.string.txt_d_selected, mTempList.size()));
            tvMoveSelected.setText(getString(R.string.txt_d_selected, mTempList.size()));
        }
    };
    CompletedVideoAdapter.CompletedVideoListener completedVideoListener = new CompletedVideoAdapter.CompletedVideoListener() {
        @Override
        public void onItemClicked(VideoModel model, int pos) {
            recyclerSearchAdapter.onItemNewClicked(pos);
            model.setNew(false);
            DBController.shared().updateVideo(model);

            Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
            intent.putExtra(Config.PLAYER_VIDEO_MODEL, GsonUtils.getInstance().toJson(model));
            startActivityForResult(intent, Config.BROWSER_SHOW_PLAYER);
        }

        @Override
        public void onItemMore(VideoModel model, int pos) {
            lvCoverView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onItemFavorite(VideoModel model, int pos) {
            if (recyclerSearchAdapter != null){
                recyclerSearchAdapter.onItemFavoriteClicked(pos);
            }
            DBController.shared().updateVideo(model);
        }

        @Override
        public void onItemShare(VideoModel model, int pos) {
            ShareUtils.shareVideo(getActivity(), model.getOfflineUrl());
            onClickCover();
        }

        @Override
        public void onItemRename(VideoModel model, int pos) {
            DialogRename dialog = new DialogRename(getActivity(), model, model1 -> {
                recyclerSearchAdapter.onItemRename(model1, pos);
                DBController.shared().updateVideo(model1);
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
            dialog.show();
            onClickCover();
        }

        @Override
        public void onItemDelete(VideoModel model, int pos) {
            DBController.shared().deleteVideoData(model);
            recyclerSearchAdapter.onItemRemoved(pos);
            CommonUtils.deleteVideo(model);
            onClickCover();
        }

        @Override
        public void onItemGoWebsite(VideoModel model, int pos) {
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra(Config.SEARCH_TEXT, model.getOnlineUrl());
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, Config.BROWSER_SHOW_FOR_RESULT);
            getActivity().overridePendingTransition(0,0);
            onClickCover();
        }

        @Override
        public void onItemRepost(VideoModel model, int pos) {
            if(CommonUtils.isPackageInstalled(getActivity(), "com.instagram.android")) {
                ShareUtils.createInstagramIntent(getActivity(), model.getOfflineUrl());
            }else{
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.instagram.android")));
                } catch (android.content.ActivityNotFoundException exception) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.instagram.android")));
                }
            }
            onClickCover();
        }
    };
}
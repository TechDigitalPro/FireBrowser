package com.growmore.firebrowser.fragment.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.growmore.firebrowser.R;
import com.growmore.firebrowser.activity.download.DownloadSettingsActivity;
import com.growmore.firebrowser.activity.main.MainActivity;
import com.growmore.firebrowser.activity.main.VideoPlayerActivity;
import com.growmore.firebrowser.adapter.DownloadVideoAdapter;
import com.growmore.firebrowser.base.BaseFragment;
import com.growmore.firebrowser.dialog.DialogHowToDownload;
import com.growmore.firebrowser.model.VideoModel;
import com.growmore.firebrowser.utils.Config;
import com.growmore.firebrowser.utils.GsonUtils;
import com.growmore.firebrowser.utils.MenuItemInformation;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownloadFragment extends BaseFragment {
    private View mFragView;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.lvBlankView) LinearLayout lvBlankView;
    @BindView(R.id.lvCoverView) RelativeLayout lvCoverView;
    @BindView(R.id.toolbarMore) ImageView toolbarMore;

    private PopupWindow popupWindowMenu;
    private MenuItemInformation itemPauseAll, itemResumeAll, itemDeleteBatch;
    private DownloadVideoAdapter recyclerAdapter;
    private ArrayList<VideoModel> mList = new ArrayList<>();

    public DownloadFragment() { }

    public static DownloadFragment newInstance() {
        DownloadFragment fragment = new DownloadFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragView = inflater.inflate(R.layout.fragment_download, container, false);
        ButterKnife.bind(this, mFragView);
        //initView();
        return mFragView;
    }

    private void initView() {
        lvCoverView.setVisibility(View.GONE);
        lvBlankView.setVisibility(View.VISIBLE);
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        recyclerAdapter = new DownloadVideoAdapter(getActivity(), mList, new DownloadVideoAdapter.DownloadVideoListener() {
            @Override
            public void onStartPauseClicked(VideoModel model, int pos) {
                if (model.getDownload_status() == 4){
                    PRDownloader.resume(model.getDownloadId());
                    startDownload(model.getId(), model);
                }else if (model.getDownload_status() == 1){
                    PRDownloader.pause(model.getDownloadId());
                    pauseDownload(model.getId());
                }
            }

            @Override
            public void onItemClicked(VideoModel model, int pos) {
                if (model.getDownload_status() == 3) {
                    Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                    intent.putExtra(Config.PLAYER_VIDEO_MODEL, GsonUtils.getInstance().toJson(model));
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                    return true;
                }
                return false;
            }
        });
    }

    private void closePopupWindow() {
        if (popupWindowMenu != null && popupWindowMenu.isShowing()) {
            popupWindowMenu.dismiss();
        }
        lvCoverView.setVisibility(View.GONE);
    }

    private void showDropDown(){
        View sortView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dropdown_download, null);
        itemPauseAll = sortView.findViewById(R.id.itemPauseAll);
        itemResumeAll = sortView.findViewById(R.id.itemResumeAll);
        itemDeleteBatch = sortView.findViewById(R.id.itemDeleteBatch);

        itemPauseAll.setOnClickListener(v -> {
            for (int i = 0; i < mList.size(); i++){
                PRDownloader.pause(mList.get(i).getDownloadId());
            }
            closePopupWindow();
        });

        itemResumeAll.setOnClickListener(v -> {
            for (int i = 0; i < mList.size(); i++){
                PRDownloader.resume(mList.get(i).getDownloadId());
            }
            closePopupWindow();
        });

        itemDeleteBatch.setOnClickListener(v -> {
            for (int i = 0; i < mList.size(); i++){
                PRDownloader.cancel(mList.get(i).getDownloadId());
            }
            mList.clear();
            closePopupWindow();
            initView();
        });

        popupWindowMenu = new PopupWindow(sortView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindowMenu.setContentView(sortView);
        popupWindowMenu.setBackgroundDrawable(new BitmapDrawable());
        popupWindowMenu.setOutsideTouchable(true);
        popupWindowMenu.showAsDropDown(toolbarMore, 0, 0);
        lvCoverView.setVisibility(View.VISIBLE);
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

    @OnClick(R.id.btHowToDownload)
    public void onClickHowToDownload(){
        DialogHowToDownload download = new DialogHowToDownload(getActivity());
        download.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        download.setCancelable(true);
        download.show();
        closePopupWindow();
    }

    public void addRecyclerItem(VideoModel model){
        boolean contains = false;
        for (int i = 0; i < mList.size(); i++){
            if (mList.get(i).getId().equalsIgnoreCase(model.getId())){
                contains = true;
                break;
            }
        }
        if (!contains){
            mList.add(model);
            recyclerAdapter.addItem(model);
            ((MainActivity) getActivity()).showBadgeView( mList.size());
        }

        lvBlankView.setVisibility(View.GONE);
    }

    public void startDownload(String video_id, VideoModel model) {
        if (recyclerAdapter != null) {
            addRecyclerItem(model);
            recyclerAdapter.startDownload(video_id);
        }
    }

    public void pauseDownload(String video_id) {
        if (recyclerAdapter != null) {
            recyclerAdapter.pauseDownload(video_id);
        }
    }

    public void cancelDownload(String video_id) {
        showMessages(getString(R.string.txt_download_failed));
        if (recyclerAdapter != null) {
            recyclerAdapter.cancelDownload(video_id);
        }

        int index = -1;
        for (int i = 0; i < mList.size(); i ++){
            if (mList.get(i).getId().equalsIgnoreCase(video_id)){
                index = i;
            }
        }
        if (index > -1){
            mList.remove(index);
        }

        ((MainActivity) getActivity()).showBadgeView( mList.size());
    }

    public void finishedDownload(String video_id) {
        if (recyclerAdapter != null) {
            recyclerAdapter.finishedDownload(video_id);
        }

        int index = -1;
        for (int i = 0; i < mList.size(); i ++){
            if (mList.get(i).getId().equalsIgnoreCase(video_id)){
                index = i;
            }
        }
        if (index > -1){
            mList.remove(index);
        }

        ((MainActivity) getActivity()).showBadgeView( mList.size());
    }

    public void showProgress(String video_id, Progress progress) {
        if (recyclerAdapter != null) {
            recyclerAdapter.showProgress(video_id, progress);
        }
    }

}
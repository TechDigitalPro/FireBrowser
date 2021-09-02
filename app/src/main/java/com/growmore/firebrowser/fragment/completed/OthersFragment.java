package com.growmore.firebrowser.fragment.completed;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.activity.browser.BrowserActivity;
import com.growmore.firebrowser.activity.main.VideoPlayerActivity;
import com.growmore.firebrowser.adapter.CompletedVideoAdapter;
import com.growmore.firebrowser.db.DBController;
import com.growmore.firebrowser.dialog.DialogRename;
import com.growmore.firebrowser.model.CompletedVideoListDTO;
import com.growmore.firebrowser.model.VideoModel;
import com.growmore.firebrowser.utils.CommonUtils;
import com.growmore.firebrowser.utils.Config;
import com.growmore.firebrowser.utils.GsonUtils;
import com.growmore.firebrowser.utils.ShareUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OthersFragment extends Fragment {
    View mFragView;
    @BindView(R.id.lvBlankView) LinearLayout lvBlankView;
    @BindView(R.id.lvCoverView) RelativeLayout lvCoverView;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private CompletedVideoAdapter recyclerAdapter;

    public OthersFragment() {
    }

    public static OthersFragment newInstance() {
        OthersFragment fragment = new OthersFragment();
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
                onClickCover();
            }catch (Exception e){
                e.printStackTrace();
            }
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
                    onClickCover();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragView = inflater.inflate(R.layout.fragment_others, container, false);
        ButterKnife.bind(this, mFragView);
        initView();
        return mFragView;
    }

    private void initView() {
        initRecycler();
    }

    private void initRecycler() {
        ArrayList<VideoModel> tempList = DBController.shared().getAllVideoData();
        ArrayList<VideoModel> videoList = new ArrayList<>();
        for (VideoModel model:tempList){
            if (!model.isPrivate()){
                videoList.add(model);
            }
        }

        ArrayList<CompletedVideoListDTO> list = new ArrayList<>();
        for (int i = 0; i < videoList.size(); i++){
            CompletedVideoListDTO dto = new CompletedVideoListDTO();
            dto.setTime(videoList.get(i).getTime());
            dto.setVideoModel(videoList.get(i));
            dto.setType(CompletedVideoListDTO.ITEM_TYPE.IS_VIDEO);
            list.add(dto);
        }

        if (list.size() == 0){
            lvBlankView.setVisibility(View.VISIBLE);
        }else {
            lvBlankView.setVisibility(View.GONE);
        }
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        recyclerAdapter = new CompletedVideoAdapter(getActivity(), list, new CompletedVideoAdapter.CompletedVideoListener() {
            @Override
            public void onItemClicked(VideoModel model, int pos) {
                recyclerAdapter.onItemNewClicked(pos);
                model.setNew(false);
                DBController.shared().updateVideo(model);

                Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                intent.putExtra(Config.PLAYER_VIDEO_MODEL, GsonUtils.getInstance().toJson(model));
                startActivity(intent);
            }

            @Override
            public void onItemMore(VideoModel model, int pos) {
                lvCoverView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onItemFavorite(VideoModel model, int pos) {
                if (recyclerAdapter != null){
                    recyclerAdapter.onItemFavoriteClicked(pos);
                }
                onItemFavoriteClicked(model);
            }

            @Override
            public void onItemShare(VideoModel model, int pos) {
                ShareUtils.shareVideo(getActivity(), model.getOfflineUrl());
                onClickCover();
            }

            @Override
            public void onItemRename(VideoModel model, int pos) {
                DialogRename dialog = new DialogRename(getActivity(), model, model1 -> {
                    recyclerAdapter.onItemRename(model1, pos);
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
                recyclerAdapter.onItemRemoved(pos);
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
        });
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void onItemFavoriteClicked(VideoModel model){

        DBController.shared().updateVideo(model);
    }

    @OnClick(R.id.lvCoverView)
    public void onClickCover(){
        if (recyclerAdapter != null){
            recyclerAdapter.onCoverClicked();
            lvCoverView.setVisibility(View.GONE);
        }
    }

    public void finishedDownload(String video_id) {
        initView();
    }
}
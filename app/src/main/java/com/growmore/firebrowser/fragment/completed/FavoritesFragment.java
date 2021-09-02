package com.growmore.firebrowser.fragment.completed;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.activity.browser.BrowserActivity;
import com.growmore.firebrowser.activity.main.VideoPlayerActivity;
import com.growmore.firebrowser.adapter.CompletedVideoAdapter;
import com.growmore.firebrowser.db.DBController;
import com.growmore.firebrowser.dialog.DialogRename;
import com.growmore.firebrowser.fragment.main.CompletedFragment;
import com.growmore.firebrowser.model.CompletedVideoListDTO;
import com.growmore.firebrowser.model.EventSortType;
import com.growmore.firebrowser.model.HeaderModel;
import com.growmore.firebrowser.model.VideoModel;
import com.growmore.firebrowser.utils.CommonUtils;
import com.growmore.firebrowser.utils.Config;
import com.growmore.firebrowser.utils.GsonUtils;
import com.growmore.firebrowser.utils.ShareUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FavoritesFragment extends Fragment {
    View mFragView;
    @BindView(R.id.lvBlankView) LinearLayout lvBlankView;
    @BindView(R.id.lvCoverView) RelativeLayout lvCoverView;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private CompletedVideoAdapter recyclerAdapter;

    public FavoritesFragment() {
    }

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible && getActivity() != null) {
            try {
                initView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!menuVisible && getActivity() != null) {
            try {
                onClickCover();
            } catch (Exception e) {
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == Config.BROWSER_SHOW_PLAYER) {
                ((CompletedFragment)getParentFragment()).showBadgeView();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragView = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, mFragView);
        initView();
        return mFragView;
    }

    private void initView() {
        if (getArguments() != null) {
            int type = getArguments().getInt(Config.VIDEO_SORT_BY);
            initRecycler(type);
        }else {
            initRecycler(0);
        }

    }

    private void initRecycler(int type) {
        ArrayList<VideoModel> tempList = DBController.shared().getAllVideoData();
        ArrayList<VideoModel> videoList = new ArrayList<>();
        for (VideoModel model:tempList){
            if (model.isFavorite()){
                videoList.add(model);
            }
        }

        ArrayList<CompletedVideoListDTO> list = new ArrayList<>();

        if (type == 0){
            ArrayList<String> headerList = new ArrayList<>();

            for (int i = 0; i < videoList.size(); i++){
                String time = CommonUtils.formattedDate(getActivity(), videoList.get(i).getTime());
                if (!headerList.contains(time)){
                    headerList.add(time);

                    HeaderModel headerModel = new HeaderModel();
                    headerModel.setTitle(time);

                    CompletedVideoListDTO dto = new CompletedVideoListDTO();
                    long timeLong = Long.parseLong(videoList.get(i).getTime());
                    dto.setTime(String.valueOf(timeLong + 1));
                    dto.setHeaderModel(headerModel);
                    dto.setType(CompletedVideoListDTO.ITEM_TYPE.IS_HEADER);
                    list.add(dto);
                }else {
                    long timeLong = Long.parseLong(videoList.get(i).getTime());
                    list.get(list.size() - 1).setTime(String.valueOf(timeLong + 1));
                }
            }

            for (int i = 0; i < videoList.size(); i++){
                CompletedVideoListDTO dto = new CompletedVideoListDTO();
                dto.setTime(videoList.get(i).getTime());
                dto.setVideoModel(videoList.get(i));
                dto.setType(CompletedVideoListDTO.ITEM_TYPE.IS_VIDEO);
                list.add(dto);
            }

            Collections.sort(list, new Comparator<CompletedVideoListDTO>() {
                public int compare(CompletedVideoListDTO o1, CompletedVideoListDTO o2) {
                    return o2.getTime().compareTo(o1.getTime());
                }
            });
        }else if (type == 1){
            for (int i = 0; i < videoList.size(); i++){
                CompletedVideoListDTO dto = new CompletedVideoListDTO();
                dto.setTime(videoList.get(i).getTime());
                dto.setVideoModel(videoList.get(i));
                dto.setType(CompletedVideoListDTO.ITEM_TYPE.IS_VIDEO);
                list.add(dto);
            }
            Collections.sort(list, new Comparator<CompletedVideoListDTO>() {
                public int compare(CompletedVideoListDTO o1, CompletedVideoListDTO o2) {
                    return o1.getVideoModel().getTitle().compareTo(o2.getVideoModel().getTitle());
                }
            });
        }else if (type == 2){
            for (int i = 0; i < videoList.size(); i++){
                CompletedVideoListDTO dto = new CompletedVideoListDTO();
                dto.setTime(videoList.get(i).getTime());
                dto.setVideoModel(videoList.get(i));
                dto.setType(CompletedVideoListDTO.ITEM_TYPE.IS_VIDEO);
                list.add(dto);
            }
            Collections.sort(list, new Comparator<CompletedVideoListDTO>() {
                public int compare(CompletedVideoListDTO o1, CompletedVideoListDTO o2) {
                    return Integer.compare((int) o1.getVideoModel().getTotalSize(), (int) o2.getVideoModel().getTotalSize());
                }
            });
        }

        if (list.size() == 0) {
            lvBlankView.setVisibility(View.VISIBLE);
        } else {
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
                startActivityForResult(intent, Config.BROWSER_SHOW_PLAYER);
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

    public void onItemFavoriteClicked(VideoModel model) {

    }

    @OnClick(R.id.lvCoverView)
    public void onClickCover() {
        if (recyclerAdapter != null) {
            recyclerAdapter.onCoverClicked();
            lvCoverView.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setType(EventSortType evt) {
        try {
            if (evt != null){
                initRecycler(evt.getType());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
package com.growmore.firebrowser.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.Layout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.growmore.firebrowser.R;
import com.growmore.firebrowser.model.CompletedVideoListDTO;
import com.growmore.firebrowser.model.MultiSelectableVideoListDTO;
import com.growmore.firebrowser.model.VideoModel;
import com.growmore.firebrowser.utils.CommonUtils;
import com.growmore.firebrowser.utils.MenuItemInformation;
import com.growmore.firebrowser.utils.ShareUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;

public class CompletedVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private ArrayList<CompletedVideoListDTO> mList = new ArrayList<>();
    private ArrayList<CompletedVideoListDTO> mSearchList = new ArrayList<>();
    private CompletedVideoListener mListener;
    private PopupWindow popupWindowMenu;
    private static final int IS_HEADER = 0, IS_VIDEO = 1;

    public interface  CompletedVideoListener{
        void onItemClicked(VideoModel model, int pos);
        void onItemMore(VideoModel model, int pos);
        void onItemFavorite(VideoModel model, int pos);
        void onItemShare(VideoModel model, int pos);
        void onItemRename(VideoModel model, int pos);
        void onItemDelete(VideoModel model, int pos);
        void onItemGoWebsite(VideoModel model, int pos);
        void onItemRepost(VideoModel model, int pos);
    }

    public CompletedVideoAdapter(Context context, ArrayList<CompletedVideoListDTO> list, CompletedVideoListener listener){
        this.mContext = context;
        this.mList.clear();
        this.mSearchList.clear();
        this.mList.addAll(list);
        this.mSearchList.addAll(list);
        this.mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position).getType() == CompletedVideoListDTO.ITEM_TYPE.IS_VIDEO){
            return IS_VIDEO;
        }else{
            return IS_HEADER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == IS_VIDEO){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_completed_video_adapter, parent, false);
            return new VideoViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_completed_video_header, parent, false);
            return new HeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == IS_VIDEO){
            ((VideoViewHolder) holder).setData(mList.get(position), position);
        }else {
            ((HeaderViewHolder) holder).setData(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setData(ArrayList<CompletedVideoListDTO> list) {
        mList.clear();
        mSearchList.clear();
        mList.addAll(list);
        mSearchList.addAll(list);
        notifyDataSetChanged();
    }


    public void onCoverClicked() {
        if (popupWindowMenu != null && popupWindowMenu.isShowing()) {
            popupWindowMenu.dismiss();
        }
    }

    public void onItemNewClicked(int pos) {
        mList.get(pos).getVideoModel().setNew(!mList.get(pos).getVideoModel().isNew());
        notifyItemChanged(pos);
    }

    public void onItemRename(VideoModel model, int pos) {
        mList.get(pos).getVideoModel().setTitle(model.getTitle());
        notifyItemChanged(pos);
    }

    public void onItemRemoved(int pos) {
        mList.remove(pos);
        notifyItemRemoved(pos);
    }

    public void onItemFavoriteClicked(int pos) {
        mList.get(pos).getVideoModel().setFavorite(!mList.get(pos).getVideoModel().isFavorite());
        notifyItemChanged(pos);
    }

    public void listFilter(String text) {
        mList.clear();
        if(text.isEmpty()){
            mList.addAll(mSearchList);
        } else{
            text = text.toLowerCase();
            for(CompletedVideoListDTO item: mSearchList){
                if(item.getVideoModel().getTitle().toLowerCase().contains(text)){
                    mList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{
        private RoundedImageView imgVideo;
        private TextView tvDuration, lvNew, tvName, tvSiteName, tvSize;
        private ImageView btFavorite, btMore;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVideo = itemView.findViewById(R.id.imgVideo);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            lvNew = itemView.findViewById(R.id.lvNew);
            tvName = itemView.findViewById(R.id.tvName);
            tvSiteName = itemView.findViewById(R.id.tvSiteName);
            tvSize = itemView.findViewById(R.id.tvSize);
            btFavorite = itemView.findViewById(R.id.btFavorite);
            btMore = itemView.findViewById(R.id.btMore);
        }

        public void setData(CompletedVideoListDTO data, int pos){
            VideoModel videoModel = data.getVideoModel();
            if (new File(videoModel.getOfflineUrl()).exists()){
                try {
                    Glide.with(mContext).load(videoModel.getOfflineUrl()).into(imgVideo);
                    tvSize.setText(CommonUtils.getFileSizeFromPath(videoModel.getOfflineUrl()));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (TextUtils.isEmpty(videoModel.getDuration())){
                tvDuration.setVisibility(View.GONE);
            }else {
                tvDuration.setVisibility(View.VISIBLE);
                tvDuration.setText(videoModel.getDuration());
            }
            if (TextUtils.isEmpty(videoModel.getTitle())){
                tvName.setVisibility(View.GONE);
            }else {
                tvName.setVisibility(View.VISIBLE);
                tvName.setText(videoModel.getTitle());
            }
            if (TextUtils.isEmpty(videoModel.getSiteName())){
                tvSiteName.setVisibility(View.GONE);
            }else {
                tvSiteName.setVisibility(View.VISIBLE);
                tvSiteName.setText(videoModel.getTitle());
            }
            if (videoModel.isNew()){
                lvNew.setVisibility(View.VISIBLE);
            }else {
                lvNew.setVisibility(View.GONE);
            }
            if (videoModel.isFavorite()){
                btFavorite.setImageResource(R.drawable.ic_favorite);
            }else {
                btFavorite.setImageResource(R.drawable.ic_favorite_inactive);
            }

            itemView.setOnClickListener(v -> {mListener.onItemClicked(data.getVideoModel(), getLayoutPosition());});
            btFavorite.setOnClickListener(v -> {mListener.onItemFavorite(data.getVideoModel(), getLayoutPosition());});
            btMore.setOnClickListener(v -> {
                mListener.onItemMore(data.getVideoModel(), getLayoutPosition());
                showDropDown(data.getVideoModel(), getLayoutPosition());
            });
        }

        public void showDropDown(VideoModel model, int pos){
            View dropDownView = LayoutInflater.from(mContext).inflate(R.layout.layout_item_completed_video, null);
            MenuItemInformation itemShare = dropDownView.findViewById(R.id.itemShare);
            MenuItemInformation itemRename = dropDownView.findViewById(R.id.itemRename);
            MenuItemInformation itemDelete = dropDownView.findViewById(R.id.itemDelete);
            MenuItemInformation itemWebsite = dropDownView.findViewById(R.id.itemWebsite);
            MenuItemInformation itemRepost = dropDownView.findViewById(R.id.itemRepost);
            itemShare.setOnClickListener(v -> {
                mListener.onItemShare(model, pos);
            });
            itemRename.setOnClickListener(v -> {
                mListener.onItemRename(model, pos);
            });
            itemDelete.setOnClickListener(v -> {
                mListener.onItemDelete(model, pos);
            });
            itemWebsite.setOnClickListener(v -> {
                mListener.onItemGoWebsite(model, pos);
            });
            itemRepost.setOnClickListener(v -> {
                mListener.onItemRepost(model, pos);
            });
            popupWindowMenu = new PopupWindow(dropDownView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindowMenu.setContentView(dropDownView);
            popupWindowMenu.setBackgroundDrawable(new BitmapDrawable());
            popupWindowMenu.setOutsideTouchable(true);
            popupWindowMenu.showAsDropDown(itemView, 0, 0, Gravity.END);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        public void setData(CompletedVideoListDTO data){
            tvTitle.setText(data.getHeaderModel().getTitle());
        }
    }
}

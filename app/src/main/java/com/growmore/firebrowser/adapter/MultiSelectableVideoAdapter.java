package com.growmore.firebrowser.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;

public class MultiSelectableVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private ArrayList<MultiSelectableVideoListDTO> mList = new ArrayList<>();

    private MultiSelectableVideoAdapter.MultiSelectableVideoListener mListener;
    private PopupWindow popupWindowMenu;
    private static final int IS_HEADER = 0, IS_VIDEO = 1;

    public interface  MultiSelectableVideoListener{
        void onItemClicked(VideoModel model, int pos);
        void onItemChecked(VideoModel model, int pos);
    }

    public MultiSelectableVideoAdapter(Context context, ArrayList<MultiSelectableVideoListDTO> list, MultiSelectableVideoAdapter.MultiSelectableVideoListener listener){
        this.mContext = context;
        this.mList.clear();
        this.mList.addAll(list);
        this.mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position).getType() == MultiSelectableVideoListDTO.ITEM_TYPE.IS_VIDEO){
            return IS_VIDEO;
        }else{
            return IS_HEADER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == IS_VIDEO){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_multiable_video_adapter, parent, false);
            return new MultiSelectableVideoAdapter.VideoViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_completed_video_header, parent, false);
            return new MultiSelectableVideoAdapter.HeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == IS_VIDEO){
            ((MultiSelectableVideoAdapter.VideoViewHolder) holder).setData(mList.get(position), position);
        }else {
            ((MultiSelectableVideoAdapter.HeaderViewHolder) holder).setData(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setData(ArrayList<MultiSelectableVideoListDTO> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public ArrayList<MultiSelectableVideoListDTO> getData(){
        return mList;
    }

    public void onCoverClicked() {
        if (popupWindowMenu != null && popupWindowMenu.isShowing()) {
            popupWindowMenu.dismiss();
        }
    }

    public void onItemChecked(int pos) {
        mList.get(pos).setChecked(!mList.get(pos).isChecked());
        notifyItemChanged(pos);
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

    class VideoViewHolder extends RecyclerView.ViewHolder{
        private RoundedImageView imgVideo;
        private TextView tvDuration, tvName, tvSiteName, tvSize;
        private ImageView btFavorite, btMore;
        private CheckBox chkItem;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVideo = itemView.findViewById(R.id.imgVideo);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            chkItem = itemView.findViewById(R.id.chkItem);
            tvName = itemView.findViewById(R.id.tvName);
            tvSiteName = itemView.findViewById(R.id.tvSiteName);
            tvSize = itemView.findViewById(R.id.tvSize);
            btFavorite = itemView.findViewById(R.id.btFavorite);
            btMore = itemView.findViewById(R.id.btMore);
        }

        public void setData(MultiSelectableVideoListDTO data, int pos){
            VideoModel videoModel = data.getVideoModel();
            if (new File(videoModel.getOfflineUrl()).exists()){
                try {
                    Glide.with(mContext).load(videoModel.getOfflineUrl()).into(imgVideo);
                    tvSize.setText(CommonUtils.getFileSizeFromPath(videoModel.getOfflineUrl()));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            chkItem.setChecked(data.isChecked());

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

            itemView.setOnClickListener(v -> {mListener.onItemClicked(data.getVideoModel(), getLayoutPosition());});

            chkItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
                mList.get(getLayoutPosition()).setChecked(isChecked);
                mListener.onItemChecked(data.getVideoModel(), getLayoutPosition());
            });
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        public void setData(MultiSelectableVideoListDTO data){
            tvTitle.setText(data.getHeaderModel().getTitle());
        }
    }
}

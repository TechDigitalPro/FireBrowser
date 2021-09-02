package com.growmore.firebrowser.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.downloader.Progress;
import com.growmore.firebrowser.R;
import com.growmore.firebrowser.model.VideoModel;
import com.growmore.firebrowser.utils.CommonUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class DownloadVideoAdapter extends RecyclerView.Adapter<DownloadVideoAdapter.ViewHolder>{
    private Context mContext;
    private DownloadVideoListener mListener;
    private ArrayList<VideoModel> mList = new ArrayList<>();
    private int notifyCount = 0;
    private int notifyPro = 0;

    public interface DownloadVideoListener{
        void onStartPauseClicked(VideoModel model, int pos);
        void onItemClicked(VideoModel model, int pos);
    }

    public DownloadVideoAdapter(Context context, ArrayList<VideoModel> list, DownloadVideoListener listener) {
        this.mContext = context;
        this.mListener = listener;
        this.mList.clear();
        this.mList.addAll(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_download_video_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoModel model = mList.get(position);
        try {
            if (new File(model.getOfflineUrl()).exists()){
                Glide.with(mContext).load(model.getOfflineUrl()).into(holder.imgVideo);
            }else {
                if (!TextUtils.isEmpty(model.getThumbUrl())){
                    Glide.with(mContext).load(model.getThumbUrl()).into(holder.imgVideo);
                }
            }
            if (!TextUtils.isEmpty(model.getTitle())){
                holder.tvTitle.setText(model.getTitle());
            }

            holder.btStart.setOnClickListener(v -> {
                mListener.onStartPauseClicked(model, position);
            });

            holder.itemView.setOnClickListener(v -> {
                mListener.onItemClicked(model, position);
            });

            if (model.getProcess() != null){
                holder.lvProgress.setVisibility(View.VISIBLE);
                int totalB = /*100*/(int)model.getProcess().totalBytes;
                int curB = /*notifyPro*/(int)model.getProcess().currentBytes;
                holder.itemProgressBar.setMax(totalB);
                holder.itemProgressBar.setProgress(curB);
                String perStr = String.valueOf(((curB * 100) / totalB));
                String progressStr = String.format(Locale.US, "%s / %s", CommonUtils.getFormattedSize(curB), CommonUtils.getFormattedSize(totalB));
                holder.tvSize.setText(progressStr);

                if (model.getDownload_status() == 3){
                    String _progressStr = String.format(Locale.US, "%s / %s", CommonUtils.getFormattedSize(totalB), CommonUtils.getFormattedSize(totalB));
                    holder.tvSize.setText(_progressStr);
                }
            }
            switch (model.getDownload_status()){
                case 1:// download started
                    holder.lvProgress.setVisibility(View.VISIBLE);
                    holder.btStart.setImageResource(R.drawable.ic_pause_btn);
                    break;
                case 2:// download canceled
                    break;
                case 3: // download finished
                    holder.lvProgress.setVisibility(View.GONE);
                    holder.btStart.setImageResource(R.drawable.ic_play_btn);
                    break;
                case 4: // download paused
                    holder.lvProgress.setVisibility(View.VISIBLE);
                    holder.btStart.setImageResource(R.drawable.ic_play_btn);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addItem(VideoModel model) {
        mList.add(model);
        notifyItemInserted(mList.size() - 1);
    }

    public void startDownload(String video_id){
        for (int i = 0; i < mList.size(); i ++){
            if (mList.get(i).getId().equalsIgnoreCase(video_id)){
                mList.get(i).setDownload_status(1);
                notifyItemChanged(i);
            }
        }
    }

    public void pauseDownload(String video_id){
        for (int i = 0; i < mList.size(); i ++){
            if (mList.get(i).getId().equalsIgnoreCase(video_id)){
                mList.get(i).setDownload_status(4);
                notifyItemChanged(i);
            }
        }
    }

    public void cancelDownload(String video_id){
        notifyPro = 0;
        int index = -1;
        for (int i = 0; i < mList.size(); i ++){
            if (mList.get(i).getId().equalsIgnoreCase(video_id)){
                index = i;
            }
        }
        if (index > -1){
            mList.remove(index);
            notifyDataSetChanged();
        }

    }

    public void finishedDownload(String video_id) {
        notifyPro = 0;
        for (int i = 0; i < mList.size(); i ++){
            if (mList.get(i).getId().equalsIgnoreCase(video_id)){
                mList.get(i).setDownload_status(3);
                notifyItemChanged(i);
            }
        }
    }

    public void showProgress(String video_id, Progress progress) {
        notifyCount ++;
        if (notifyCount > 10){
            if (notifyPro > 80){

            }else {
                Random random = new Random();
                int randomNum = random.nextInt((5 - 1) + 1) + 1;
                notifyPro += randomNum;
            }
            for (int i = 0; i < mList.size(); i ++){
                if (mList.get(i).getId().equalsIgnoreCase(video_id)){
                    mList.get(i).setProcess(progress);
                    notifyItemChanged(i);
                    notifyCount = 0;
                }
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle, tvSpeed, tvSize;
        private ImageView btStart;
        private RoundedImageView imgVideo;
        private ProgressBar itemProgressBar;
        private RelativeLayout lvProgress;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSpeed = itemView.findViewById(R.id.tvSpeed);
            tvSize = itemView.findViewById(R.id.tvSize);
            btStart = itemView.findViewById(R.id.btStart);
            imgVideo = itemView.findViewById(R.id.imgVideo);
            itemProgressBar = itemView.findViewById(R.id.itemProgressBar);
            lvProgress = itemView.findViewById(R.id.lvProgress);
        }
    }
}

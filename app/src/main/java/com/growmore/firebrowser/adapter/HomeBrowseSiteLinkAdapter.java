package com.growmore.firebrowser.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.model.SiteLinkModel;

import java.util.ArrayList;

public class HomeBrowseSiteLinkAdapter extends RecyclerView.Adapter<HomeBrowseSiteLinkAdapter.ViewHolder>{
    private Context mContext;
    private OnItemClickListener mListener;
    private ArrayList<SiteLinkModel> mList = new ArrayList<>();

    public HomeBrowseSiteLinkAdapter(Context context, ArrayList<SiteLinkModel> list, OnItemClickListener listener){
        this.mContext = context;
        this.mListener = listener;
        this.mList.clear();
        this.mList.addAll(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_home_browse_site_link, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SiteLinkModel model = mList.get(position);
        holder.imgItem.setImageResource(model.getImage());
        holder.tvTitle.setText(model.getTitle());
        holder.itemView.setOnClickListener(v -> {
            mListener.onItemClicked(model);
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgItem;
        private TextView tvTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgItem);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }

    public interface OnItemClickListener{
        void onItemClicked(SiteLinkModel model);
    }
}

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
import com.growmore.firebrowser.model.SearchEngineModel;

import java.util.ArrayList;

public class SearchEngineAdapter extends RecyclerView.Adapter<SearchEngineAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<SearchEngineModel> mList = new ArrayList<>();
    private SearchEngineListener mListener;

    public interface SearchEngineListener {
        void onItemClicked(int position);
    }

    public SearchEngineAdapter(Context context, ArrayList<SearchEngineModel> list, SearchEngineListener listener){
        this.mContext = context;
        this.mListener = listener;
        this.mList.clear();
        this.mList.addAll(list);
    }


    public void onItemSelected(int position) {
        for (int i = 0; i < mList.size(); i++){
            mList.get(i).setChecked(false);
        }
        mList.get(position).setChecked(true);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_engine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchEngineModel model = mList.get(position);
        holder.tvTitle.setText(model.getTitle());
        if (model.isChecked()){
            holder.imgCheck.setImageResource(R.drawable.ic_checkbox_checked);
        }else {
            holder.imgCheck.setImageResource(R.drawable.ic_checkbox_unchecked);
        }
        holder.itemView.setOnClickListener(v -> {
            mListener.onItemClicked(position);
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        private ImageView imgCheck;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            imgCheck = itemView.findViewById(R.id.imgCheck);
        }
    }
}

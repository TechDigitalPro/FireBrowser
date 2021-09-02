package com.growmore.firebrowser.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter;
import com.growmore.firebrowser.R;

public class TabBrowserAdapter extends DragDropSwipeAdapter<View, TabBrowserAdapter.ViewHolder> {
    private Context mContext;

    public TabBrowserAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    protected ViewHolder getViewHolder(@NonNull View view) {
        view = LayoutInflater.from(mContext).inflate(R.layout.item_browser_tab, null);
        return new ViewHolder(view);
    }

    @Nullable
    @Override
    protected View getViewToTouchToStartDraggingItem(View record, @NonNull ViewHolder viewHolder, int i) {
        return viewHolder.itemView;
    }

    @Override
    protected void onBindViewHolder(View record, @NonNull ViewHolder viewHolder, int i) {

        if (record.getParent() != null){
            ViewGroup parentViewGroup = (ViewGroup) record.getParent();
            parentViewGroup.removeAllViews();
        }
        viewHolder.lvMain.removeAllViews();
        viewHolder.lvMain.addView(record);
/*        FaviconHelper faviconHelper = new FaviconHelper(mContext);
        Bitmap bitmap = faviconHelper.getFavicon(record.getURL());

        viewHolder.imgIcon.setImageBitmap(bitmap);
        viewHolder.tvTitle.setText(record.getTitle());
        viewHolder.btClose.setOnClickListener(v -> {

        });
        viewHolder.webView.setVisibility(View.GONE);*/
/*        WebViewClient webViewClient = new WebViewClient();
        viewHolder.webView.setWebViewClient(webViewClient);
        viewHolder.webView.setInitialScale(1);
        viewHolder.webView.getSettings().setLoadWithOverviewMode(true);
        viewHolder.webView.getSettings().setUseWideViewPort(true);
        viewHolder.webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        viewHolder.webView.setScrollContainer(false);
        viewHolder.webView.setScrollbarFadingEnabled(true);

        viewHolder.webView.loadUrl(record.getURL());*/

    }

    class ViewHolder extends DragDropSwipeAdapter.ViewHolder{
        private ImageView imgIcon, btClose;
        private TextView tvTitle;
        private WebView webView;
        private RelativeLayout coverView;
        private LinearLayout lvMain;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            btClose = itemView.findViewById(R.id.btClose);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            webView = itemView.findViewById(R.id.webView);
            coverView = itemView.findViewById(R.id.coverView);
            lvMain = itemView.findViewById(R.id.lvMain);
        }
    }
}

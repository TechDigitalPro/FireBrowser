package com.growmore.firebrowser.activity.browser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.base.BaseActivity;
import com.growmore.firebrowser.utils.Config;
import com.growmore.firebrowser.utils.browser.database.Record;
import com.growmore.firebrowser.utils.browser.database.RecordAction;
import com.growmore.firebrowser.utils.browser.unit.BrowserUnit;
import com.growmore.firebrowser.utils.browser.view.RecordAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookmarkActivity extends BaseActivity {
    @BindView(R.id.lvBlankView) LinearLayout lvBlankView;
    @BindView(R.id.listOverView) ListView listOverView;
    @BindView(R.id.btBack) ImageView btBack;
    @BindView(R.id.btDelete) ImageView btDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        RecordAction action = new RecordAction(this);
        action.open(false);
        final List<Record> list;
        list = action.listBookmark(this, false, 0);
        action.close();

        if (list == null || list.size() == 0){
            lvBlankView.setVisibility(View.VISIBLE);
        }else {
            lvBlankView.setVisibility(View.GONE);
        }

        RecordAdapter adapter = new RecordAdapter(this, list){
            @SuppressWarnings("NullableProblems")
            @Override
            public View getView (int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ImageView record_item_icon = v.findViewById(R.id.record_item_icon);
                record_item_icon.setVisibility(View.GONE);
                return v;
            }
        };

        listOverView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listOverView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent();
            intent.putExtra(Config.BROWSER_SHOW_BOOKMARK_DATA, position);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    @OnClick(R.id.btBack)
    public void onClickBack(){
        finish();
    }

    @OnClick(R.id.btDelete)
    public void onClickDelete(){
        BrowserUnit.clearBookmark(this);
        initView();
    }
}
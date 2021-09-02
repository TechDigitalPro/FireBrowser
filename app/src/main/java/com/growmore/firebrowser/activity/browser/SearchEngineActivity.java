package com.growmore.firebrowser.activity.browser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.adapter.SearchEngineAdapter;
import com.growmore.firebrowser.application.App;
import com.growmore.firebrowser.base.BaseActivity;
import com.growmore.firebrowser.model.BrowserSettingModel;
import com.growmore.firebrowser.model.SearchEngineModel;
import com.growmore.firebrowser.utils.Config;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchEngineActivity extends BaseActivity {

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private SearchEngineAdapter recyclerAdapter;
    private BrowserSettingModel browserSettingModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_engine);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        browserSettingModel = App.readBrowserSetting();
        int position = Integer.parseInt(browserSettingModel.getSearchEngine());
        ArrayList<SearchEngineModel> list = Config.getSearchEngineList();
        for (int i = 0; i < list.size(); i++){
            list.get(i).setChecked(false);
        }
        list.get(position).setChecked(true);
        recyclerAdapter = new SearchEngineAdapter(this, list, position1 -> {
            recyclerAdapter.onItemSelected(position1);
            browserSettingModel.setSearchEngine(String.valueOf(position1));
            App.saveBrowserSetting(browserSettingModel);
        });
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @OnClick(R.id.btBack)
    public void onClickBack(){
        finish();
    }
}
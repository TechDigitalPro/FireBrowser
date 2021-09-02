package com.growmore.firebrowser.activity.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.adapter.FolderAdapter;
import com.growmore.firebrowser.application.App;
import com.growmore.firebrowser.base.BaseActivity;
import com.growmore.firebrowser.model.FilePojo;
import com.growmore.firebrowser.utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectDownloadPathActivity extends BaseActivity {
    @BindView(R.id.fp_tv_location)
    TextView tv_location;
    @BindView(R.id.btCancel) TextView btCancel;
    @BindView(R.id.btMove) TextView btMove;

    private ArrayList<FilePojo> folderAndFileList;
    private ArrayList<FilePojo> foldersList;
    private ArrayList<FilePojo> filesList;

    private String location = Environment.getExternalStorageDirectory().getAbsolutePath();
    private boolean pickFiles;
    private Intent receivedIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_download_path);
        ButterKnife.bind(this);
        if (!isExternalStorageReadable()) {
            showMessages(getString(R.string.txt_storage_access_permission_not_given));
            finish();
        }
        receivedIntent = getIntent();
        tv_location.setMovementMethod(new ScrollingMovementMethod());
        location = Environment.getExternalStorageDirectory().getAbsolutePath();
        loadLists(location);
    }

    boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    void loadLists(String location) {
        try {

            File folder = new File(location);
            if (!folder.isDirectory()){
                folder.mkdirs();
            }
            String path = folder.getAbsolutePath().replace("/storage/emulated/0", getString(R.string.txt_internal_storage));
            //path = path.replace("/", " &#x25ba; ");
            tv_location.setText(Html.fromHtml(path));
            File[] files = folder.listFiles();

            foldersList = new ArrayList<>();
            filesList = new ArrayList<>();

            for (File currentFile : files) {
                if (currentFile.isDirectory()) {
                    FilePojo filePojo = new FilePojo(currentFile.getName(), true);
                    foldersList.add(filePojo);
                } else {
                    FilePojo filePojo = new FilePojo(currentFile.getName(), false);
                    filesList.add(filePojo);
                }
            }

            Collections.sort(foldersList, comparatorAscending);
            folderAndFileList = new ArrayList<>();
            folderAndFileList.addAll(foldersList);

            if (pickFiles) {
                Collections.sort( filesList, comparatorAscending );
                folderAndFileList.addAll(filesList);
            }

            showList();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    Comparator<FilePojo> comparatorAscending = new Comparator<FilePojo>() {
        @Override
        public int compare(FilePojo f1, FilePojo f2) {
            return f1.getName().compareTo(f2.getName());
        }
    };


    void showList() {

        try {
            FolderAdapter FolderAdapter = new FolderAdapter(this, folderAndFileList);
            ListView listView = (ListView) findViewById(R.id.fp_listView);
            listView.setAdapter(FolderAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    listClick(position);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    void listClick(int position) {

        if (pickFiles && !folderAndFileList.get(position).isFolder()) {
            String data = location + File.separator + folderAndFileList.get(position).getName();
            receivedIntent.putExtra(Config.DOWNLOAD_VIDEO_MOVE, data);
            setResult(RESULT_OK, receivedIntent);
            finish();
        } else {
            location = location + File.separator + folderAndFileList.get(position).getName();
            loadLists(location);
        }

    }

    @Override
    public void onBackPressed(){
        onClickFolderUp();
    }

    void exit(){
        setResult(RESULT_CANCELED, receivedIntent);
        finish();
    }

    @OnClick(R.id.btBack)
    public void onClickBack(){
        finish();
    }
    @OnClick(R.id.btMove)
    public void onClickOk(){
        receivedIntent.putExtra(Config.DOWNLOAD_VIDEO_MOVE, location);
        setResult(RESULT_OK, receivedIntent);
        finish();
    }
    @OnClick(R.id.btCancel)
    public void onClickCancel(){
        exit();
    }

    public void onClickFolderUp(){
        if (location != null && !location.equals("") && !location.equals("/")) {
            if (location.equalsIgnoreCase("/storage/emulated/0")) return;
            int start = location.lastIndexOf('/');
            String newLocation = location.substring(0, start);
            location = newLocation;
            loadLists(location);
        }else{
            exit();
        }
    }
}

package com.growmore.firebrowser.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseManager extends SQLiteOpenHelper{
    private static DatabaseManager mInstance;

    public DatabaseManager(@Nullable Context context) {
        super(context, DB_name, null, DB_version);
    }

    public static DatabaseManager shared(Context context) {
        if (mInstance == null){
            mInstance = new DatabaseManager(context);
        }
        return mInstance;
    }
    private static final int DB_version = 2;
    private static final String DB_name = "FIRE_BROWSER_DB.db";
    static final String VideoTable = "Table_VIDEO";

    //------------------------------------- video --------------------------------------------------//
    public static final String KEY_id = "key_id";
    public static final String KEY_model = "key_model";

    private static final String CREATE_VIDEO_TABLE = "CREATE TABLE IF NOT EXISTS " + VideoTable +
            "(" + KEY_id + " TEXT ,"
            + KEY_model + " TEXT"
            + ")";

    static final String[] columnsVideo = new String[] {
            DatabaseManager.KEY_id,
            DatabaseManager.KEY_model
    };
    static final String[] columnsMusic = new String[] {
            DatabaseManager.KEY_id,
            DatabaseManager.KEY_model
    };
    static final String[] columnsPoint = new String[] {
            DatabaseManager.KEY_id,
            DatabaseManager.KEY_model
    };
    //---------------------------------------------------------------------------------------------//

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("db", "update");
        db.execSQL("DROP TABLE IF EXISTS '" + CREATE_VIDEO_TABLE + "'");
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        //enable foreign key constraints like ON UPDATE CASCADE, ON DELETE CASCADE
        db.execSQL("PRAGMA foregin_keys=ON;");
    }
}

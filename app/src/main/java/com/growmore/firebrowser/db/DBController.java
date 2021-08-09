package com.growmore.firebrowser.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.growmore.firebrowser.application.App;
import com.growmore.firebrowser.model.FireVideoModel;
import com.growmore.firebrowser.utils.GsonUtils;

import java.util.ArrayList;

public class DBController {
   private SQLiteDatabase database;
   private static DBController mInstance;
   private DatabaseManager dbManager = DatabaseManager.shared(App.context);

   public static DBController shared() {
      if (mInstance == null) {
         synchronized (DBController.class) {
            if (mInstance == null) {
               mInstance = new DBController();
            }
         }
      }
      return mInstance;
   }

   public void deleteAllVideoData() {
      database = dbManager.getWritableDatabase();
      try {
         database.delete(DatabaseManager.VideoTable, null, null);
      }finally {
         database.close();
      }
   }
   public int insertVideo(FireVideoModel data) {
      database = DatabaseManager.shared(App.context).getWritableDatabase();
      try {
         ContentValues values = new ContentValues();

         values.put(DatabaseManager.KEY_id, data.getId());
         String modelStr = GsonUtils.getInstance().toJson(data);
         values.put(DatabaseManager.KEY_model, modelStr);

         long insertedID = database.insert(DatabaseManager.VideoTable, null, values);
         return (int) insertedID;
      }finally {
         database.close();
      }
   }
   public void deleteVideoData(FireVideoModel data) {
      database = dbManager.getWritableDatabase();
      try {
         database.delete(DatabaseManager.VideoTable, DatabaseManager.KEY_id + " = ?", new String[]{String.valueOf(data.getId())});
      }finally {
         database.close();
      }
   }
   public FireVideoModel getVideoDataById(String videoId) {
      database = dbManager.getWritableDatabase();
      try {
         Cursor cursor = database.query(DatabaseManager.VideoTable, DatabaseManager.columnsVideo,
                 DatabaseManager.KEY_id + "= ?", new String[]{videoId},
                 null, null, null);
         if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
         } else {
            if (cursor != null) {
               cursor.close();
            }
            return null;
         }

         String modelStr = cursor.getString(1);
         FireVideoModel data = GsonUtils.getInstance().fromJson(modelStr, FireVideoModel.class);
         cursor.close();
         // to free up memory of cursor
         return data;
      }finally {
         database.close();
      }
   }

   public ArrayList<FireVideoModel> getAllVideoData() {
      ArrayList<FireVideoModel> results = new ArrayList<>();
      database = dbManager.getWritableDatabase();
      try {
         Cursor cursor = database.rawQuery("select * from " + DatabaseManager.VideoTable , new String[]{});
         if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
               do {
                  String modelStr = cursor.getString(1);
                  FireVideoModel data = GsonUtils.getInstance().fromJson(modelStr, FireVideoModel.class);
                  results.add(data);
               } while (cursor.moveToNext());
            }

            cursor.close();
         } else {
            if (cursor != null) {
               cursor.close();
            }
         }

         return results;
      }finally {
         database.close();
      }
   }
}

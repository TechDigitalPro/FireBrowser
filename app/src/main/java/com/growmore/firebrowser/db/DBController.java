package com.growmore.firebrowser.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.growmore.firebrowser.application.App;
import com.growmore.firebrowser.model.VideoModel;
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

   //region ------------------------------------------------- VIDEO -----------------------------------------------//
   public void deleteAllVideoData() {
      database = dbManager.getWritableDatabase();
      try {
         database.delete(DatabaseManager.VideoTable, null, null);
      }finally {
         database.close();
      }
   }
   public int insertVideo(VideoModel data) {
      database = DatabaseManager.shared(App.context).getWritableDatabase();
      try {
         ContentValues values = new ContentValues();

         values.put(DatabaseManager.KEY_id, data.getId());
         values.put(DatabaseManager.KEY_time, data.getTime());
         String modelStr = GsonUtils.getInstance().toJson(data);
         values.put(DatabaseManager.KEY_model, modelStr);

         long insertedID = database.insert(DatabaseManager.VideoTable, null, values);
         return (int) insertedID;
      }finally {
         database.close();
      }
   }
   public void updateVideo(VideoModel data) {
      database = DatabaseManager.shared(App.context).getWritableDatabase();
      try {
         ContentValues values = new ContentValues();

         values.put(DatabaseManager.KEY_id, data.getId());
         values.put(DatabaseManager.KEY_time, data.getTime());
         String modelStr = GsonUtils.getInstance().toJson(data);
         values.put(DatabaseManager.KEY_model, modelStr);

         String where = DatabaseManager.KEY_id + " = ?";
         database.update(DatabaseManager.VideoTable, values, where, new String[]{data.getId()});
      }finally {
         database.close();
      }
   }
   public void deleteVideoData(VideoModel data) {
      database = dbManager.getWritableDatabase();
      try {
         database.delete(DatabaseManager.VideoTable, DatabaseManager.KEY_id + " = ?", new String[]{String.valueOf(data.getId())});
      }finally {
         database.close();
      }
   }
   public VideoModel getVideoDataById(String videoId) {
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

         String modelStr = cursor.getString(2);
         VideoModel data = GsonUtils.getInstance().fromJson(modelStr, VideoModel.class);
         cursor.close();
         // to free up memory of cursor
         return data;
      }finally {
         database.close();
      }
   }
   public ArrayList<VideoModel> getVideoDataByTime(String time) {
      ArrayList<VideoModel> results = new ArrayList<>();
      database = dbManager.getWritableDatabase();
      try {
         Cursor cursor = database.query(DatabaseManager.VideoTable, DatabaseManager.columnsVideo,
                 DatabaseManager.KEY_time + "= ?", new String[]{time},
                 null, null, null);
         if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()){
               do {
                  String modelStr = cursor.getString(2);
                  VideoModel data = GsonUtils.getInstance().fromJson(modelStr, VideoModel.class);
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
   public ArrayList<VideoModel> getAllVideoData() {
      ArrayList<VideoModel> results = new ArrayList<>();
      database = dbManager.getWritableDatabase();
      try {
         Cursor cursor = database.rawQuery("select * from " + DatabaseManager.VideoTable , new String[]{});
         if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
               do {
                  String modelStr = cursor.getString(2);
                  VideoModel data = GsonUtils.getInstance().fromJson(modelStr, VideoModel.class);
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
   //endregion
/*   //region ------------------------------------------------- FAVORITE -----------------------------------------------//
   public void deleteAllFavoriteData() {
      database = dbManager.getWritableDatabase();
      try {
         database.delete(DatabaseManager.FavoriteTable, null, null);
      }finally {
         database.close();
      }
   }
   public int insertFavorite(VideoModel data) {
      database = DatabaseManager.shared(App.context).getWritableDatabase();
      try {
         ContentValues values = new ContentValues();

         values.put(DatabaseManager.KEY_id, data.getId());
         values.put(DatabaseManager.KEY_time, data.getTime());
         String modelStr = GsonUtils.getInstance().toJson(data);
         values.put(DatabaseManager.KEY_model, modelStr);

         long insertedID = database.insert(DatabaseManager.FavoriteTable, null, values);
         return (int) insertedID;
      }finally {
         database.close();
      }
   }
   public void updateVideoFavorite(VideoModel data) {
      database = DatabaseManager.shared(App.context).getWritableDatabase();
      try {
         ContentValues values = new ContentValues();

         values.put(DatabaseManager.KEY_id, data.getId());
         values.put(DatabaseManager.KEY_time, data.getTime());
         String modelStr = GsonUtils.getInstance().toJson(data);
         values.put(DatabaseManager.KEY_model, modelStr);

         String where = DatabaseManager.KEY_id + " = ?";
         database.update(DatabaseManager.FavoriteTable, values, where, new String[]{data.getId()});
      }finally {
         database.close();
      }
   }
   public void deleteFavoriteData(VideoModel data) {
      database = dbManager.getWritableDatabase();
      try {
         database.delete(DatabaseManager.FavoriteTable, DatabaseManager.KEY_id + " = ?", new String[]{data.getId()});
      }finally {
         database.close();
      }
   }
   public VideoModel getFavoriteDataById(String videoId) {
      database = dbManager.getWritableDatabase();
      try {
         Cursor cursor = database.query(DatabaseManager.FavoriteTable, DatabaseManager.columnsFavorite,
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

         String modelStr = cursor.getString(2);
         VideoModel data = GsonUtils.getInstance().fromJson(modelStr, VideoModel.class);
         cursor.close();
         // to free up memory of cursor
         return data;
      }finally {
         database.close();
      }
   }
   public ArrayList<VideoModel> getFavoriteDataByTime(String time) {
      ArrayList<VideoModel> results = new ArrayList<>();
      database = dbManager.getWritableDatabase();
      try {
         Cursor cursor = database.query(DatabaseManager.FavoriteTable, DatabaseManager.columnsFavorite,
                 DatabaseManager.KEY_time + "= ?", new String[]{time},
                 null, null, null);
         if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()){
               do {
                  String modelStr = cursor.getString(2);
                  VideoModel data = GsonUtils.getInstance().fromJson(modelStr, VideoModel.class);
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
   public ArrayList<VideoModel> getAllFavoriteData() {
      ArrayList<VideoModel> results = new ArrayList<>();
      database = dbManager.getWritableDatabase();
      try {
         Cursor cursor = database.rawQuery("select * from " + DatabaseManager.FavoriteTable , new String[]{});
         if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
               do {
                  String modelStr = cursor.getString(2);
                  VideoModel data = GsonUtils.getInstance().fromJson(modelStr, VideoModel.class);
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
   //endregion
   //region ------------------------------------------------- OTHER -----------------------------------------------//
   public void deleteAllOtherData() {
      database = dbManager.getWritableDatabase();
      try {
         database.delete(DatabaseManager.OtherTable, null, null);
      }finally {
         database.close();
      }
   }
   public int insertOther(VideoModel data) {
      database = DatabaseManager.shared(App.context).getWritableDatabase();
      try {
         ContentValues values = new ContentValues();

         values.put(DatabaseManager.KEY_id, data.getId());
         values.put(DatabaseManager.KEY_time, data.getTime());
         String modelStr = GsonUtils.getInstance().toJson(data);
         values.put(DatabaseManager.KEY_model, modelStr);

         long insertedID = database.insert(DatabaseManager.OtherTable, null, values);
         return (int) insertedID;
      }finally {
         database.close();
      }
   }
   public void updateOther(VideoModel data) {
      database = DatabaseManager.shared(App.context).getWritableDatabase();
      try {
         ContentValues values = new ContentValues();

         values.put(DatabaseManager.KEY_id, data.getId());
         values.put(DatabaseManager.KEY_time, data.getTime());
         String modelStr = GsonUtils.getInstance().toJson(data);
         values.put(DatabaseManager.KEY_model, modelStr);

         String where = DatabaseManager.KEY_id + " = ?";
         database.update(DatabaseManager.OtherTable, values, where, new String[]{data.getId()});
      }finally {
         database.close();
      }
   }
   public void deleteOtherData(VideoModel data) {
      database = dbManager.getWritableDatabase();
      try {
         database.delete(DatabaseManager.OtherTable, DatabaseManager.KEY_id + " = ?", new String[]{data.getId()});
      }finally {
         database.close();
      }
   }
   public VideoModel getOtherDataById(String videoId) {
      database = dbManager.getWritableDatabase();
      try {
         Cursor cursor = database.query(DatabaseManager.OtherTable, DatabaseManager.columnsOther,
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

         String modelStr = cursor.getString(2);
         VideoModel data = GsonUtils.getInstance().fromJson(modelStr, VideoModel.class);
         cursor.close();
         // to free up memory of cursor
         return data;
      }finally {
         database.close();
      }
   }
   public ArrayList<VideoModel> getOtherDataByTime(String time) {
      ArrayList<VideoModel> results = new ArrayList<>();
      database = dbManager.getWritableDatabase();
      try {
         Cursor cursor = database.query(DatabaseManager.OtherTable, DatabaseManager.columnsOther,
                 DatabaseManager.KEY_time + "= ?", new String[]{time},
                 null, null, null);
         if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()){
               do {
                  String modelStr = cursor.getString(2);
                  VideoModel data = GsonUtils.getInstance().fromJson(modelStr, VideoModel.class);
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
   public ArrayList<VideoModel> getAllOtherData() {
      ArrayList<VideoModel> results = new ArrayList<>();
      database = dbManager.getWritableDatabase();
      try {
         Cursor cursor = database.rawQuery("select * from " + DatabaseManager.OtherTable , new String[]{});
         if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
               do {
                  String modelStr = cursor.getString(2);
                  VideoModel data = GsonUtils.getInstance().fromJson(modelStr, VideoModel.class);
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
   //endregion*/
}

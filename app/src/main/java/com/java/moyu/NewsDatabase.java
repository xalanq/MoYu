package com.java.moyu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NewsDatabase extends SQLiteOpenHelper {

    private final String TABLE_NAME_NEWS = "news";
    private final String TABLE_NAME_FAVOUR = "favour";
    private final String TABLE_NAME_HISTORY = "history";
    private final String VALUE_ID = "_id";
    private final String VALUE_NEWS_ID = "news_id";
    private final String VALUE_DATA = "data";
    private final String VALUE_TIME = "time";
    private final String CREATE_NEWS = "create table " + TABLE_NAME_NEWS + "(" +
        VALUE_NEWS_ID + " text primary key," +
        VALUE_DATA + " text not null" +
        ")";
    private final String CREATE_FAVOUR = "create table " + TABLE_NAME_FAVOUR + "(" +
        VALUE_ID + " integer primary key," +
        VALUE_NEWS_ID + " text not null unique," +
        VALUE_TIME + " text not null" +
        ")";
    private final String CREATE_HISTORY = "create table " + TABLE_NAME_HISTORY + "(" +
        VALUE_ID + " integer primary key," +
        VALUE_NEWS_ID + " text not null unique," +
        VALUE_TIME + " text not null" +
        ")";
    private final String DROP_NEWS = "drop table " + TABLE_NAME_NEWS;
    private String TAG = "NewsDatabase";
    private static NewsDatabase instance;

    private NewsDatabase(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        Log.d(TAG, "-------> NewsDatabase");
    }

    public static NewsDatabase getInstance() {
        if (instance == null) {
            instance = new NewsDatabase(BasicApplication.getContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NEWS);
        db.execSQL(CREATE_FAVOUR);
        db.execSQL(CREATE_HISTORY);
        Log.d(TAG, "-------> onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "-------> onUpgrade" + "  oldVersion = " + oldVersion + "   newVersion = " + newVersion);
        if (oldVersion != newVersion) {
            switch (newVersion) {
            case 2:
                db.execSQL(CREATE_FAVOUR);
                db.execSQL(CREATE_HISTORY);
                break;
            }
        }
    }

    /**
     * @param news
     * @return successful or not
     */
    public boolean addNews(News news) {
        ContentValues values = new ContentValues();
        values.put(VALUE_NEWS_ID, news.getID());
        values.put(VALUE_DATA, news.toJSONObject().toString());

        long index = getWritableDatabase().insertWithOnConflict(TABLE_NAME_NEWS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return index > 0;
    }

    /**
     * @param news_id
     * @param time
     * @return conflicted or not
     */
    public boolean addFavour(String news_id, LocalDateTime time) {
        ContentValues values = new ContentValues();
        values.put(VALUE_NEWS_ID, news_id);
        values.put(VALUE_TIME, time.format(Constants.dataFormatter));

        long index = getWritableDatabase().insertWithOnConflict(TABLE_NAME_FAVOUR, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        return index > 0;
    }

    /**
     * @param news_id
     * @param time
     * @return conflicted or not
     */
    public boolean addHistory(String news_id, LocalDateTime time) {
        ContentValues values = new ContentValues();
        values.put(VALUE_NEWS_ID, news_id);
        values.put(VALUE_TIME, time.format(Constants.dataFormatter));

        long index = getWritableDatabase().insertWithOnConflict(TABLE_NAME_HISTORY, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        return index > 0;
    }

    final public News queryNews(String news_id) {
        Cursor cursor = getWritableDatabase().query(TABLE_NAME_NEWS, null, VALUE_NEWS_ID + " = ?", new String[]{news_id}, null, null, null, null);

        News news = new News();
        try {
            cursor.moveToFirst();
            news = new News(new JSONObject(cursor.getString(cursor.getColumnIndex(VALUE_DATA))));
        } catch (Exception e) {
            cursor.close();
            e.printStackTrace();
        }

        cursor.close();
        return news;
    }

    final public List<News> queryFavourList(Integer offset, Integer limit) {
        String str_limit = offset.toString() + "," + limit.toString();
        Cursor cursor = getWritableDatabase().query(TABLE_NAME_FAVOUR, null, null, null, null, null, VALUE_TIME + " DESC", str_limit);

        List<News> list = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                list.add(this.queryNews(cursor.getString(cursor.getColumnIndex(VALUE_NEWS_ID))));
                cursor.moveToNext();
            }
        }

        cursor.close();
        getWritableDatabase().close();
        return list;
    }

    final public boolean queryFavour(String news_id) {
        Cursor cursor = getWritableDatabase().query(TABLE_NAME_FAVOUR, null, VALUE_NEWS_ID + " = ?", new String[]{news_id}, null, null, null, null);
        boolean hasExist = cursor.getCount() > 0;
        cursor.close();
        getWritableDatabase().close();
        return hasExist;
    }

    final public List<News> queryHistory(Integer offset, Integer limit) {
        String str_limit = offset.toString() + "," + limit.toString();
        Cursor cursor = getWritableDatabase().query(TABLE_NAME_HISTORY, null, null, null, null, null, VALUE_TIME + " DESC", str_limit);

        List<News> list = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                list.add(this.queryNews(cursor.getString(cursor.getColumnIndex(VALUE_NEWS_ID))));
                cursor.moveToNext();
            }
        }

        cursor.close();
        getWritableDatabase().close();
        return list;
    }

    final public void delFavour(String news_id) {
        getWritableDatabase().delete(TABLE_NAME_FAVOUR, VALUE_NEWS_ID + " = ? ", new String[]{news_id});
    }

    final public void delHistory(String news_id) {
        getWritableDatabase().delete(TABLE_NAME_HISTORY, VALUE_NEWS_ID + " = ? ", new String[]{news_id});
    }

    final public void delAllFavour() {
        getWritableDatabase().delete(TABLE_NAME_FAVOUR, null, null);
    }

    final public void delAllHistory() {
        getWritableDatabase().delete(TABLE_NAME_HISTORY, null, null);
    }

}

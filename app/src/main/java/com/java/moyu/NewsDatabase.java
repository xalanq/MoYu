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

    private static NewsDatabase instance;
    private final String TABLE_NAME_NEWS = "news";
    private final String TABLE_NAME_CATEGORY = "category";
    private final String TABLE_NAME_SEARCH = "search";
    private final String TABLE_NAME_USER = "user";
    private final String VALUE_ID = "_id";
    private final String VALUE_NEWS_ID = "news_id";
    private final String VALUE_DATA = "data";
    private final String VALUE_VIEW_TIME = "view_time";
    private final String VALUE_VIEWED = "viewed";
    private final String VALUE_STAR_TIME = "star_time";
    private final String VALUE_STARED = "stared";
    private final String VALUE_NAME = "name";
    private final String VALUE_POSITION = "position";
    private final String VALUE_KEYWORD = "keyword";
    private final String VALUE_TOKEN = "token";
    private final String CREATE_NEWS = "create table " + TABLE_NAME_NEWS + "(" +
        VALUE_ID + " integer primary key," +
        VALUE_NEWS_ID + " text not null unique," +
        VALUE_DATA + " text not null," +
        VALUE_VIEWED + " integer not null," +
        VALUE_VIEW_TIME + " text," +
        VALUE_STARED + " integer not null," +
        VALUE_STAR_TIME + " text" +
        ")";
    private final String CREATE_CATEGORY = "create table " + TABLE_NAME_CATEGORY + "(" +
        VALUE_ID + " integer primary key," +
        VALUE_NAME + " text not null," +
        VALUE_POSITION + " integer not null" +
        ")";
    private final String CREATE_SEARCH = "create table " + TABLE_NAME_SEARCH + "(" +
        VALUE_ID + " integer primary key," +
        VALUE_KEYWORD + " text not null unique" +
        ")";
    private final String CREATE_USER = "create table " + TABLE_NAME_USER + "(" +
        VALUE_ID + " integer primary key," +
        VALUE_TOKEN + " text not null unique" +
        ")";
    private String TAG = "NewsDatabase";

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
        db.execSQL(CREATE_CATEGORY);
        db.execSQL(CREATE_SEARCH);
        db.execSQL(CREATE_USER);
        int i = 1;
        for (String str : Constants.category) {
            ContentValues values = new ContentValues();
            values.put(VALUE_NAME, str);
            values.put(VALUE_POSITION, i <= 5 ? i : 0);
            db.insertWithOnConflict(TABLE_NAME_CATEGORY, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            i++;
        }
        ContentValues values = new ContentValues();
        values.put(VALUE_TOKEN, "");
        db.insertWithOnConflict(TABLE_NAME_USER, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        Log.d(TAG, "-------> onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "-------> onUpgrade" + "  oldVersion = " + oldVersion + "   newVersion = " + newVersion);
    }

    /**
     * @param news
     * @return successful or not
     */
    public boolean addNews(News news) {
        ContentValues values = new ContentValues();
        values.put(VALUE_NEWS_ID, news.getID());
        values.put(VALUE_DATA, news.toJSONObject().toString());

        Cursor cursor = getWritableDatabase().query(TABLE_NAME_NEWS, null, VALUE_NEWS_ID + " = ?", new String[]{news.getID()}, null, null, null, null);
        if (cursor.getCount() > 0) {
            long index = getWritableDatabase().update(TABLE_NAME_NEWS, values, VALUE_NEWS_ID + " = ?", new String[]{news.getID()});
            return index > 0;
        }

        values.put(VALUE_VIEWED, 0);
        values.put(VALUE_STARED, 0);
        long index = getWritableDatabase().insertWithOnConflict(TABLE_NAME_NEWS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        return index > 0;
    }

    private ContentValues getRow(Cursor cursor) {
        ContentValues values = new ContentValues();
        values.put(VALUE_NEWS_ID, cursor.getString(cursor.getColumnIndex(VALUE_NEWS_ID)));
        values.put(VALUE_DATA, cursor.getString(cursor.getColumnIndex(VALUE_DATA)));
        values.put(VALUE_VIEWED, cursor.getInt(cursor.getColumnIndex(VALUE_VIEWED)));
        if (values.getAsInteger(VALUE_VIEWED) == 1) {
            values.put(VALUE_VIEW_TIME, cursor.getString(cursor.getColumnIndex(VALUE_VIEW_TIME)));
        } else {
            values.putNull(VALUE_VIEW_TIME);
        }
        values.put(VALUE_STARED, cursor.getInt(cursor.getColumnIndex(VALUE_STARED)));
        if (values.getAsInteger(VALUE_STARED) == 1) {
            values.put(VALUE_STAR_TIME, cursor.getString(cursor.getColumnIndex(VALUE_STAR_TIME)));
        } else {
            values.putNull(VALUE_STAR_TIME);
        }
        return values;
    }

    /**
     * @param news_id
     * @param time
     * @return conflicted or not
     */
    public boolean addFavor(String news_id, LocalDateTime time) {
        Cursor cursor = getWritableDatabase().query(TABLE_NAME_NEWS, null, VALUE_NEWS_ID + " = ?", new String[]{news_id}, null, null, null, null);

        ContentValues values = new ContentValues();
        try {
            cursor.moveToFirst();
            values = getRow(cursor);
        } catch (Exception e) {
            cursor.close();
            e.printStackTrace();
        }

        values.put(VALUE_STARED, 1);
        values.put(VALUE_STAR_TIME, time.format(Constants.TIME_FORMATTER));

        int index = getWritableDatabase().update(TABLE_NAME_NEWS, values, VALUE_NEWS_ID + " = ? ", new String[]{news_id});
        return index > 0;
    }

    /**
     * @param news_id
     * @param time
     * @return conflicted or not
     */
    public boolean addHistory(String news_id, LocalDateTime time) {
        Cursor cursor = getWritableDatabase().query(TABLE_NAME_NEWS, null, VALUE_NEWS_ID + " = ?", new String[]{news_id}, null, null, null, null);

        ContentValues values = new ContentValues();
        try {
            cursor.moveToFirst();
            values = getRow(cursor);
        } catch (Exception e) {
            cursor.close();
            e.printStackTrace();
        }

        values.put(VALUE_VIEWED, 1);
        values.put(VALUE_VIEW_TIME, time.format(Constants.TIME_FORMATTER));

        int index = getWritableDatabase().update(TABLE_NAME_NEWS, values, VALUE_NEWS_ID + " = ? ", new String[]{news_id});
        return index > 0;
    }

    final public News queryNews(String news_id) {
        Cursor cursor = getReadableDatabase().query(TABLE_NAME_NEWS, null, VALUE_NEWS_ID + " = ?", new String[]{news_id}, null, null, null, null);

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

    final public List<News> queryFavorList(Integer offset, Integer limit) {
        String str_limit = offset.toString() + "," + limit.toString();
        Cursor cursor = getReadableDatabase().query(TABLE_NAME_NEWS, null, VALUE_STARED + " = ?", new String[]{"1"}, null, null, VALUE_STAR_TIME + " DESC", str_limit);

        List<News> list = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                list.add(queryNews(cursor.getString(cursor.getColumnIndex(VALUE_NEWS_ID))));
                cursor.moveToNext();
            }
        }

        cursor.close();
        return list;
    }

    final public boolean queryFavor(String news_id) {
        Cursor cursor = getReadableDatabase().query(TABLE_NAME_NEWS, null, VALUE_NEWS_ID + " = ?", new String[]{news_id}, null, null, null, null);
        cursor.moveToFirst();
        boolean hasStared = cursor.getInt(cursor.getColumnIndex(VALUE_STARED)) > 0;
        cursor.close();
        return hasStared;
    }

    final public List<News> queryHistoryList(Integer offset, Integer limit) {
        String str_limit = offset.toString() + "," + limit.toString();
        Cursor cursor = getReadableDatabase().query(TABLE_NAME_NEWS, null, VALUE_VIEWED + " = ?", new String[]{"1"}, null, null, VALUE_VIEW_TIME + " DESC", str_limit);

        List<News> list = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                list.add(this.queryNews(cursor.getString(cursor.getColumnIndex(VALUE_NEWS_ID))));
                cursor.moveToNext();
            }
        }

        cursor.close();
        return list;
    }

    public void delFavor(String news_id) {
        ContentValues values = new ContentValues();
        values.put(VALUE_STARED, 0);
        values.putNull(VALUE_STAR_TIME);
        getWritableDatabase().update(TABLE_NAME_NEWS, values, VALUE_NEWS_ID + " = ? ", new String[]{news_id});
    }

    public void delHistory(String news_id) {
        ContentValues values = new ContentValues();
        values.put(VALUE_VIEWED, 0);
        values.putNull(VALUE_VIEW_TIME);
        getWritableDatabase().update(TABLE_NAME_NEWS, values, VALUE_NEWS_ID + " = ? ", new String[]{news_id});
    }

    public void delAllFavor() {
        ContentValues values = new ContentValues();
        values.put(VALUE_STARED, 0);
        values.putNull(VALUE_STAR_TIME);
        getWritableDatabase().update(TABLE_NAME_NEWS, values, null, null);
    }

    public void delAllHistory() {
        ContentValues values = new ContentValues();
        values.put(VALUE_VIEWED, 0);
        values.putNull(VALUE_VIEW_TIME);
        getWritableDatabase().update(TABLE_NAME_NEWS, values, null, null);
    }

    final public List<String> queryAllCategory() {
        return queryCategory(null);
    }

    /**
     * @param chosen if it is null, query all category. chosen = 1, current. Otherwise remain.
     */
    final public List<String> queryCategory(Integer chosen) {
        Cursor cursor;
        if (chosen == null) {
            cursor = getReadableDatabase().query(TABLE_NAME_CATEGORY, null, null, null, null, null, null, null);
        } else if (chosen == 1) {
            cursor = getReadableDatabase().query(TABLE_NAME_CATEGORY, null, VALUE_POSITION + " != ?", new String[]{"0"}, null, null, VALUE_POSITION + " ASC", null);
        } else {
            cursor = getReadableDatabase().query(TABLE_NAME_CATEGORY, null, VALUE_POSITION + " = ?", new String[]{"0"}, null, null, null, null);
        }

        List<String> list = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                list.add(cursor.getString(cursor.getColumnIndex(VALUE_NAME)));
                cursor.moveToNext();
            }
        }

        cursor.close();
        return list;
    }

    public void updateCategory(List<String> chosen, List<String> remain) {
        ContentValues values = new ContentValues();
        for (int i = 0; i < chosen.size(); ++i) {
            values.put(VALUE_POSITION, i + 1);
            getWritableDatabase().update(TABLE_NAME_CATEGORY, values, VALUE_NAME + " = ?", new String[]{chosen.get(i)});
        }
        values.put(VALUE_POSITION, 0);
        for (String s : remain)
            getWritableDatabase().update(TABLE_NAME_CATEGORY, values, VALUE_NAME + " = ?", new String[]{s});
    }

    public boolean addSearchHistory(String keyword) {
        ContentValues values = new ContentValues();
        values.put(VALUE_KEYWORD, keyword);
        long index = getWritableDatabase().insertWithOnConflict(TABLE_NAME_SEARCH, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return index > 0;
    }

    final public List<String> querySearchHistory(Integer offset, Integer limit) {
        String str_limit = offset.toString() + "," + limit.toString();
        Cursor cursor = getReadableDatabase().query(TABLE_NAME_SEARCH, null, null, null, null, null, VALUE_ID + " DESC", str_limit);

        List<String> list = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                list.add(cursor.getString(cursor.getColumnIndex(VALUE_KEYWORD)));
                cursor.moveToNext();
            }
        }

        cursor.close();
        return list;
    }

    public void delSearchHistory(String keyword) {
        getWritableDatabase().delete(TABLE_NAME_SEARCH, VALUE_KEYWORD, new String[]{keyword});
    }

    public void delAllSearchHistory() {
        getWritableDatabase().delete(TABLE_NAME_SEARCH, null, null);
    }

    final public String getToken() {
        Cursor cursor = getReadableDatabase().query(TABLE_NAME_USER, null, VALUE_TOKEN, null, null, null, null);
        if (cursor.getCount() > 0)
            return cursor.getString(0);
        return "";
    }

    public void setToken(String token) {
        ContentValues values = new ContentValues();
        values.put(VALUE_TOKEN, token);
        getWritableDatabase().replace(TABLE_NAME_USER, null, values);
    }

}

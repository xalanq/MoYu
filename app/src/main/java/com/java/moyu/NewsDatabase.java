package com.java.moyu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.LocalDateTime;
import java.util.List;

public class NewsDatabase extends SQLiteOpenHelper {

    private String TAG = "NewsDatabase";

    private final String TABLE_NAME_NEWS = "news";
    private final String TABLE_NAME_FAVOUR = "favour";
    private final String TABLE_NAME_HISTORY = "history";

    private final String VALUE_ID = "_id";
    private final String VALUE_NEWS_ID = "news_id";
    private final String VALUE_DATA = "data";
    private final String VALUE_TIME = "time";

    private final String CREATE_NEWS = "create table " + TABLE_NAME_NEWS + "(" +
        VALUE_NEWS_ID + " text primary key," +
        VALUE_DATA    + " text not null" +
        ")";
    private final String CREATE_FAVOUR = "create table " + TABLE_NAME_FAVOUR + "(" +
        VALUE_ID      + " integer primary key," +
        VALUE_NEWS_ID + " text not null unique" +
        ")";
    private final String CREATE_HISTORY = "create table " + TABLE_NAME_HISTORY + "(" +
        VALUE_ID      + " integer primary key," +
        VALUE_NEWS_ID + " text not null unique," +
        VALUE_TIME    + " text not null" +
        ")";

    private final String DROP_NEWS  = "drop table " + TABLE_NAME_NEWS;

    public NewsDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.d(TAG, "-------> NewsDatabase");
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
     * @param   news
     * @return  successful or not
     */
    public boolean addNews(News news)
    {
        ContentValues values = new ContentValues();
        values.put(VALUE_NEWS_ID, news.getID());
        values.put(VALUE_DATA, news.toJSONObject().toString());

        long index = getWritableDatabase().insertWithOnConflict(TABLE_NAME_NEWS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return index > 0;
    }

    /**
     * @param   news_id
     * @return  conflicted or not
     */
    public boolean addFavour(String news_id)
    {
        ContentValues values = new ContentValues();
        values.put(VALUE_NEWS_ID, news_id);

        long index = getWritableDatabase().insertWithOnConflict(TABLE_NAME_FAVOUR, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        return index > 0;
    }

    /**
     * @param   news_id
     * @param   time
     * @return  conflicted or not
     */
    public boolean addHistory(String news_id, LocalDateTime time)
    {
        ContentValues values = new ContentValues();
        values.put(VALUE_NEWS_ID, news_id);
        values.put(VALUE_TIME, time.format(Constants.dataFormatter));

        long index = getWritableDatabase().insertWithOnConflict(TABLE_NAME_HISTORY, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        return index > 0;
    }

//    public List<News> queryFavour(int count) {
//        Cursor cursor = getWritableDatabase().query(TABLE_NAME_NEWS, null, null, null, null, null, null, null);
//    }

}

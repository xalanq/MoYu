package com.java.moyu;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NewsDatabase extends SQLiteOpenHelper {

    private String TAG = "NewsDatabase";

    private final String TABLE_NAME = "news";
//    private final String VALUE_ID = "_id";
    private final String VALUE_NEWS_ID = "news_id";
    private final String VALUE_JSON = "json";

    private final String CREATE_NEWS = "create table " + TABLE_NAME + "(" +
//        VALUE_ID + " integer primary key," +
//        VALUE_NEWS_ID + " text not null," +
        VALUE_NEWS_ID + " text primary key," +
        VALUE_JSON + " text not null" +
        ")";

    private final String DROP_TABLE  = "drop table " + TABLE_NAME;

    public NewsDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.e(TAG, "-------> NewsDatabase");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NEWS);
        Log.e(TAG, "-------> onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * @param   news which wait for add
     * @return  successful or not
     */
    public boolean addNews(News news)
    {
        ContentValues values = new ContentValues();
        values.put(VALUE_NEWS_ID, news.getID());
        values.put(VALUE_JSON, news.getJSON());

        long index = getWritableDatabase().insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return index > 0;
    }

}

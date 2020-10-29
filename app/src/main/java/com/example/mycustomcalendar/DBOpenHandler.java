package com.example.mycustomcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHandler extends SQLiteOpenHelper {
    private static final String CREATE_EVENTS_TABLE = "create table eventstable(ID INTEGER PRIMARY KEY AUTOINCREMENT, event TEXT, time TEXT, date TEXT, month TEXT, year TEXT, notify TEXT)";
    private static final String DROP_EVENTS_TABLE = "DROP TABLE IF EXISTS eventstable";

    public DBOpenHandler(Context context) {
        super(context, DataBaseStructure.DB_NAME, null, 2);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_EVENTS_TABLE);
        onCreate(db);
    }
    public boolean checkTable(SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + "eventstable" + "'", null);
        if(cursor.getCount()>0)
            return true;
        return false;
    }
    public void SaveEvent(String event, String time, String date, String month, String year, SQLiteDatabase database) {
        //deleteEvent(database);
        if(!checkTable(database))
            onCreate(database);

        ContentValues contentValues = new ContentValues();
        contentValues.put("event", event);
        contentValues.put(DataBaseStructure.TIME, time);
        contentValues.put(DataBaseStructure.DATE, date);
        contentValues.put(DataBaseStructure.MONTH, month);
        contentValues.put(DataBaseStructure.YEAR, year);
        database.insert(DataBaseStructure.EVENT_TABLE_NAME, null, contentValues);
    }

    public Cursor ReadEvents(String date, SQLiteDatabase database) {
        Cursor c=database.query(DataBaseStructure.EVENT_TABLE_NAME, new String[]{"event", DataBaseStructure.TIME, DataBaseStructure.DATE, DataBaseStructure.MONTH, DataBaseStructure.YEAR}, "date=?", new String[]{date}, null, null, null);
        if(c.getCount()>0)
            return c;
        return null;
    }

    public Cursor ReadEventsPerMonth(String month, String year, SQLiteDatabase database) {
            Cursor c = database.query(DataBaseStructure.EVENT_TABLE_NAME, new String[]{"event", DataBaseStructure.TIME, DataBaseStructure.DATE, DataBaseStructure.MONTH, DataBaseStructure.YEAR}, "month=? and year=?", new String[]{month, year}, null, null, null);
            if (c.getCount() > 0)
                return c;

        return null;
    }
    public void deleteEvent(SQLiteDatabase db){
        db.execSQL(DROP_EVENTS_TABLE);
    }
}

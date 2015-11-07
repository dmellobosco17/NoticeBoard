package com.bosco.noticeboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "NoticeBoard.db";
    public static final String NOTICES_TABLE_NAME = "notices";
    public static final String CHANNELS_TABLE_NAME = "channels";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create table notices
        db.execSQL(
                "create table notices " +
                        "(id integer, subject text,content text,priority integer, channel integer,DOE text)"
        );

        //Create table channels
        db.execSQL(
                "create table channels " +
                        "(id integer, name text,description text,image text)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notices");
        db.execSQL("DROP TABLE IF EXISTS channels");
        onCreate(db);
    }

    public boolean insertNotice(Notice note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", note.id);
        contentValues.put("subject", note.subject);
        contentValues.put("content", note.content);
        contentValues.put("priority", note.priority);
        contentValues.put("DOE", note.DOE);

        db.insert("notices", null, contentValues);
        return true;
    }

    public int numberOfNotices() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, "notices");
        return numRows;
    }
    public int numberOfChannels() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, "channels");
        return numRows;
    }

    public Integer deleteNotice(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("notices",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public Integer deleteChannel(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("channels",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<Notice> getAllNotices() {
        ArrayList<Notice> array_list = new ArrayList<Notice>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from notices", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            Notice note = new Notice();
            note.id = res.getInt(res.getColumnIndex("id"));
            note.subject = res.getString(res.getColumnIndex("subject"));
            note.content = res.getString(res.getColumnIndex("content"));
            note.DOE = res.getString(res.getColumnIndex("DOE"));
            note.channel = res.getInt(res.getColumnIndex("channel"));
            note.priority = res.getInt(res.getColumnIndex("priority"));

            array_list.add(note);
            res.moveToNext();
        }
        return array_list;
    }
}
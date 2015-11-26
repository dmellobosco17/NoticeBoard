package com.bosco.noticeboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "NoticeBoard.db";
    public static final String NOTICES_TABLE_NAME = "notices";
    public static final String CHANNELS_TABLE_NAME = "channels";

    private static final String TAG = "DBHelper";
    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create table notices
        db.execSQL(
                "create table notices " +
                        "(id integer, subject text,content text,priority integer, channel integer, channel_name text, DOE text)"
        );

        //Create table channels
        db.execSQL(
                "create table channels " +
                        "(id integer, name text,description text,image text default 'default.jpg')"
        );

        //Insert Universal channel
        db.execSQL(
                "insert into channels (id,name,description) values (0,'Universal','Universal')"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notices");
        db.execSQL("DROP TABLE IF EXISTS channels");
        onCreate(db);
    }

    public boolean insertNotice(Notice note) {

        //Since SQLite DB takes '\n' character very seriously we replace it with '</br/>'

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", note.id);
        contentValues.put("subject", note.subject);
        contentValues.put("content", note.content.replace("\n","<br/>"));
        contentValues.put("channel", note.channel);
        contentValues.put("channel_name", note.channelName);
        contentValues.put("priority", note.priority);
        contentValues.put("DOE", note.DOE);

        db.insert("notices", null, contentValues);
        db.close();
        return true;
    }

    public boolean insertChannel(Channel ch) {

        //Since SQLite DB takes '\n' character very seriously we replace it with '</br/>'

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", ch.id);
        contentValues.put("name", ch.name);
        contentValues.put("description", ch.description.replace("\n","<br/>"));
        contentValues.put("image", ch.imageName);

        db.insert("channels", null, contentValues);
        db.close();
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
        Log.d(TAG,"Number of channels : "+numRows);
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

    public List<Notice> getAllNotices() {
        List<Notice> array_list = new ArrayList<Notice>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from notices order by id desc", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {

            Notice note = new Notice(
                    res.getInt(res.getColumnIndex("id")),
                    res.getString(res.getColumnIndex("subject")),
                    res.getString(res.getColumnIndex("content")).replace("<br/>","\n"), //Remember to replace '<br/>' to '\n'
                    res.getInt(res.getColumnIndex("channel")),
                    res.getString(res.getColumnIndex("channel_name")),
                    res.getInt(res.getColumnIndex("priority")),
                    res.getString(res.getColumnIndex("DOE"))
            );

            Log.d(TAG, note.toString());

            array_list.add(note);
            res.moveToNext();
        }
        res.close();
        db.close();
        return array_list;
    }


    public List<Channel> getAllChannels() {
        List<Channel> array_list = new ArrayList<Channel>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from channels", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {

            Channel ch = new Channel(
                    res.getInt(res.getColumnIndex("id")),
                    res.getString(res.getColumnIndex("name")),
                    res.getString(res.getColumnIndex("description")).replace("<br/>", "\n") //Remember to replace '<br/>' to '\n'
            );

            ch.setImage(context,res.getString(res.getColumnIndex("image")));
            Log.d(TAG, ch.toString());

            array_list.add(ch);
            res.moveToNext();
        }
        res.close();
        db.close();
        return array_list;
    }

    public Notice getNotice(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM notices WHERE id = ?", new String[]{Integer.toString(id)});
        res.moveToFirst();

        Notice note = new Notice(
                res.getInt(res.getColumnIndex("id")),
                res.getString(res.getColumnIndex("subject")),
                res.getString(res.getColumnIndex("content")).replace("<br/>","\n"), //Remember to replace '<br/>' to '\n'
                res.getInt(res.getColumnIndex("channel")),
                res.getString(res.getColumnIndex("channel_name")),
                res.getInt(res.getColumnIndex("priority")),
                res.getString(res.getColumnIndex("DOE"))
        );

        return note;
    }

    public void emptyChannels(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from channels");
    }
}
package com.bosco.noticeboard;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bosco on 7/11/15.
 */
public class SyncDatabase extends Thread {

    private final String TAG = "SyncDatabase";
    private Context context;

    SyncDatabase(Context c){
        context = c;
    }

    public void run(){
        Map<String,String> payload = new HashMap<String,String>();
        payload.put("channels","[1]");
        String url = NoticeBoardPreferences.URL_SYNC_DB;

        NetworkHandler nh = new NetworkHandler(payload,url);
        String result = nh.callServer();
        Log.d(TAG, result);

        JSONHandler json = new JSONHandler(result);

        List<Notice> notices = new ArrayList<Notice>();
        JSONArray notes = json.getArray("notices");

        for (int i = 0; i < notes.length(); i++) {
            try {
                notices.add(new JSONHandler(notes.getString(i)).getNotice());
            } catch (JSONException e) {
                Log.d(TAG, "JSONException > " + notes.toString());
            }
        }
        DBHelper db = new DBHelper(context);
        db.onUpgrade(db.getWritableDatabase(),1,1);

        for(Notice n : notices){
            db.insertNotice(n);
        }
    }
}

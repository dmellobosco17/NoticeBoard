package com.bosco.noticeboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bosco on 7/11/15.
 */
public class SyncDatabase extends AsyncTask<String, Void, String> {

    private final String TAG = "SyncDatabase";
    private Context context;
    public List<Notice> notices;
    public List<Channel> channels;
    private ProgressDialog pd;

    SyncDatabase(Context c) {
        context = c;
        pd = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.setMessage("Refreshing");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected String doInBackground(String... params) {
        Map<String, String> payload = new HashMap<String, String>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        DBHelper db = new DBHelper(context);
        List<Channel> channels = db.getAllChannels();
        String channelString = "[";
        for (Channel ch: channels) {
            if(sharedPreferences.getBoolean("channel_"+ch.id,false)){
                channelString += ""+ch.id+",";
            }
        }
        if(!channelString.equals("["))
            channelString = channelString.substring(0,channelString.length()-1);

        channelString += "]";
        Log.d(TAG,channelString);
        // get device IMEI number
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMEI = "UNKNOWN";
        try {
            IMEI = mngr.getDeviceId();
        }catch(Exception e){
            Log.d(TAG,"Unable to get IMEI");
        }

        payload.put("channels", channelString);
        payload.put("IMEI", IMEI);
        payload.put("token", sharedPreferences.getString(NoticeBoardPreferences.GCM_TOKEN,"NULL"));
        String url = NoticeBoardPreferences.URL_SYNC_DB;

        NetworkHandler nh = new NetworkHandler(payload, url);
        String result = nh.callServer();
        Log.d(TAG, result);

        JSONHandler json = new JSONHandler(result);

        notices = new ArrayList<Notice>();
        channels = new ArrayList<Channel>();
        JSONArray notes = json.getArray("notices");

        for (int i = 0; i < notes.length(); i++) {
            try {
                notices.add(new JSONHandler(notes.getString(i)).getNotice());
            } catch (JSONException e) {
                Log.d(TAG, "JSONException > " + notes.toString());
            }
        }

        JSONArray chans = json.getArray("channels");
        for (int i = 0; i < chans.length(); i++) {
            try {
                channels.add(new JSONHandler(chans.getString(i)).getChannel());
            } catch (JSONException e) {
                Log.d(TAG, "JSONException > " + chans.toString());
            }
        }

        db.onUpgrade(db.getWritableDatabase(), 1, 1);

        for (Notice n : notices) {
            db.insertNotice(n);
        }

        for (Channel ch : channels) {
            db.insertChannel(ch);
        }

        Collections.reverse(notices);
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        MainActivity activity = (MainActivity)context;
        activity.NA = new NoticeAdapter(notices);
        activity.RV.setAdapter(activity.NA);
        activity.RV.refreshDrawableState();
        pd.dismiss();
    }
}

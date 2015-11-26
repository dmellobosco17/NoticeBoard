package com.bosco.noticeboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bosco on 11/26/2015.
 * This AsyncTask is only to fetch channel images when we open settings activity.
 */
public class SyncChannels extends AsyncTask<String, Void, String> {
    private ProgressDialog pd;
    SharedPreferences sharedPreferences;
    Context context;
    private static String TAG = "SyncChannels";
    private boolean runInBackground;

    SyncChannels(Context context,boolean background){
        this.context = context;
        this.runInBackground = background;
        pd = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(!runInBackground) {
            pd.setMessage("Refreshing");
            pd.setCancelable(false);
            pd.show();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG,"Fetching channels in background");

        NetworkHandler nh = new NetworkHandler(new HashMap<String,String>(),NoticeBoardPreferences.URL_SYNC_CHANNELS);
        String result = nh.callServer();
        if(result.equals("No response")){
            pd.dismiss();
            this.cancel(true);
        }
        Log.d(TAG, result);

        JSONHandler json = new JSONHandler(result);
        if(json.jsonObject == null){
            Toast.makeText(context, "Server Error!!!", Toast.LENGTH_LONG).show();
            cancel(true);
        }
        List<Channel> channels = new ArrayList<Channel>();

        JSONArray chans = json.getArray("channels");
        for (int i = 0; i < chans.length(); i++) {
            try {
                channels.add(new JSONHandler(chans.getString(i)).getChannel());
            } catch (JSONException e) {
                Log.d(TAG, "JSONException > " + chans.toString());
            }
        }

        //Fetch channels from sqlite
        DBHelper db = new DBHelper(context);
        db.emptyChannels();

        for (Channel ch : channels) {
            ch.getAndSetImage(context);
            db.insertChannel(ch);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        NoticeBoardPreferences.initResources(context);
        // Notify UI.
        Intent syncComplete = new Intent(NoticeBoardPreferences.NEW_NOTICE_ARRIVED);        //We are using same code.
        LocalBroadcastManager.getInstance(context).sendBroadcast(syncComplete);

        pd.dismiss();
    }
}

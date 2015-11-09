package com.bosco.noticeboard;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Bosco on 11/5/2015.
 *
 * Used to handle JSON Object
 */
public class JSONHandler {

    private String jsonString;
    private JSONObject jsonObject;
    private final String TAG = "JSONHandler";

    public JSONHandler(String json){
        this.jsonString = json;
        try {
            jsonObject = new JSONObject(jsonString);
        }catch (JSONException e){
            Log.d(TAG+" Exception1", json);
        }
    }

    public Object getObject(String key){
        Object obj = null;
        try {
            obj = jsonObject.getJSONObject(key);
        } catch (JSONException e) {
            Log.d(TAG+" Exception2",e.getMessage());
        }

        return obj;
    }

    public String getString(String key){
        String result = null;
        try {
            result = jsonObject.getString(key);
        } catch (JSONException e) {
            Log.d(TAG+" Exception3",e.getMessage());
        }
        return result;
    }

    public Notice getNotice(){
        Notice n = null;
        try {
            n = new Notice(
                    Integer.parseInt(jsonObject.getString("id")),
                    jsonObject.getString("subject"),
                    jsonObject.getString("content"),
                    Integer.parseInt(jsonObject.getString("channel")),
                    jsonObject.getString("channel_name"),
                    Integer.parseInt(jsonObject.getString("priority")),
                    jsonObject.getString("expiry")
            );

        } catch (JSONException e) {
            Log.d(TAG+" Exception4",e.getMessage());
        }

        Log.d(TAG,n.toString());
        return n;
    }

    public JSONArray getArray(String key){
        JSONArray result = null;
        try {
            result = jsonObject.getJSONArray(key);
        } catch (JSONException e) {
            Log.d(TAG+" Exception5",e.getMessage());
        }
        return result;
    }
}

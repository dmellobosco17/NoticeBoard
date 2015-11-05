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
            Log.d(TAG, e.getMessage());
        }
    }

    public Object getObject(String key){
        Object obj = null;
        try {
            obj = jsonObject.getJSONObject(key);
        } catch (JSONException e) {
            Log.d(TAG,e.getMessage());
        }

        return obj;
    }

    public String getString(String key){
        String result = null;
        try {
            result = jsonObject.getString(key);
        } catch (JSONException e) {
            Log.d(TAG,e.getMessage());
        }
        return result;
    }

    public JSONArray getArray(String key){
        JSONArray result = null;
        try {
            result = jsonObject.getJSONArray(key);
        } catch (JSONException e) {
            Log.d(TAG,e.getMessage());
        }
        return result;
    }
}

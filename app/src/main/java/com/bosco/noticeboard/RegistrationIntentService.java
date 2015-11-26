/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bosco.noticeboard;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    private Intent intent;
    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.intent = intent;
        String result="";
        String token="";

        Log.d(TAG,""+sharedPreferences.getBoolean(NoticeBoardPreferences.SENT_TOKEN_TO_SERVER, false));
        //Return if already have token
        if(sharedPreferences.getBoolean(NoticeBoardPreferences.SENT_TOKEN_TO_SERVER, false)){
            Log.d(TAG, "Device is already registered.");
            Log.d(TAG, "Token : "+sharedPreferences.getString(NoticeBoardPreferences.GCM_TOKEN, ""));
            return;
        }

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            if(!NetworkHandler.isConnectingToInternet()){
                Thread.sleep(1500);
                return;
            }
            InstanceID instanceID = InstanceID.getInstance(this.getApplicationContext());
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.d(TAG, "GCM Registration Token: " + token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            result = sendRegistrationToServer(token);

            JSONHandler json = new JSONHandler(result);
            Log.d(TAG, "JSON : result => " + json.getString("result"));
            if(json.getString("result").equals("success")){
                sharedPreferences.edit().putBoolean(NoticeBoardPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                sharedPreferences.edit().putString(NoticeBoardPreferences.GCM_TOKEN, token).apply();
            }else {
                sharedPreferences.edit().putBoolean(NoticeBoardPreferences.SENT_TOKEN_TO_SERVER, false).apply();
            }
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            Toast.makeText(this,"Failed to register device.", Toast.LENGTH_LONG).show();
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(NoticeBoardPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(NoticeBoardPreferences.REGISTRATION_COMPLETE);
        Bundle data=new Bundle();
        data.putString("result", result);
        registrationComplete.putExtra("data", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private String sendRegistrationToServer(String token) {
        //TODO Sync channels from server to mobile after reinstalling app
        // get device IMEI number
        TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String IMEI = "UNKNOWN";
        String deviceInfo = "UNKNOWN";
        try {
            IMEI = mngr.getDeviceId();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("SDK", Build.VERSION.SDK_INT);
            jsonObject.put("RELEASE", Build.VERSION.RELEASE);
            jsonObject.put("MODEL", android.os.Build.MODEL);
            jsonObject.put("BRAND", Build.BRAND);
            jsonObject.put("MANUFACTURER", Build.MANUFACTURER);
            Log.d(TAG,"DEVICE : "+jsonObject.toString());
            deviceInfo = jsonObject.toString();
        }catch(Exception e){
            Log.d(TAG,"Unable to device info");
        }

        String url = NoticeBoardPreferences.URL_REGISTER_TOKEN;
        Map<String,String> payload = new HashMap<String,String>();
        payload.put(NoticeBoardPreferences.KEY_TOKEN, token);
        payload.put(NoticeBoardPreferences.KEY_IMEI, IMEI);
        payload.put(NoticeBoardPreferences.KEY_DEVICE, deviceInfo);

        NetworkHandler nh = new NetworkHandler(payload, url);
        return nh.callServer();

    }

}

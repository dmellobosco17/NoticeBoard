/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bosco.noticeboard;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String id = data.getString("id");
        String subject = data.getString("subject");
        String content = data.getString("content");
        String priority = data.getString("priority");
        String channel = data.getString("channel");
        String DOE = data.getString("DOE");

        Log.d(TAG, "ID : " + id);
        Log.d(TAG, "Subject : " + subject);
        Log.d(TAG, "Content : " + content);
        Log.d(TAG, "Priority : " + priority);
        Log.d(TAG, "Channel : " + channel);
        Log.d(TAG, "DOE : " + DOE);

        Notice note = new Notice(Integer.parseInt(id), subject, content, Integer.parseInt(channel), Integer.parseInt(priority), DOE);

        DBHelper db = new DBHelper(this);
        db.insertNotice(note);

        //TODO update UI when GCM arrives
        //MainActivity.NA.notifyDataSetChanged();
        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        //TODO save message in database
        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(note);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param note Notice received from server.
     */
    private void sendNotification(Notice note) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(note.subject)
                .setSmallIcon(R.drawable.normal_bell)
                .setContentText(note.content)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (note.priority == 1) {
            notificationBuilder.setColor(getResources().getColor(R.color.icon_normal));
        } else if (note.priority == 2) {
            notificationBuilder.setColor(getResources().getColor(R.color.icon_important));
        }


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}

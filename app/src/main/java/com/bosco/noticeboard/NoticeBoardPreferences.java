/**
 * Author Bosco D'mello
 *
 * This class contains keys for authentication
 */

package com.bosco.noticeboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.List;

public class NoticeBoardPreferences {

    //Shared preferences keys
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String NEW_NOTICE_ARRIVED = "newNoticeArrived";
    public static final String UPDATE_UI = "updateUI";
    public static final String GCM_TOKEN = "GcmToken";

    //PHP keys
    public static final String KEY_TOKEN = "token";
    public static final String KEY_IMEI = "IMEI";
    public static final String KEY_DEVICE = "DEVICE";

    //Server URLs
    public static final String HOST = "http://dmellobosco17.esy.es";
    //public static final String HOST = "http://192.168.0.100";
    public static final String URL_REGISTER_TOKEN = HOST+"/NoticeBoard/main/index.php?opt=register_token";
    public static final String URL_SYNC_DB = HOST+"/NoticeBoard/main/index.php?opt=sync_db";
    public static final String URL_SYNC_CHANNELS = HOST+"/NoticeBoard/main/index.php?opt=sync_channels";
    public static final String URL_IMAGES = HOST+"/NoticeBoard/channel_imgs/";

    //API keys
    public static final String KEY_AUTH = "9004264999";

    //Resources
    public static List<Channel> channels;
    public static Bitmap defaultBitmap;

    public static void initResources(Context context){
        defaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.church_photo);

        DBHelper db = new DBHelper(context);
        channels = db.getAllChannels();
        db.close();
    }
}

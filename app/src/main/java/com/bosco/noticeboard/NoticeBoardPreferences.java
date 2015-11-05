/**
 * Author Bosco D'mello
 *
 * This class contains keys for authentication
 */

package com.bosco.noticeboard;

public class NoticeBoardPreferences {

    //Shared preferences keys
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String GCM_TOKEN = "GcmToken";

    //PHP keys
    public static final String KEY_TOKEN = "token";
    public static final String KEY_IMEI = "IMEI";

    //Server URLs
    public static final String HOST = "http://192.168.0.100";
    public static final String URL_REGISTER_TOKEN = HOST+"/NoticeBoard/main/index.php?opt=register_token";

    //API keys
    public static final String KEY_AUTH = "9004264999";
}

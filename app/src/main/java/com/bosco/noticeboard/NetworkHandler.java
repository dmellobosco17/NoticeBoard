package com.bosco.noticeboard;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Used to call server safely
 * Does following tasks:
 * <ul>
 * <li>Creates object with data and URL</li>
 * <li>Call the server when invoked callServer method</li>
 * <li>Wait for the server feedback</li>
 * <li>Returns the server feedback in String</li>
 * </ul>
 *
 * @author Bosco D'mello (dmellobosco17@gmail.com)
 * @category Logic Class
 */
public class NetworkHandler extends Thread {

    protected String data = "";
    protected String result;
    protected URL url;
    protected BufferedReader reader;
    private static final String TAG = "NetworkHandler";
    public static Context context;
    private static int networkState = 0;        //0 = need to check;1 = net present;2 = net absent;

    /**
     * @param payload Map<String, String> Object
     * @param url     URL in string format
     */
    public NetworkHandler(Map<String, String> payload, String url) {
        super();
        try {
            for (Map.Entry<String, String> entry : payload.entrySet()) {
                data += URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
            }
            data += URLEncoder.encode("AUTH", "UTF-8") + "=" + URLEncoder.encode(NoticeBoardPreferences.KEY_AUTH, "UTF-8");

            Log.d(TAG, data);
            Log.d(TAG, url);

            this.url = new URL(url);
        } catch (Exception e) {
        }
    }

    /**
     * Calls sever and waits for server response
     * Halts the execution of main thread until network thread finishes the call
     *
     * @return Server feedback
     */
    public String callServer() {
        result = "No response";

        this.start();
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("SERVER RESPONSE", result);

        return result;
    }

    @Override
    public void run() {
        URLConnection conn;
        try {
            conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
            }
            result = sb.toString();

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    public static boolean isConnectingToInternet() {
        boolean result = false;
        if (networkState == 0) {
            try {
                InetAddress ipAddr = InetAddress.getByName("www.google.com"); //You can replace it with your name

                if (ipAddr.equals("")) {
                    networkState = 2;
                    result = false;
                } else {
                    networkState = 1;
                    result = true;
                }

            } catch (Exception e) {
                result = false;
            }
        } else if (networkState == 1) {
            result = true;
        } else
            result = false;

        if (!result) {
            Log.d(TAG, "Internet absent");
            Toast.makeText(context, "Please check your internet !!!", Toast.LENGTH_LONG).show();
        }
        return result;
    }
}

package com.bosco.noticeboard;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
public class NetworkHandler extends Thread{
	
	protected String data="";
	protected String result;
	protected URL url;
	protected BufferedReader reader;
	private final String TAG="NetworkHandler";
	/**
	 * @param payload Map<String, String> Object
	 * @param url URL in string format
	 */
	public NetworkHandler(Map<String, String> payload,String url)
	{
		super();
		try {
			for (Map.Entry<String, String> entry : payload.entrySet()) {
				data += URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") +"&";
			}
            data += URLEncoder.encode("AUTH", "UTF-8") + "=" + URLEncoder.encode(NoticeBoardPreferences.KEY_AUTH, "UTF-8");

            Log.d(TAG,data);
			this.url = new URL(url);
		}catch(Exception e){}
	}
	
	/**
	 * Calls sever and waits for server response
	 * Halts the execution of main thread until network thread finishes the call
	 *  
	 * @return Server feedback
	 */
	public String callServer()
	{
		result="No response";
		
		this.start();
		try {
			this.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("SERVER RESPONSE", result);
		
		return result;
	}
	
	@Override
	public void run()
	{
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			
		}
	}
}

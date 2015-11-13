package com.bosco.noticeboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Bosco on 11/8/2015.
 */
public class Channel implements Serializable{

    String name,description,imageName=null;
    int id;
    Bitmap imageBitmap;

    Channel(int id, String nm, String desc){
        this.id = id;
        name = nm;
        description = desc;
        imageBitmap = NoticeBoardPreferences.defaultBitmap;
    }

    public void setImage(Context context, String imageName){
        this.imageName = imageName;
        InputStream input = null;
        try {
            input = context.openFileInput(imageName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageBitmap = BitmapFactory.decodeStream(input);
    }

    public void getAndSetImage(Context context){
        try {
            URL url = new URL(NoticeBoardPreferences.URL_IMAGES+imageName);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            FileOutputStream fOut = context.openFileOutput(imageName, Context.MODE_WORLD_READABLE);
            Bitmap.CompressFormat format = null;
            switch(imageName.substring(imageName.lastIndexOf('.')+1).toUpperCase()){
                case "PNG" : format = Bitmap.CompressFormat.PNG;break;
                case "JPG" :
                case "JPEG" : format = Bitmap.CompressFormat.JPEG;break;
            }
            myBitmap.compress(format, 85, fOut);
            fOut.close();
            imageBitmap = myBitmap;
        } catch (IOException e) {
            Log.e("getBmpFromUrl error: ", e.getMessage().toString());
        }
    }

    public String toString(){
        String str = "-----Channel----";
        str += "\nID : " + id;
        str += "\nName : " + name;
        str += "\nDescription : " + description;
        str += "\nImage : " + imageName;

        return str;
    }

}

package com.bosco.noticeboard;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Bosco on 11/6/2015.
 */
public class Notice implements Serializable {
    String subject, content;
    int id, channel, priority;
    String DOE, channelName;

    Notice(int id, String subject, String content, int channel, String channelName, int priority, String DOE) {
        this.id = id;
        this.subject = subject;
        this.content = content;
        this.channel = channel;
        this.channelName = channelName;
        this.priority = priority;
        this.DOE = DOE;

    }

    public Bitmap getBitmap(){
        for (Channel chan : NoticeBoardPreferences.channels) {
            if(chan.id == channel) {
                return chan.imageBitmap;
            }
        }
        return NoticeBoardPreferences.defaultBitmap;
    }

    public String toString() {
        String str = "-----Notice----";
        str += "\nID : " + id;
        str += "\nSubject : " + subject;
        str += "\nContent : " + content;
        str += "\npriority : " + priority;
        str += "\nchannel : " + channel;
        str += "\nDOE : " + DOE;

        return str;
    }

}

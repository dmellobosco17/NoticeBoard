package com.bosco.noticeboard;

import java.util.Date;

/**
 * Created by Bosco on 11/6/2015.
 */
public class Notice {
    String subject, content;
    int id, channel, priority;
    String DOE;

    //TODO should add channels dynamically from server
    private int channelImages[] = {
            R.drawable.church_photo
    };
    int channelImage;

    Notice(){

    }

    Notice(int id, String subject, String content, int channel, int priority, String DOE) {
        this.id = id;
        this.subject = subject;
        this.content = content;
        this.channel = channel;
        this.priority = priority;
        this.DOE = DOE;

        channelImage = channelImages[channel-1];
    }

}

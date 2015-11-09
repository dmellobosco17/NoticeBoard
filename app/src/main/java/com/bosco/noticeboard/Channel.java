package com.bosco.noticeboard;

/**
 * Created by Bosco on 11/8/2015.
 */
public class Channel {

    String name,description;
    int id,imageResource;

    Channel(int id, String nm, String desc, int img){
        this.id = id;
        name = nm;
        description = desc;
        imageResource = img;
    }
}

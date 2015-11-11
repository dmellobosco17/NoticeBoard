package com.bosco.noticeboard;

/**
 * Created by Bosco on 11/8/2015.
 */
public class Channel {

    String name,description;
    int id,imageResource;

    Channel(int id, String nm, String desc){
        this.id = id;
        name = nm;
        description = desc;
    }

    public String toString(){
        String str = "-----Channel----";
        str += "\nID : " + id;
        str += "\nName : " + name;
        str += "\nDescription : " + description;

        return str;
    }

}

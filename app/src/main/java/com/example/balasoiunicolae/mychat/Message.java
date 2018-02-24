package com.example.balasoiunicolae.mychat;

import java.util.Date;

/**
 * Created by Balasoiu Nicolae on 12/10/2017.
 */

public class Message {

    private String mess;
    private String user;
    private long time;

    public Message(String mess, String user){

        this.mess = mess;
        this.user = user;
        this.time = new Date().getTime();
    }


    public Message(){

    }


    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

package com.example.balasoiunicolae.mychat;

import java.util.Date;

/**
 * Created by Balasoiu Nicolae on 1/9/2018.
 */

public class MessageModel {

    private long date;
    private String message;
    private String image;


    public MessageModel(){


    }

    public MessageModel(String message,String image){

        this.message = message;
        this.image = image;
        this.date = new Date().getTime();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

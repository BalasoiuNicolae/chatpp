package com.example.balasoiunicolae.mychat;

/**
 * Created by Balasoiu Nicolae on 1/9/2018.
 */

public class Friends {

    private String date;
    private String name;
    private String url;

    public Friends(String date,String name, String url) {
        this.date = date;
        this.name = name;
        this.url = url;

    }

    public  Friends(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

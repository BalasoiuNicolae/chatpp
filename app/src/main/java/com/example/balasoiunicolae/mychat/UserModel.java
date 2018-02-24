package com.example.balasoiunicolae.mychat;

/**
 * Created by Balasoiu Nicolae on 1/8/2018.
 */

public class UserModel {

    private String status;
    private String nickname;
    private String image;

    public UserModel(String status,String nickname,String image,String thumbnail){
        this.status = status;
        this.nickname = nickname;
        this.image = image;
    }

    public UserModel(){

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

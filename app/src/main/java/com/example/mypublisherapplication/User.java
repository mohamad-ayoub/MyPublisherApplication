package com.example.mypublisherapplication;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String nickName;
    private String email;
    private int activeScore;

    public User() {
    }

    public User(String uid, String nickName, String email, int activeScore) {
        this.uid = uid;
        this.nickName = nickName;
        this.email = email;
        this.activeScore = activeScore;
    }

    public User(String uid, String nickName, String email) {
        this(uid, nickName, email, 0);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getActiveScore() {
        return activeScore;
    }

    public void setActiveScore(int activeScore) {
        this.activeScore = activeScore;
    }
}

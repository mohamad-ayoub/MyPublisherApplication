package com.example.mypublisherapplication;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;

public class UserItemScore implements Serializable {
    private String itemId;
    private int score;
    private Date date;

    public UserItemScore() {
    }

    public UserItemScore(String itemId, int score, Date date) {
        this.itemId = itemId;
        this.score = score;
        this.date = date;
    }

    public UserItemScore(String itemId, int score) {
        this(itemId, score, new Date());
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}

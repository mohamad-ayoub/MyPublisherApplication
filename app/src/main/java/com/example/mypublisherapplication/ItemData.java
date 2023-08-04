package com.example.mypublisherapplication;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;

public class ItemData implements Serializable {
    private String itemId;
    private String uid;
    private String message;
    private String imageUrl;
    private Date publishTime;
    private int score;
    private int usersCount;

    public ItemData() {
    }

    public ItemData(String itemId, String uid, String message, String imageUrl, Date publishTime, int score, int usersCount) {
        this.itemId = itemId;
        this.uid = uid;
        this.message = message;
        this.imageUrl = imageUrl;
        this.publishTime = publishTime;
        this.score = score;
        this.usersCount = usersCount;
    }

    public ItemData(String itemId, String uid, String message, String imageUrl) {
        //TODO - initialize with usercount=0
        this(itemId, uid, message, imageUrl, new Date(), 0, 0);
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }

    public void addScore(int score) {
        this.score += score;
        this.usersCount++;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ItemData) {
            ItemData otherItem = (ItemData)obj;
            return this.itemId.equals(otherItem.itemId);
        }
        return super.equals(obj);
    }
}

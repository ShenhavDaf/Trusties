package com.example.trusties.model;

import com.google.gson.JsonObject;

public class Notification {

    String notificationID;
    String postID;
    String authorID;
    String time;
    String type;

    public Notification(String notificationID, String postID, String authorID, String time, String type) {
        this.notificationID = notificationID;
        this.postID = postID;
        this.authorID = authorID;
        this.time = time;
        this.type = type;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static Notification create(JsonObject json) {
        String notificationId = json.get("_id").getAsString();
        String postId = json.get("postId").getAsString();
        String author = json.get("sender").getAsString();
        String time = json.get("time").getAsString();
        String type = json.get("type").getAsString();
        Boolean isDeleted = json.get("isDeleted").getAsBoolean();

        Notification notification = new Notification(notificationId, postId, author, time, type);
        return notification;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("_id", notificationID);
        json.addProperty("postId", postID);
        json.addProperty("sender", authorID);
        json.addProperty("time", time);
        json.addProperty("type", type);
        json.addProperty("status", "OPEN");
        return json;
    }
}

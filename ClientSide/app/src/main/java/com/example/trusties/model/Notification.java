package com.example.trusties.model;

import com.google.gson.JsonObject;

public class Notification {

    String notificationID;
    String postID;
    String authorID;
    String time;
    String role;
    Integer circle;

    public Notification(String notificationID, String postID, String authorID, String time, String role, Integer circle) {
        this.notificationID = notificationID;
        this.postID = postID;
        this.authorID = authorID;
        this.time = time;
        this.role = role;
        this.circle = circle;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getCircle() {
        return circle;
    }

    public void setCircle(Integer circle) {
        this.circle = circle;
    }

    public static Notification create(JsonObject json) {
        String notificationId = json.get("_id").getAsString();
        String postId = json.get("postId").getAsString();
        String author = json.get("sender").getAsString();
        String time = json.get("time").getAsString();
        String role = json.get("role").getAsString();
        Boolean isDeleted = json.get("isDeleted").getAsBoolean();
        Integer circle = json.get("friends_circle").getAsInt();

        Notification notification = new Notification(notificationId, postId, author, time, role, circle);
        return notification;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("_id", notificationID);
        json.addProperty("postId", postID);
        json.addProperty("sender", authorID);
        json.addProperty("time", time);
        json.addProperty("role", role);
        json.addProperty("status", "OPEN");
        json.addProperty("friends_circle", circle);
        return json;
    }
}

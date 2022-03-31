package com.example.trusties.model;

import com.google.gson.JsonObject;

import java.util.Date;

public class Comment {

    String postId;
    String sender;
    String content;
    String time;

    public Comment() {

    }

    public Comment(String postId, String sender, String content, String currentTime) {
        this.postId = postId;
        this.sender = sender;
        this.content = content;
        this.time = currentTime;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCurrentTime() {
        return time;
    }

    public void setCurrentTime(String currentTime) {
        this.time = currentTime;
    }

    public static Comment create(JsonObject json) {
        String postId = json.get("post").getAsString();
        String sender = json.get("sender").getAsString();
        String content = json.get("message").getAsString();
        String time = json.get("time").getAsString();

        Comment comment = new Comment(postId, sender, content, time);

        return comment;
    }

    /*------------------------------------------------------*/

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("post", postId);
        json.addProperty("sender", sender);
        json.addProperty("message", content);
        json.addProperty("time", time);

        return json;
    }
}

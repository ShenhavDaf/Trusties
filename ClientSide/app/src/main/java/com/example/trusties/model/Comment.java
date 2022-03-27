package com.example.trusties.model;

import com.google.gson.JsonObject;

public class Comment {

    String postId;
    String sender;
    String content;
    Long currentTime;

    public Comment() {

    }

    public Comment(String postId, String sender, String content, Long currentTime) {
        this.postId = postId;
        this.sender = sender;
        this.content = content;
        this.currentTime = currentTime;
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

    public Long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }

    public static Comment create(JsonObject json) {
        String postId = json.get("postId").getAsString();
        String sender = json.get("sender").getAsString();
        String content = json.get("content").getAsString();
        Long currentTime = json.get("currentTime").getAsLong();

        Comment comment = new Comment(postId, sender, content, currentTime);

        return comment;
    }

    /*------------------------------------------------------*/

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("postId", postId);
        json.addProperty("sender", sender);
        json.addProperty("content", content);
        json.addProperty("currentTime", currentTime);

        return json;
    }
}

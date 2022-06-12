package com.example.trusties.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Comment {

    String commentId;
    String postId;
    String sender;
    String content;
    String time;
    String isCorrect;

    List<String> report_negative;
    List<String> report_positive;

    public Comment() {

    }

    public Comment(String commentId, String postId, String sender, String content, String currentTime,String isCorrect,List<String> negative,List<String> positive) {
        this.commentId=commentId;
        this.postId = postId;
        this.sender = sender;
        this.content = content;
        this.time = currentTime;
        this.isCorrect=isCorrect;

        this.report_negative=negative;
        this.report_positive=positive;
    }

    public String getCommentId() {
        return commentId;
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

    public String IsCorrect() {
            return isCorrect;
        }

    public int getCommentRate(){
        int number=this.report_positive.size()-this.report_negative.size();
        if(number==0){
            number=this.report_positive.size();
        }
        return number;
    }

    public boolean IsUserRated_negative(String user_id){

        System.out.println("IsUserRated_negative");
        System.out.println(user_id);
        System.out.println("this.report_negative");
        System.out.println(this.report_negative);

        return this.report_negative.contains(user_id);
    }
    public boolean IsUserRated_positive(String user_id){

        return this.report_positive.contains(user_id);
    }


    /*------------------------------------------------------*/
    public static Comment create(JsonObject json) {
        String commentId=json.get("_id").getAsString();
        String postId = json.get("post").getAsString();
        String sender = json.get("sender").getAsString();
        String content = json.get("message").getAsString();
        String time = json.get("time").getAsString();
        String isCorrect = json.get("isCorrect").getAsString();

        List<String> negative=new ArrayList<>();
        List<String> positive=new ArrayList<>();
        JsonArray arr_negative=json.get("report_negative").getAsJsonArray();
        JsonArray arr_positive=json.get("report_positive").getAsJsonArray();

        for(int i=0;i<arr_negative.size();i++){
            String elm=arr_negative.get(i).toString();
            negative.add(elm);
        }
        for(int i=0;i<arr_positive.size();i++){
            String elm=arr_positive.get(i).toString();
            positive.add(elm);
        }

        Comment comment = new Comment(commentId,postId, sender, content, time,isCorrect,negative,positive);

        return comment;
    }
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("_id", commentId);
        json.addProperty("post", postId);
        json.addProperty("sender", sender);
        json.addProperty("message", content);
        json.addProperty("time", time);
        json.addProperty("isCorrect", isCorrect);

        return json;
    }
}

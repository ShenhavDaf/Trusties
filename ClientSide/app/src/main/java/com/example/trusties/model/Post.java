package com.example.trusties.model;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

public class Post {

    /* ****************************** Data Members ****************************** */
    final public static String COLLECTION_NAME = "posts";
    final public static String LAST_UPDATE = "PostsLastUpdateDate";

    String postID;
    String authorID;
    String authorName;
    String category;
    String title;
    String description;
    String time;
    String role;
    String status;
    Boolean isDeleted;
    String location;
    String address;
    Integer circle;
    //    Long updateDate = new Long(0);


    /* ****************************** Constructors ****************************** */

    public Post(String id, String authorID, String category, String title, String description, String time, String role, String status, Boolean isDeleted, String location, String address, Integer circle) {
        this.postID = id;
        this.authorID = authorID;
        this.category = category;
        this.title = title;
        this.description = description;
        this.time = time;
        this.role = role;
        this.status = status;
        this.isDeleted = isDeleted;
        this.location = location;
        this.address = address;
        this.circle = circle;
        Model.instance.findUserById(authorID, user -> this.authorName = user.get("name").getAsString());
    }

    /* ****************************** Getters & Setters ****************************** */
    public String getId() {
        return postID;
    }

    public void setId(@NonNull String id) {
        this.postID = id;
    }

    /*------------------------------------------------------*/

    public String getAuthorID() {
        return authorID;
    }

    /*------------------------------------------------------*/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /*------------------------------------------------------*/

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*------------------------------------------------------*/

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /*------------------------------------------------------*/

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /*------------------------------------------------------*/

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /*------------------------------------------------------*/

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /*------------------------------------------------------*/

    public Integer getCircle() {
        return circle;
    }

    public void setCircle(Integer circle) {
        this.circle = circle;
    }

    /*------------------------------------------------------*/

    public String getCategory() {
        return category;
    }

    /*------------------------------------------------------*/

    public String getAuthorName() {
        return authorName;
    }


    /* ****************************** Functions ****************************** */

    public static Post create(JsonObject json) {

        String id = json.get("_id").getAsString();
        String author = json.get("sender").getAsString();
        String category = json.get("category").getAsString();
        String title = json.get("title").getAsString();
        String description = json.get("description").getAsString();
        String time = json.get("time").getAsString();
        String role = json.get("role").getAsString();
        String status = json.get("status").getAsString();
        Boolean isDeleted = json.get("isDeleted").getAsBoolean();
        String location = json.get("location").getAsString();
        String address = json.get("address").getAsString();
        Integer circle = json.get("friends_circle").getAsInt();


        Post post = new Post(id, author, category, title, description, time, role, status, isDeleted, location, address, circle);

        return post;
    }

    /*------------------------------------------------------*/

    public JsonObject toJson() {

        JsonObject json = new JsonObject();
        json.addProperty("_id", postID);
        json.addProperty("sender", authorID);
        json.addProperty("category", category);
        json.addProperty("title", title);
        json.addProperty("description", description);
        json.addProperty("time", time);
        json.addProperty("role", role);
        json.addProperty("status", "OPEN");
        json.addProperty("isDeleted", true);
        json.addProperty("location", location);
        json.addProperty("address", address);
        json.addProperty("friends_circle", circle);

        return json;
    }
}

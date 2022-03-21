package com.example.trusties.model;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

public class Post {

    /* ****************************** Data Members ****************************** */
    final public static String COLLECTION_NAME = "posts";
    final public static String LAST_UPDATE = "PostsLastUpdateDate";

    String postID;
    String title;
    String description;
    Boolean display;
    //    Long updateDate = new Long(0);


    /* ****************************** Constructors ****************************** */

    public Post(String id, String title, String description, Boolean display) {
        this.postID = id;
        this.title = title;
        this.description = description;
        this.display = display;
    }

    /* ****************************** Getters & Setters ****************************** */
    public String getId() {
        return postID;
    }

    public void setId(@NonNull String id) {
        this.postID = id;
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

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }


    /* ****************************** Functions ****************************** */

    public static Post create(JsonObject json) {

        String id = json.get("_id").getAsString();
        String title = json.get("title").getAsString();
        String description = json.get("description").getAsString();
//TODO:        Boolean display = json.get("display").getAsBoolean();
        Boolean display = true;

        Post post = new Post(id, title, description, display);

        return post;
    }

    /*------------------------------------------------------*/

    public JsonObject toJson() {

        JsonObject json = new JsonObject();
        json.addProperty("_id", postID);
        json.addProperty("title", title);
        json.addProperty("description", description);
        json.addProperty("display", true);

        return json;
    }
}

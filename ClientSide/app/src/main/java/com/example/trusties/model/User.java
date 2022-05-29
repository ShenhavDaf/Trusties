package com.example.trusties.model;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class User {

    String fullName = "";

    String email = "";
    String phone = "";
    String userID;
    Float rating;
//    List<String> friends;

    //public User(String id, String fullName, String email, String phone, List<String> friends) {

    public User(String id, String fullName, String email, String phone,Float rating) {
        this.userID = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.rating=rating;
    }
    public User(String id, String fullName, String email, String phone) {
        this.userID = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.rating=Float.valueOf(0);
    }


//    public List<String> getFriends() {
//        return friends;
//    }
    public String getId() {
        return userID;
    }

    public void setId(@NonNull String id) {
        this.userID = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static User create(JsonObject json) {

        String id = json.get("_id").getAsString();
        String email = json.get("email").getAsString();
        String name = json.get("name").getAsString();
        String phone = json.get("phone").getAsString();
     //   Float rating= json.get("rating").getAsFloat();

        User user = new User( id,  name,  email,  phone);

        return user;
    }

}

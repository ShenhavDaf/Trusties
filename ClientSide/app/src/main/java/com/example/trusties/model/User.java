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
//    List<String> friends;

    //public User(String id, String fullName, String email, String phone, List<String> friends) {

    public User(String id, String fullName, String email, String phone) {
        this.userID = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
//        this.friends=friends;
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
//        JsonArray friendsArr=json.get("friends").getAsJsonArray();
//
//
//        List<String> friends=new ArrayList<>();
//        for(int i=0;i<friendsArr.size();i++){
//            String elm=friendsArr.get(i).toString();
//            friends.add(elm);
//        }
      //  User user = new User( id,  name,  email,  phone,friends);
        User user = new User( id,  name,  email,  phone);

        return user;
    }

}

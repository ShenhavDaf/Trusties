package com.example.trusties.posts.sos;

import com.example.trusties.model.Comment;
import com.example.trusties.model.Model;
import com.example.trusties.model.User;

import java.util.List;

public class RequestsViewModel {

    List<User> data;
    String postId;

    public RequestsViewModel(String postId) {
        this.postId = postId;

    }

    public List<User> getData() {
        return data;
    }

}

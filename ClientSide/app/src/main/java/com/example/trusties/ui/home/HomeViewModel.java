package com.example.trusties.ui.home;

import androidx.lifecycle.ViewModel;

import com.example.trusties.model.Model;
import com.example.trusties.model.Post;

import java.util.List;

public class HomeViewModel extends ViewModel {

    List<Post> data;

    public HomeViewModel() {
        Model.instance.getAllPosts(postsList -> {
            this.data = postsList;
        });
//        Model.instance.getAllPostsInHomePage( postsList->{
//            this.data = postsList;
//        });
    }

    public List<Post> getData() {
        return data;
    }
}
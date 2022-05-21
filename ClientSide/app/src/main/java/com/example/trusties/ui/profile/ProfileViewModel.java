package com.example.trusties.ui.profile;

import androidx.lifecycle.ViewModel;

import com.example.trusties.model.Model;
import com.example.trusties.model.Post;

import java.util.List;

public class ProfileViewModel extends ViewModel {

    List<Post> data;

    public ProfileViewModel() {
//        Model.instance.getAllPosts(postsList -> {
//            this.data = postsList;
//        });
        Model.instance.getMyPosts(
                Model.instance.getCurrentUserModel().getId(), postsList -> {
                    this.data = postsList;
                });
    }


    public List<Post> getData() {
        return data;
    }

}
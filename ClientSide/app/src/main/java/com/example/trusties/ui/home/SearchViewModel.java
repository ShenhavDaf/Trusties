package com.example.trusties.ui.home;

import androidx.lifecycle.ViewModel;

import com.example.trusties.model.Post;
import com.example.trusties.model.User;

import java.util.List;

public class SearchViewModel extends ViewModel {

    public List<User> usersData;

    public List<User> getUsersData() {
        return usersData;
    }

    /*----------------------------------------------------------*/

    List<Post> postsData;

    public List<Post> getPostsData() {
        return postsData;
    }
}
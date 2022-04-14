package com.example.trusties.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.trusties.model.Model;
import com.example.trusties.model.Post;

import java.util.List;

public class DashboardViewModel extends ViewModel {

    List<Post> data;

    public DashboardViewModel() {
        Model.instance.getAllPosts(postsList -> {
            this.data = postsList;
        });
    }

    public List<Post> getData() {
        return data;
    }

}
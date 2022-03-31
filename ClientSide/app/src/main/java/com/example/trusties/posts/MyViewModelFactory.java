package com.example.trusties.posts;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.trusties.model.Comment;

import java.util.List;

public class MyViewModelFactory implements ViewModelProvider.Factory {
    private String postId;
    List<Comment> data;

    public MyViewModelFactory(String param) {
        postId = param;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        DetailsPostViewModel vm= (DetailsPostViewModel) new DetailsPostViewModel(postId);
        this.data=vm.getData();
        return (T) vm;
    }
    public List<Comment> getData() {
        return data;
    }


}
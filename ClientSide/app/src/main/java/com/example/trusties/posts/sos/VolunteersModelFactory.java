package com.example.trusties.posts.sos;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.trusties.model.Comment;
import com.example.trusties.model.User;
import com.example.trusties.posts.DetailsPostViewModel;

import java.util.List;

public class VolunteersModelFactory implements ViewModelProvider.Factory {
    private String postId;
    List<User> data;

    public VolunteersModelFactory(String param) {
        postId = param;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        VolunteersViewModel vm= (VolunteersViewModel) new VolunteersViewModel(postId);
        this.data=vm.getData();
        return (T) vm;
    }

    public List<User> getData() {
        return data;
    }


}
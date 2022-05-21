package com.example.trusties.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.trusties.model.Model;
import com.example.trusties.model.Post;

import java.util.List;

public class HomeViewModel extends ViewModel {

    LiveData<List<Post>> data;

    public HomeViewModel() {
        data = Model.instance.getAll();
    }

    public LiveData<List<Post>> getData() {
        return data;
    }
}
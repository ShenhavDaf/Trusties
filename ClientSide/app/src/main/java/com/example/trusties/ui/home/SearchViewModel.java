package com.example.trusties.ui.home;

import androidx.lifecycle.ViewModel;

import com.example.trusties.model.User;

import java.util.List;

public class SearchViewModel extends ViewModel {

    List<User> data;

    public SearchViewModel() {
    }

    public List<User> getData() {
        return data;
    }

}
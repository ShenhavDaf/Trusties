package com.example.trusties.ui.profile;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.trusties.model.Model;
import com.example.trusties.model.User;
import com.google.gson.JsonObject;

import java.util.List;

public class ConnectionsViewModel extends ViewModel {

    List<JsonObject> data;

    //TODO: get all connections
    public ConnectionsViewModel() {
//        Model.instance.getAllUsers(userList -> {
//            Log.d("TAG","inside + " + userList.size());
//            this.data = userList;
//        });
    }

    public List<JsonObject> getData() {
        return data;
    }
}

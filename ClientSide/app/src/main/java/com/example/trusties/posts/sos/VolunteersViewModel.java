package com.example.trusties.posts.sos;

import androidx.lifecycle.ViewModel;

import com.example.trusties.model.Comment;
import com.example.trusties.model.Model;
import com.example.trusties.model.User;

import java.util.List;

public class VolunteersViewModel extends ViewModel {

    List<User> data;
    String sosId;

    public VolunteersViewModel(String sosId) {
        this.sosId= sosId;

        Model.instance.getSosVolunteers(sosId,VolunteersList ->{
            this.data =VolunteersList;
        });
    }

    public List<User> getData() {
        return data;
    }

}

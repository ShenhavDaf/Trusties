package com.example.trusties.ui.notifications;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.trusties.model.Model;
import com.example.trusties.model.Notification;
import com.example.trusties.model.Post;

import java.util.List;

public class NotificationsViewModel extends ViewModel {

    List<Notification> data;

    public NotificationsViewModel() {
        Log.d("TAG", "Model --> getAllNotifications");
        Model.instance.getAllNotifications(notificationsList -> {
            this.data = notificationsList;
        });
    }

    public List<Notification> getData() {
        return data;
    }
}
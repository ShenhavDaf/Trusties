package com.example.trusties.posts;

import androidx.lifecycle.ViewModel;

import com.example.trusties.model.Comment;
import com.example.trusties.model.Model;
import com.example.trusties.model.Post;

import java.util.List;

public class DetailsPostViewModel extends ViewModel {

    List<Comment> data;
    String postId;

    public DetailsPostViewModel(String postId) {
        this.postId = postId;

        Model.instance.getPostComments(postId,commentsList ->{
        this.data =commentsList;
        });

    }

    public List<Comment> getData() {
        return data;
    }

}

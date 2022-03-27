package com.example.trusties.posts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trusties.R;
import com.example.trusties.model.Model;
import com.example.trusties.model.Post;
import com.example.trusties.model.User;
import com.google.gson.JsonObject;


public class DetailsPostFragment extends Fragment {

    TextView titleEt;
    TextView timeEt;
    TextView authorEt;
    TextView descriptionEt;
    TextView statusEt;
    TextView roleEt;
    // TODO: Add location (SOS Call)
    Button editBtn;
    Button deleteBtn;
    String postId;
    ProgressBar progressBar;
    ImageView postImg;
    View line;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_post, container, false);
        progressBar = view.findViewById(R.id.postdetails_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        titleEt = view.findViewById(R.id.postdetails_title_tv);
        timeEt = view.findViewById(R.id.postdetails_time_tv);
        authorEt = view.findViewById(R.id.postdetails_author_tv);
        descriptionEt=view.findViewById(R.id.postdetails_description_tv);
        roleEt = view.findViewById(R.id.postdetails_role_tv);
        statusEt = view.findViewById(R.id.postdetails_status_tv);
        editBtn = view.findViewById(R.id.postdetails_edit_btn);
        deleteBtn = view.findViewById(R.id.postdetails_delete_btn);
        postImg = view.findViewById(R.id.postDetails_post_img);
        postImg.setVisibility(View.GONE);
        line = view.findViewById(R.id.postdetails_line);

        titleEt.setVisibility(View.INVISIBLE);
        timeEt.setVisibility(View.INVISIBLE);
        authorEt.setVisibility(View.INVISIBLE);
        descriptionEt.setVisibility(View.INVISIBLE);
        roleEt.setVisibility(View.INVISIBLE);
        statusEt.setVisibility(View.INVISIBLE);
        line.setVisibility(View.INVISIBLE);
        postImg.setVisibility(View.INVISIBLE);

        postId = DetailsPostFragmentArgs.fromBundle(getArguments()).getPostId();

        // get post by ID
        Model.instance.getPostById(postId, new Model.getPostByIdListener() {
            @Override
            public void onComplete(JsonObject post) {

                String title = post.get("title").toString().replace("\"", "");
                String description = post.get("description").toString().replace("\"", "");
//                String time = post.get("time").toString().replace("\"", "");
                String time = post.get("time").getAsString().substring(0, 16).replace("T", "  ").replace("-", "/");
                String senderId = post.get("sender").toString().replace("\"", "");
                String status = post.get("status").toString().replace("\"", "");
                String role = post.get("role").toString().replace("\"", "");
                displayPost(title, description, time,senderId, status, role);
                progressBar.setVisibility(View.GONE);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.instance.deletePost(postId, new Model.deletePostListener() {
                    @Override
                    public void onComplete() {
//                        Model.instance.refresh;//TODO: ADD REFRESH
                        Log.d("TAG", "delete");
                        Navigation.findNavController(v).navigateUp();
                    }
                });
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(DetailsPostFragmentDirections.actionDetailsPostFragmentToEditPostFragment(postId));
            }
        });

        return view;
    }

    public void displayPost(String title, String description, String time,String senderId, String status, String role)
    {
        Model.instance.findUserById(senderId, user -> {
            titleEt.setText(title);
            descriptionEt.setText(description);
            timeEt.setText(time);
            authorEt.setText(user.get("name").toString().replace("\"", "")); //TODO: find user by ID
            statusEt.setText(status);
            roleEt.setText(role);
    //        if(!post.getPhoto().contentEquals("")) {
    //            Picasso.get()
    //                    .load(post.getPhoto())
    //                    .into(postImg);
    //        }
            titleEt.setVisibility(View.VISIBLE);
            timeEt.setVisibility(View.VISIBLE);
            authorEt.setVisibility(View.VISIBLE);
            descriptionEt.setVisibility(View.VISIBLE);
            roleEt.setVisibility(View.VISIBLE);
            statusEt.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            postImg.setVisibility(View.VISIBLE);

            if(role == "SOS") {
                // TODO: Display specific details of SOS call
            }
        });

    }
}
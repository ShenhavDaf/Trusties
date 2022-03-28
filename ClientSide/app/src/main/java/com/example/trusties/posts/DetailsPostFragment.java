package com.example.trusties.posts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.trusties.R;
import com.example.trusties.model.Model;
import com.example.trusties.model.User;
import com.google.gson.JsonObject;

import java.util.HashMap;


public class DetailsPostFragment extends Fragment {

    TextView titleEt;
    TextView timeEt;
    TextView authorEt;
    TextView descriptionEt;
    TextView statusEt;
    TextView roleEt;
    EditText comment;
    // TODO: Add location (SOS Call)
    Button editBtn;
    Button deleteBtn;
    String postId;
    ProgressBar progressBar;
    ImageView postImg;
    ImageView imgUser;
    ImageView sendCommentBtn;
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
        comment = view.findViewById(R.id.postdetails_comment_et);
        sendCommentBtn = view.findViewById(R.id.postdetails_sendComment_btn);
        imgUser = view.findViewById(R.id.postdetails_imgUser_img);

        updateUI(View.INVISIBLE);

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

        deleteBtn.setOnClickListener(v -> Model.instance.deletePost(postId, () -> {
//                        Model.instance.refresh;//TODO: ADD REFRESH
            Log.d("TAG", "delete");
            Navigation.findNavController(v).navigateUp();
        }));

        editBtn.setOnClickListener(v -> Navigation.findNavController(v).navigate(DetailsPostFragmentDirections.actionDetailsPostFragmentToEditPostFragment(postId)));


//        adapter.setOnItemClickListener((v, position) -> {
//            String commentId = commentViewModel.getData().get(position).getId();
//            System.out.println("the postID is:  " + postId);
//            Navigation.findNavController(v).navigate(HomeFragmentDirections.actionNavigationHomeToDetailsPostFragment(postId));
//        });

        sendCommentBtn.setOnClickListener(v -> {
            String content = comment.getText().toString();
            User user = Model.instance.getCurrentUserModel();

            HashMap<String, String> map = new HashMap<>();
            map.put("postId", postId);
            map.put("sender", user.getEmail());
            map.put("content", content);
            map.put("currentTime", (new Long(0)).toString());

            Model.instance.addComment(map, () -> {
                // TODO: Add comment to local DB ??
                // TODO: Refresh recycle view
            });
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

            updateUI(View.VISIBLE);

            if(role == "SOS") {
                // TODO: Display specific details of SOS call
            }
        });

    }

    public void updateUI(int type) {
        titleEt.setVisibility(type);
        timeEt.setVisibility(type);
        authorEt.setVisibility(type);
        descriptionEt.setVisibility(type);
        roleEt.setVisibility(type);
        statusEt.setVisibility(type);
        line.setVisibility(type);
        postImg.setVisibility(type);
        sendCommentBtn.setVisibility(type);
        imgUser.setVisibility(type);
    }

}
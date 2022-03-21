package com.example.trusties.posts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.trusties.R;
import com.example.trusties.model.Model;
import com.google.gson.JsonObject;

import java.util.HashMap;


public class EditPostFragment extends Fragment {
    TextView titleEt;
    TextView descriptionEt;
    TextView titleTv;
    TextView descriptionTv;
    TextView tagsTv;
    TextView picturesTv;
    ProgressBar progressBar;

    Spinner dropdown;
    Button saveBtn;
    Button cancelBtn;
    ImageButton cameraBtn;
    ImageButton galleryBtn;
    String postId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);

        progressBar = view.findViewById(R.id.editpost_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        titleEt = view.findViewById(R.id.editpost_title_show);
        descriptionEt=view.findViewById(R.id.editpost_description_show);
        dropdown = view.findViewById(R.id.editpost_dropdown);
        saveBtn = view.findViewById(R.id.editpost_save_btn);
        cancelBtn = view.findViewById(R.id.editpost_cancel_btn);
        cameraBtn = view.findViewById(R.id.editpost_camera_btn);
        galleryBtn = view.findViewById(R.id.editpost_gallery_btn);
        titleTv = view.findViewById(R.id.editpost_title_tv);
        descriptionTv = view.findViewById(R.id.editpost_description_tv);
        tagsTv = view.findViewById(R.id.editpost_tags_tv);
        picturesTv = view.findViewById(R.id.editpost_pictures_tv);
        updateUI(View.INVISIBLE);
        
        postId = DetailsPostFragmentArgs.fromBundle(getArguments()).getPostId();
        //get post by ID
        Model.instance.getPostById(postId, new Model.getPostByIdListener() {
            @Override
            public void onComplete(JsonObject post) {
                
                String title = post.get("title").toString().replace("\"", "");
                String description = post.get("description").toString().replace("\"", "");
                displayPost(title, description); //TODO: add tags, image etc..
                progressBar.setVisibility(View.GONE);
                updateUI(View.VISIBLE);
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Model.instance.getPostById(postId, new Model.getPostByIdListener() {
                    @Override
                    public void onComplete(JsonObject post) {
                        savePost(view,titleEt.getText().toString(), descriptionEt.getText().toString(),postId);
                    }

                });
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });

        return view;
    }

    private void savePost(View view,String title, String description,String postId) {
        HashMap<String,String> map = new HashMap<>();
        map.put("title",title);
        map.put("description",description);
        Model.instance.editPost(map,postId, new Model.editPostListener() {
            @Override
            public void onComplete() {
                Navigation.findNavController(view).navigateUp();
            }
        });

    }

    private void displayPost(String title, String description) {

        titleEt.setText(title);
        titleEt.setTextSize(20);
        descriptionEt.setText(description);
        descriptionEt.setTextSize(20);
    }

    public void updateUI(int type) {
        titleEt.setVisibility(type);
        descriptionEt.setVisibility(type);
        dropdown.setVisibility(type);
        saveBtn.setVisibility(type);
        cancelBtn.setVisibility(type);
        cameraBtn.setVisibility(type);
        galleryBtn.setVisibility(type);
        titleTv.setVisibility(type);
        descriptionTv.setVisibility(type);
        tagsTv.setVisibility(type);
        picturesTv.setVisibility(type);
    }
}
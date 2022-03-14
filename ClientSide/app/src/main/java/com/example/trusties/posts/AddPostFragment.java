package com.example.trusties.posts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.trusties.R;

public class AddPostFragment extends Fragment {

    EditText postTitle, description;
    Spinner tags;
    ImageView image;
    ImageButton cameraBtn, galleryBtn;
    Button firstCircleBtn, secondCircleBtn, thirdCircleBtn, postBtn, sosBtn;
    ProgressBar progressBar;
    //TODO: location

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_post, container, false);


        postTitle = view.findViewById(R.id.newpost_title_et);
        description = view.findViewById(R.id.newpost_description_et);
        tags = view.findViewById(R.id.newpost_tags_spinner);
        image = view.findViewById(R.id.newpost_post_image);

        progressBar = view.findViewById(R.id.newpost_progressBar);
        progressBar.setVisibility(View.GONE);

        cameraBtn = view.findViewById(R.id.newpost_camera_btn);
        galleryBtn = view.findViewById(R.id.newpost_gallery_btn);
        firstCircleBtn = view.findViewById(R.id.newpost_firstcircle_btn);
        secondCircleBtn = view.findViewById(R.id.newpost_secondcircle_btn);
        thirdCircleBtn = view.findViewById(R.id.newpost_thirdcircle_btn);
        postBtn = view.findViewById(R.id.newpost_post_btn);
        sosBtn = view.findViewById(R.id.newpost_sos_btn);

        cameraBtn.setOnClickListener(v -> OpenCamera());
        galleryBtn.setOnClickListener(v -> OpenGallery());
        firstCircleBtn.setOnClickListener(v -> FindFirstCircle());
        secondCircleBtn.setOnClickListener(v -> FindSecondCircle());
        thirdCircleBtn.setOnClickListener(v -> FindThirdCircle());
        postBtn.setOnClickListener(v -> PostQuestion());
        sosBtn.setOnClickListener(v -> PostSOS());

        return view;
    }

    private void OpenCamera() {
        //TODO
    }

    private void OpenGallery() {
        //TODO
    }

    private void FindFirstCircle() {
        //TODO
    }

    private void FindSecondCircle() {
        // first + second
        //TODO
    }

    private void FindThirdCircle() {
        // second (Inside there is also the first) + third
        //TODO
    }

    private void PostQuestion() {
        // navigation - back to home
        //TODO
    }

    private void PostSOS() {
        // navigation - back to home
        //TODO
    }
}
package com.example.trusties.posts;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
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
import com.example.trusties.model.Model;
import com.example.trusties.model.User;
import com.example.trusties.ui.home.HomeFragment;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

public class AddPostFragment extends Fragment {

    EditText postTitle, description;
    Spinner tags;
    ImageView image;
    ImageButton cameraBtn, galleryBtn, carBtn, deliveryBtn, toolsBtn, houseBtn;
    Button firstCircleBtn, secondCircleBtn, thirdCircleBtn, postBtn, sosBtn;
    ProgressBar progressBar;
    //TODO: location

    Integer circle;

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
        postBtn.setOnClickListener(v -> PostQuestion(v));
        sosBtn.setOnClickListener(v -> postSOSCall(v));

        /*--------------------------categories---------------------------*/
        carBtn = view.findViewById(R.id.newpost_car_btn);
        deliveryBtn = view.findViewById(R.id.newpost_delivery_btn);
        toolsBtn = view.findViewById(R.id.newpost_tools_btn);
        houseBtn = view.findViewById(R.id.newpost_house_damage_btn);

        return view;
    }

    private void OpenCamera() {
        //TODO
    }

    private void OpenGallery() {
        //TODO
    }

    private void FindFirstCircle() {

        circle = 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            firstCircleBtn.setBackgroundColor(firstCircleBtn.getContext().getColor(R.color.titleColor));
            secondCircleBtn.setBackgroundColor(secondCircleBtn.getContext().getColor(R.color.lightGray));
            thirdCircleBtn.setBackgroundColor(thirdCircleBtn.getContext().getColor(R.color.lightGray));
        }
    }

    private void FindSecondCircle() {
        FindFirstCircle();
        circle = 2;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            secondCircleBtn.setBackgroundColor(secondCircleBtn.getContext().getColor(R.color.titleColor));
            thirdCircleBtn.setBackgroundColor(thirdCircleBtn.getContext().getColor(R.color.lightGray));
        }
    }

    private void FindThirdCircle() {
        FindSecondCircle();
        circle = 3;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            thirdCircleBtn.setBackgroundColor(thirdCircleBtn.getContext().getColor(R.color.titleColor));
        }
    }

    private void PostQuestion(View view) {
        createPost(view, "QUESTION");
    }

    private void postSOSCall(View view) {
        createPost(view, "SOS");

        // TODO: Check if all relevant details of SOS call are exist
    }

    private void createPost(View view, String type) {

        String currUserID = Model.instance.getCurrentUserModel().getId();

        Model.instance.getFriendsList(currUserID, circle, friendsList -> {
            System.out.println(circle + "--> " + friendsList);
        });

        String title = postTitle.getText().toString();
        String message = description.getText().toString();
//        User user = HomeFragment.connectedUser;
        User user = Model.instance.getCurrentUserModel();
        String email = user.getEmail().replace("\"", "");

        HashMap<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("description", message);
        map.put("email", email);
        map.put("role", type);

        Model.instance.addPost(map, () -> Navigation.findNavController(view).navigate(AddPostFragmentDirections.actionGlobalNavigationHome(user.getFullName())));
    }

}
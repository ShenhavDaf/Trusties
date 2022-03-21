package com.example.trusties.posts;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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
import java.util.HashMap;

public class AddPostFragment extends Fragment {

    EditText postTitle, description;
    Spinner tags;
    ImageView image;
    ImageButton cameraBtn, galleryBtn, carBtn, deliveryBtn, toolsBtn, houseBtn;
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
        postBtn.setOnClickListener(v -> PostQuestion(v));
        sosBtn.setOnClickListener(v -> PostSOS());

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

    private void PostQuestion(View view) {
        // navigation - back to home
        //TODO
        String title = postTitle.getText().toString();
        String message = description.getText().toString();
        User user = HomeFragment.connectedUser;
        String email= user.getEmail().replace("\"", "");
        HashMap<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("description", message);
        map.put("email", email);

        Model.instance.addPost(map, new Model.addPostListener() {
            @Override
            public void onComplete() {
                 Navigation.findNavController(view).navigate(AddPostFragmentDirections.actionAddPostFragmentToNavigationHome());
            }
        });


    }

    private void PostSOS() {
        // navigation - back to home
        //TODO
    }
}
package com.example.trusties.posts.sos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.trusties.R;
import com.example.trusties.databinding.FragmentDashboardBinding;
import com.example.trusties.model.Comment;
import com.example.trusties.model.Model;
import com.example.trusties.model.User;
import com.example.trusties.posts.DetailsPostFragmentArgs;
import com.example.trusties.posts.DetailsPostFragmentDirections;
import com.example.trusties.posts.DetailsPostModelFactory;
import com.example.trusties.posts.DetailsPostViewModel;
import com.example.trusties.ui.profile.ProfileFragment;
import com.example.trusties.ui.profile.ProfileFragmentDirections;
import com.google.gson.JsonObject;

import java.util.HashMap;


public class FeedbackFragment extends Fragment {

    private FeedbackFragment binding;
    TextView userName;
    TextView connections;
    User currUser;
    RatingBar rating_stars;
    ImageView userImage;
    Bitmap decodedByte;

    Button submit;

    String userId="";
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        userId = FeedbackFragmentArgs.fromBundle(getArguments()).getUserId();

    }


    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_feedback, container, false);
        userName = root.findViewById(R.id.feedback_profile_name);
        userImage = root.findViewById(R.id.feedback_profile_image);
        submit = root.findViewById(R.id.feedback_submit_btn);
        rating_stars = root.findViewById(R.id.feedback_ratingBar);

        currUser = Model.instance.getCurrentUserModel();
        Model.instance.findUserById(userId, new Model.findUserByIdListener() {
            @Override
            public void onComplete(JsonObject user) {
                userName.setText(user.get("name").toString().replace("\"", ""));

                if (user.get("photo") != null) {
                    String photoBase64 = user.get("photo").getAsString();
                    byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    userImage.setImageBitmap(decodedByte);
                }
            }
        });

        connections = root.findViewById(R.id.feedback_profile_connections);
        Model.instance.getFriendsList(currUser.getId(), 1, friendsList -> {
            connections.setText(friendsList.size() + " connections");
        });
        connections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(FeedbackFragmentDirections.actionFeedbackFragmentToOthersProfileFragment(userId));
            }
        });
        submit.setOnClickListener(v -> {
        //TODO: Need to get the correct number of selected stars- now it equals to 5 by defult ?
            int stars=rating_stars.getNumStars();

            HashMap<String, String> map = new HashMap<>();
            map.put("stars", String.valueOf(stars));

            Model.instance.rateMyHelp(userId, map, () -> {
                Navigation.findNavController(v).navigate(FeedbackFragmentDirections.actionGlobalNavigationHome(Model.instance.getCurrentUserModel().getFullName()));

            });
        });

        return  root;


    }
}
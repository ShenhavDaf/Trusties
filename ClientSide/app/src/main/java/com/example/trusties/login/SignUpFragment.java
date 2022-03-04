package com.example.trusties.login;

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

import com.example.trusties.R;

public class SignUpFragment extends Fragment {

    EditText fullName, email, password, verify, phone;
    ImageView userImg;
    ImageButton camera, gallery;
    Button joinBtn;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        fullName = view.findViewById(R.id.signup_name_et);
        email = view.findViewById(R.id.signup_email_et);
        password = view.findViewById(R.id.signup_password_et);
        verify = view.findViewById(R.id.signup_verify_et);
        phone = view.findViewById(R.id.signup_phone_et);
        userImg = view.findViewById(R.id.signup_user_image);

        camera = view.findViewById(R.id.signup_camera_btn);
        gallery = view.findViewById(R.id.signup_gallery_btn);
        joinBtn = view.findViewById(R.id.signup_join_btn);

        progressBar = view.findViewById(R.id.signup_progressBar);
        progressBar.setVisibility(View.GONE);

        return view;
    }
}
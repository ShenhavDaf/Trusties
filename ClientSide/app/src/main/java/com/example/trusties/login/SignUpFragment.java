package com.example.trusties.login;

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

import com.example.trusties.R;

import java.util.Random;

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

        progressBar = view.findViewById(R.id.signup_progressBar);
        progressBar.setVisibility(View.GONE);

        camera = view.findViewById(R.id.signup_camera_btn);
        gallery = view.findViewById(R.id.signup_gallery_btn);
        joinBtn = view.findViewById(R.id.signup_join_btn);

        camera.setOnClickListener(v -> openCamera());
        gallery.setOnClickListener(v -> openGallery());
        joinBtn.setOnClickListener(v -> Join(view));

        return view;
    }

    private void openCamera() {
        //TODO
    }

    private void openGallery() {
        //TODO
    }

    private void Join(View view) {
        progressBar.setVisibility(View.VISIBLE);
        joinBtn.setEnabled(false);

        String nameToSend = fullName.getText().toString();
        String emailToSend = email.getText().toString();

        //TODO: Code generation (5 digits) should be done on a server side
        final int min = 10000;
        final int max = 99999;
        String randomCode = String.valueOf(new Random().nextInt((max - min) + 1) + min);
        System.out.println("----------> The random code is " + randomCode);

        Navigation.findNavController(view).navigate(SignUpFragmentDirections.actionSignUpFragmentToVerificationFragment(nameToSend, emailToSend, randomCode));
    }
}
package com.example.trusties.login;

import android.app.AlertDialog;
import android.os.Bundle;

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
import android.widget.Toast;

import com.example.trusties.R;
import com.example.trusties.RetrofitInterface;

import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpFragment extends Fragment {

    EditText fullName, email, password, verify, phone;
    ImageView userImg;
    ImageButton camera, gallery;
    Button joinBtn;
    ProgressBar progressBar;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:4000";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

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
//        joinBtn.setOnClickListener(v -> Join(view));
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handleSignupDialog(v);
            }
        });

        return view;
    }

    private void handleSignupDialog(View view) {

        HashMap<String, String> map = new HashMap<>();

        map.put("name", fullName.getText().toString());
        map.put("email", email.getText().toString());
        map.put("password", password.getText().toString());

        final int min = 10000;
        final int max = 99999;
        String randomCode = String.valueOf(new Random().nextInt((max - min) + 1) + min);
        map.put("verifyCode", randomCode);
        System.out.println("----------> The random code is " + randomCode);

        Call<Void> call = retrofitInterface.executeSignup(map);
        Call<Void> call2 = retrofitInterface.verifyEmail(map);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 200) {
//                    Toast.makeText(getContext(),
//                            "Signed up successfully", Toast.LENGTH_LONG).show();
                    call2.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(getContext(),
                                    "Email sent!", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(),
                                    "oops.. didn't send email!", Toast.LENGTH_LONG).show();
                        }
                    });
                    Join(view,randomCode);

                } else if (response.code() == 400) {
                    Toast.makeText(getContext(),
                            "Already registered", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openCamera() {
        //TODO
    }

    private void openGallery() {
        //TODO
    }

    private void Join(View view, String randomCode) {
//        progressBar.setVisibility(View.VISIBLE);
//        joinBtn.setEnabled(false);

        String nameToSend = fullName.getText().toString();
        String emailToSend = email.getText().toString();

        Navigation.findNavController(view).navigate(SignUpFragmentDirections.actionSignUpFragmentToVerificationFragment(nameToSend, emailToSend, randomCode));
    }
}
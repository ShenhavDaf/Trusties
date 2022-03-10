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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
    String randomCodeFromServer;
    String localname, localmail, localpass;

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


//        joinBtn.setOnClickListener(v -> handleSignupDialog(v, localname, localmail, localpass));
joinBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        localname = fullName.getText().toString();
        localmail = email.getText().toString();
        localpass = password.getText().toString();
        handleSignupDialog(v, localname, localmail, localpass);
    }
});
        return view;
    }

    public void handleSignupDialog(View view, String name, String mail, String pass) {

        HashMap<String, String> map = new HashMap<>();

        map.put("name", name);
        map.put("email", mail);
        map.put("password", pass);

        Call<JsonObject> signUpCall = retrofitInterface.executeSignup(map);
        Call<Void> verifyCall = retrofitInterface.verifyEmail(map);

        signUpCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                randomCodeFromServer = response.body().get("randomCode").toString();
                if (response.code() == 200) {
                    verifyCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(getContext(), "Email sent!", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "oops.. didn't send email!", Toast.LENGTH_LONG).show();
                        }
                    });
//                    Join(view, randomCodeFromServer);
                    Navigation.findNavController(view).navigate(
                            SignUpFragmentDirections.actionSignUpFragmentToVerificationFragment(localname, localmail, localpass, randomCodeFromServer));

                } else if (response.code() == 400) {
                    Toast.makeText(getContext(), "Already registered", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openCamera() {
        //TODO
    }

    private void openGallery() {
        //TODO
    }

}
package com.example.trusties.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.trusties.CommonFunctions;
import com.example.trusties.MainActivity;
import com.example.trusties.R;
import com.example.trusties.model.Model;

public class LogInFragment extends Fragment {

    EditText email, password;
    TextView joinBtn;
    Button loginBtn;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        email = view.findViewById(R.id.login_email_et);
        password = view.findViewById(R.id.login_password_et);
        progressBar = view.findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.GONE);

        loginBtn = view.findViewById(R.id.login_btn);
        joinBtn = view.findViewById(R.id.login_joinbtn_tv);

        loginBtn.setOnClickListener(v -> Login());
        joinBtn.setOnClickListener(v -> Join(v));

        return view;
    }

    private void Login() {
        progressBar.setVisibility(View.VISIBLE);
        loginBtn.setEnabled(false);

        String localEmail = email.getText().toString();
        String localPassword = password.getText().toString();

//        if (!Patterns.EMAIL_ADDRESS.matcher(localEmail).matches()) {
//            email.setError("Please provide valid email");
//            email.requestFocus();
//
//            return;
//        }
//        if (localEmail.isEmpty()) {
//            email.setError("Please enter your Email");
//            email.requestFocus();
//            return;
//        }
//
//        if (localPassword.length() < 6) {
//            password.setError("Password length should be at least 6 characters");
//            password.requestFocus();
//            return;
//        }

        Model.instance.login(localEmail, localPassword, statusCode -> {
            if (statusCode == 200) {
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            } else if (statusCode == 400) {
                new CommonFunctions().myPopup(getContext(), "Error", "Incorrect email or password");
                progressBar.setVisibility(View.GONE);
                loginBtn.setEnabled(true);
            }
        },getContext());
    }

    private void Join(View view) {
        Navigation.findNavController(view).navigate(R.id.action_logInFragment_to_signUpFragment);
    }
}
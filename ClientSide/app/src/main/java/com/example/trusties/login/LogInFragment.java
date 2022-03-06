package com.example.trusties.login;

import android.app.AlertDialog;
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
import android.widget.TextView;

import com.example.trusties.R;

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

        loginBtn.setOnClickListener(v -> Login(view));
        joinBtn.setOnClickListener(v -> Join(view));


        return view;
    }

    private void Login(View view) {
        progressBar.setVisibility(View.VISIBLE);
        loginBtn.setEnabled(false);

        String localEmail = email.getText().toString().trim();
        String LocalPassword = password.getText().toString().trim();

        if(localEmail.equals("shenhav") && LocalPassword.equals("123")){
            Navigation.findNavController(view).navigate(R.id.action_logInFragment_to_navigation_home);
        }
        else {
            progressBar.setVisibility(View.GONE);
            loginBtn.setEnabled(true);

            String msg = "Username or password incorrect!!\nPlease try again 😊";

            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            builder.setNegativeButton("OK", (dialog, which) -> dialog.cancel());

            AlertDialog alert = builder.create();
            alert.setTitle("Error");
            alert.setMessage("\n" + msg + "\n");
            alert.show();
        }
    }

    private void Join(View view) {
        Navigation.findNavController(view).navigate(R.id.action_logInFragment_to_signUpFragment);
    }
}
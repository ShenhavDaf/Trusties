package com.example.trusties.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.InputType;
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
import com.example.trusties.model.User;
import com.google.gson.JsonObject;

import java.util.HashMap;


public class LogInFragment extends Fragment {

    EditText email, password;
    TextView joinBtn, forgotPassword;
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
        forgotPassword = view.findViewById(R.id.login_forgotPassword_tv);

        loginBtn.setOnClickListener(v -> Login(v));
        joinBtn.setOnClickListener(v -> Join(v));
        forgotPassword.setOnClickListener(v -> forgotMyPassword());

        return view;
    }

    /* ------------------------------- Functions ------------------------------------- */

    private void Login(View view) {
//        progressBar.setVisibility(View.VISIBLE);
//        loginBtn.setEnabled(false);

        String localEmail = email.getText().toString().trim().toLowerCase();
        String localPassword = password.getText().toString();

        if (localEmail.isEmpty()) {
            email.setError("Please enter your Email");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(localEmail).matches()) {
            email.setError("Please provide valid email");
            email.requestFocus();

            return;
        }

        if (localPassword.length() < 6) {
            password.setError("Password length should be at least 6 characters");
            password.requestFocus();
            return;
        }

        Model.instance.login(localEmail, localPassword, (statusCode, user) -> {
            if (statusCode == 200) {
                String localName = user.get("name").toString();
                String localPhone = user.get("phone").toString();
                if (user.get("verified").toString().equals("false")) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", localName);
                    map.put("email", localEmail);
                    map.put("password", localPassword);
                    map.put("phone", localPhone);
                    map.put("fragment", "LoginFragment");
                    Model.instance.signup(map, randomCodeFromServer -> {
                        setConnectedUser(localEmail);
                        Navigation.findNavController(view).navigate(
                                LogInFragmentDirections.actionLogInFragmentToVerificationFragment(localName.replace("\"", ""), localEmail.replace("\"", ""), randomCodeFromServer));
                    }, getContext());
                } else {
                    setConnectedUser(localEmail);
                    Intent myIntent = new Intent(getContext(), MainActivity.class);
//                    myIntent.putExtra("email",localEmail);
                    startActivity(myIntent);
                    getActivity().finish();
                }
            } else if (statusCode == 400) {
                new CommonFunctions().myPopup(getContext(), "Error", user.get("message").toString());
                progressBar.setVisibility(View.GONE);
                loginBtn.setEnabled(true);
            }
        }, getContext());
    }

    /* ------------------------------------------------------------------------------ */

    private void Join(View view) {
        Navigation.findNavController(view).navigate(R.id.action_logInFragment_to_signUpFragment);
    }

    /* ------------------------------------------------------------------------------ */

    @SuppressLint("ResourceAsColor")
    private void forgotMyPassword() {

        String title = "Send me password";
        String msg = "Enter the email address you sign up with and a new password will be sent to you.\n" +
                "After login we recommend updating the new password (by editing a profile)";

        EditText input = new EditText(getContext());
        input.setBackground(new ColorDrawable(Integer.valueOf(R.color.lightGray)));
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setView(R.layout.input_popup);

        builder.setView(input);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.setPositiveButton("Send", (dialog, which) -> {
            String userEmail = input.getText().toString();
            Model.instance.forgotPassword(userEmail, () -> { /*EMPTY*/ });
        });

        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.setMessage("\n" + msg + "\n");
        alert.show();

    }
    void setConnectedUser(String localEmail){
        Model.instance.findUserByEmail(localEmail, new Model.findUserByEmailListener() {
            @Override
            public void onComplete(JsonObject user) {
                Model.instance.setCurrentUserModel(new User(user.get("name").toString().replace("\"", ""), user.get("email").toString().replace("\"", ""), user.get("phone").toString().replace("\"", "")));
            }
        });

    }
}
package com.example.trusties.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trusties.CommonFunctions;
import com.example.trusties.R;
import com.example.trusties.RetrofitInterface;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VerificationFragment extends Fragment {

    TextView userName, userEmail, resendBtn;
    EditText verificationCodeEt;
    Button checkBtn;
    ProgressBar progressBar;

    String nameArg, emailArg, passArg, verifyCodeFromServer;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:4000";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        /*------------------------------ Arguments --------------------------------*/

        nameArg = VerificationFragmentArgs.fromBundle(getArguments()).getUserName();
        emailArg = VerificationFragmentArgs.fromBundle(getArguments()).getUserEmail();
        passArg = VerificationFragmentArgs.fromBundle(getArguments()).getUserPassword();
        verifyCodeFromServer = VerificationFragmentArgs.fromBundle(getArguments()).getVerifyCode();

        /*-------------------------------- View ----------------------------------*/

        View view = inflater.inflate(R.layout.fragment_verification, container, false);

        userName = view.findViewById(R.id.verification_username_tv);
        userEmail = view.findViewById(R.id.verification_user_email_tv);
        verificationCodeEt = view.findViewById(R.id.verification_code_et);
        progressBar = view.findViewById(R.id.verification_progressBar);
        progressBar.setVisibility(View.GONE);

        if (nameArg.equals("")) nameArg = "______";
        if (emailArg.equals("")) emailArg = "______";
        userName.setText(nameArg);
        userEmail.setText(emailArg);

        checkBtn = view.findViewById(R.id.verification_check_btn);
        resendBtn = view.findViewById(R.id.verification_resend_btn);
        checkBtn.setOnClickListener(v -> CheckCode(view, verificationCodeEt.getText().toString()));
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResendCode(v);
            }
        });

        return view;
    }

    private void CheckCode(View view, String code) {
//        progressBar.setVisibility(View.VISIBLE);
        checkBtn.setEnabled(false);
        resendBtn.setEnabled(false);

        if (code.equals(verifyCodeFromServer)) {
            Navigation.findNavController(view).navigate(R.id.action_verificationFragment_to_navigation_home);
        } else {
            String msg = "Verification code is incorrect!!\nPlease try again or resend code 😊";
            new CommonFunctions().myPopup(this.getContext(), "Error", msg);
            checkBtn.setEnabled(true);
            resendBtn.setEnabled(true);
        }
    }

    private void ResendCode(View view) {
        //TODO
//        new SignUpFragment().handleSignupDialog(view, nameArg, emailArg, passArg);
        HashMap<String, String> map = new HashMap<>();

        map.put("name", nameArg);
        map.put("email", emailArg);
        map.put("password", passArg);

        Call<Void> resendCall = retrofitInterface.resendEmail();
        Call<Void> verifyCall = retrofitInterface.verifyEmail(map);

        resendCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                System.out.println("---------------");
                if(response.code() == 200) {
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
                    CheckCode(view, verifyCodeFromServer);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

//        signUpCall.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                String newRandomCodeFromServer = response.body().get("randomCode").toString();
//                if (response.code() == 200) {

//                    Join(view, randomCodeFromServer);
//                    Navigation.findNavController(view).navigate(
//                            SignUpFragmentDirections.actionSignUpFragmentToVerificationFragment(localname, localmail, localpass, randomCodeFromServer));
//        CheckCode(view, verifyCodeFromServer);
//
//                } else if (response.code() == 400) {
//                    Toast.makeText(getContext(), "Already registered", Toast.LENGTH_LONG).show();
//                }
//            }

//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
    }
}
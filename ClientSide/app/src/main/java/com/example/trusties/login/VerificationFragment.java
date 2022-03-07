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

import com.example.trusties.CommonFunctions;
import com.example.trusties.R;

public class VerificationFragment extends Fragment {

    TextView userName, userEmail, resendBtn;
    EditText verificationCodeEt;
    Button checkBtn;
    ProgressBar progressBar;

    String nameArg, emailArg, verifyCodeFromServer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*------------------------------ Arguments --------------------------------*/

        nameArg = VerificationFragmentArgs.fromBundle(getArguments()).getUserName();
        emailArg = VerificationFragmentArgs.fromBundle(getArguments()).getUserEmail();
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
        resendBtn.setOnClickListener(v -> ResendCode());

        return view;
    }

    private void CheckCode(View view, String code) {
        progressBar.setVisibility(View.VISIBLE);
        checkBtn.setEnabled(false);
        resendBtn.setEnabled(false);

        if (code.equals(verifyCodeFromServer)) {
            Navigation.findNavController(view).navigate(R.id.action_verificationFragment_to_navigation_home);
        } else {
            String msg = "Verification code is incorrect!!\nPlease try again or resend code 😊";
            new CommonFunctions().myPopup(this.getContext(), msg);
        }
    }

    private void ResendCode() {
        //TODO
    }
}
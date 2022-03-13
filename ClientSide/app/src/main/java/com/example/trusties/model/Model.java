package com.example.trusties.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.http.Body;

public class Model {

    public static final Model instance = new Model();
    ModelServer modelServer = new ModelServer();
//    User connectedUser;

    public Executor executor = Executors.newFixedThreadPool(1);
    public Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());

    /* ---------------------------------------------------------------------------- */

    public interface loginListener {
        void onComplete(Integer statusCode);
    }

    public void login(String email, String password, loginListener listener, Context context) {
        modelServer.handleLoginDialog(email, password, listener,context);
    }

    /* ---------------------------------------------------------------------------- */

    public interface signupListener {
        void onComplete(String randomCodeFromServer);
    }

    public void signup(HashMap<String, String> map, signupListener listener, Context context) {
        modelServer.handleSignupDialog(map, listener, context);
    }

    /* ---------------------------------------------------------------------------- */

    public interface resendEmailListener {
        void onComplete(String randomCodeFromServer);
    }

    public void resendEmail(HashMap<String, String> map, resendEmailListener listener,Context context) {
        modelServer.resendEmail(map, listener,context);
    }

    public interface  verifiedUserListener{
        void onComplete(String str);
    }
    public void verifiedUser(@Body HashMap<String, String> map, verifiedUserListener listener, Context context) {
        modelServer.verifiedUser(map,listener,context);
    }

}

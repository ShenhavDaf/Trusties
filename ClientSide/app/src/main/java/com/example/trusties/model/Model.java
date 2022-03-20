package com.example.trusties.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import com.example.trusties.User;
import com.google.gson.JsonObject;

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
        void onComplete(Integer statusCode, JsonObject user);
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

    /* ---------------------------------------------------------------------------- */

    public interface  verifiedUserListener{
        void onComplete(String str);
    }
    public void verifiedUser(@Body HashMap<String, String> map, verifiedUserListener listener, Context context) {
        modelServer.verifiedUser(map,listener,context);
    }

    /* ---------------------------------------------------------------------------- */

    public interface  forgotPasswordListener{
        void onComplete();
    }
    public void forgotPassword(String email, forgotPasswordListener listener) {
        modelServer.forgotPassword(email,listener);
    }

    public interface findUserByEmailListener{
        void onComplete(JsonObject user);
    }
    public void findUserByEmail(String email, findUserByEmailListener listener){
        modelServer.findUserByEmail(email,listener);
    }
    
    public interface addPostListener{
        void onComplete();
    }
    
    public void addPost(HashMap<String, String> map, addPostListener listener)
    {
        modelServer.addPost(map,listener);
    }

}

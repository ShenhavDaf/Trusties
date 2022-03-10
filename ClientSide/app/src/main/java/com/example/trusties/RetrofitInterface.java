package com.example.trusties;

import com.example.trusties.login.LogInFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/auth/login")
    Call<Void> executeLogin(@Body HashMap<String, String> map);

    @POST("/auth/register")
    Call<JsonObject> executeSignup (@Body HashMap<String, String> map);

    @POST("/auth/verify")
    Call<Void> verifyEmail (@Body HashMap<String, String> map);

    @POST("/auth/resendEmail")
    Call<String> resendEmail ();


}

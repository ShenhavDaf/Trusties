package com.example.trusties;

import com.example.trusties.login.LogInFragment;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/auth/login")
    Call<Void> executeLogin(@Body HashMap<String, String> map);

    @POST("/auth/register")
    Call<Void> executeSignup (@Body HashMap<String, String> map);

    @POST("/auth/verify")
    Call<Void> verifyEmail (@Body HashMap<String, String> map);

    @GET("/auth/verify")
    Call<Void> verifyCode (@Body String code);


}

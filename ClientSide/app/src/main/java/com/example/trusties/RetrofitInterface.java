package com.example.trusties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {

    /*------------------------------------------Authentication----------------------------------------*/

    @POST("/auth/login")
    Call<JsonObject> executeLogin(@Body HashMap<String, String> map);

    @POST("/auth/register")
    Call<JsonObject> executeSignup(@Body HashMap<String, String> map);

    @POST("/auth/verify")
    Call<Void> verifyEmail(@Body HashMap<String, String> map);

    @POST("/auth/resendEmail")
    Call<String> resendEmail();

    @POST("/auth/afterVerify")
    Call<Void> verifiedUser(@Body HashMap<String, String> map);

    @GET("/auth/forgotPassword")
    Call<Void> forgotPassword(@Query("emailAddress") String emailAddress);

    @GET("/auth/findByEmail")
    Call<JsonObject> findUserByEmail(@Query("emailAddress") String emailAddress);

    @GET("/auth/findById")
    Call<JsonObject> findUserById(@Query("id") String userId);

    /*------------------------------------------Posts----------------------------------------*/

    @POST("/post/add")
    Call<Void> addPost(@Body HashMap<String, String> map);

    @GET("/post/allPosts")
    Call<JsonArray> getAllPosts();

    @GET("/post/{id}")
    Call<JsonObject> getPostById(@Path("id") String id);

    @POST("/post/delete/{id}")
    Call<Void> deletePost(@Path("id") String id);

    @POST("/post/edit/{id}")
    Call<Void> editPost(@Body HashMap<String, String> map, @Path("id") String id);


}

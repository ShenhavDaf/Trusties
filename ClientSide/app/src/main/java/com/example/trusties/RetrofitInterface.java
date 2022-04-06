package com.example.trusties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
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

    @GET("/post/allPosts")
    Call<JsonArray> getAllPosts(@Header("authorization") String accessToken);

    @POST("/post/add")
    Call<Void> addPost(@Header("authorization") String accessToken, @Body HashMap<String, String> map);

    @GET("/post/{id}")
    Call<JsonObject> getPostById(@Header("authorization") String accessToken, @Path("id") String id);

    @POST("/post/delete/{id}")
    Call<Void> deletePost(@Header("authorization") String accessToken, @Path("id") String id);

    @POST("/post/edit/{id}")
    Call<Void> editPost(@Header("authorization") String accessToken, @Body HashMap<String, String> map, @Path("id") String id);

    /*------------------------------------------Comments----------------------------------------*/

    @POST("/comment/add")
    Call<Void> addComment(@Header("authorization") String accessToken, @Body HashMap<String, String> map);

    @GET("/comment/allComments")
    Call<JsonArray> getAllComments(@Header("authorization") String accessToken);

    @GET("/comment/{id}/allComments")
    Call<JsonArray> getPostComments(@Header("authorization") String accessToken,@Path("id") String id);

    @GET("/comment/{id}")
    Call<Void> getCommentById(@Header("authorization") String accessToken);

    @POST("/comment/edit/{id}")
    Call<Void> editComment(@Header("authorization") String accessToken, @Body HashMap<String, String> map,@Path("id") String id);

    @POST("/comment/delete/{id}")
    Call<Void> deleteComment(@Header("authorization") String accessToken, @Path("id") String id);

}

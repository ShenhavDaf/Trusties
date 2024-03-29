package com.example.trusties;

import com.google.android.datatransport.runtime.dagger.MapKey;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface RetrofitInterface {

    /*------------------------------------------Authentication----------------------------------------*/

    @GET("/auth/getCurrUser")
    Call<JsonObject> getCurrUser();

    @POST("/auth/login")
    Call<JsonObject> executeLogin(@Body HashMap<String, String> map);

    @POST("/auth/register")
    Call<JsonObject> executeSignup(@Body HashMap<String, String> map);

    @POST("/auth/verify")
    Call<Void> verifyEmail(@Body HashMap<String, String> map);

    @POST("/auth/logout/{id}")
    Call<Void> logout(@Query("id") String id);

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

    @POST("/auth/editUser/{id}")
    Call<Void> editUser(@Header("authorization") String accessToken, @Body HashMap<String, String> map, @Path("id") String id);

    @GET("/auth/allUsers")
    Call<JsonArray> getAllUsers(@Header("authorization") String accessToken);
    /*------------------------------------------Users----------------------------------------*/

    @GET("/user/getFeed/{id}")
    Call<JsonArray> getMyRelatedPosts(@Header("authorization") String accessToken, @Path("id") String userId);

    @GET("/user/getFriendsList")
    Call<JsonArray> getFriendsList(@Query("id") String userID, @Query("circle") Integer circleNumber);

    @GET("/user/getWaitingList/{id}")
    Call<JsonArray> getWaitingList(@Query("id") String userID);

    @GET("/user/second/{id}")
    Call<JsonArray> getSecondCircle(@Query("id") String userID);

    @GET("/user/third/{id}")
    Call<JsonArray> getThirdCircle(@Query("id") String userID);

    @GET("/user/addFriendToMyContacts/{myId}/{hisId}")
    Call<Void> addFriendToMyContacts(@Query("myId") String myID, @Query("hisId") String hisID);

    @POST("/user/approveFriend/{myId}/{hisId}")
    Call<Void> approveFriend(@Query("myId") String myID, @Query("hisId") String hisID);

    @GET("/user/removeFriendFromMyContacts/{myId}/{hisId}")
    Call<Void> removeFriendFromMyContacts(@Query("myId") String myID, @Query("hisId") String hisID);

	@POST("/user/rateMyHelp/{id}")
    Call<Void> rateMyHelp(@Path("id") String id, @Body HashMap<String, String> map);

    @GET("/user/getRating/{id}")
    Call<JsonObject> getRating(@Query("id") String userId);
	
    /*------------------------------------------Posts----------------------------------------*/

    @GET("/post/allPosts")
    Call<JsonArray> getAllPosts(@Header("authorization") String accessToken);

    @POST("/post/add")
    Call<JsonObject> addPost(@Header("authorization") String accessToken, @Body HashMap<String, String> map);

    @GET("/post/{id}")
    Call<JsonObject> getPostById(@Header("authorization") String accessToken, @Path("id") String id);

    @POST("/post/delete/{id}")
    Call<Void> deletePost(@Header("authorization") String accessToken, @Path("id") String id);

    @POST("/post/edit/{id}")
    Call<JsonObject> editPost(@Header("authorization") String accessToken, @Body HashMap<String, String> map, @Path("id") String id);

    @GET("/post/MyPosts/{id}")
    Call<JsonArray> getMyPosts(@Header("authorization") String accessToken, @Path("id") String id);

    @POST("/post/addPhotosToPost/{id}")
    Call<Void> addPhotosToPost(@Header("authorization") String accessToken, @Body ArrayList<String> photos, @Path("id") String id);

    /*------------------------------------------Comments----------------------------------------*/

    @POST("/comment/add")
    Call<Void> addComment(@Header("authorization") String accessToken, @Body HashMap<String, String> map);

    @GET("/comment/allComments")
    Call<JsonArray> getAllComments(@Header("authorization") String accessToken);

    @GET("/comment/{id}/allComments")
    Call<JsonArray> getPostComments(@Header("authorization") String accessToken, @Path("id") String id);

    @GET("/comment/{id}")
    Call<Void> getCommentById(@Header("authorization") String accessToken);

    @GET("/comment/isUserRatedNegative/{commentId}")
    Call<JsonObject> isUserRatedNegative(@Header("authorization") String accessToken,@Query("commentId") String commentId,@Query("userId") String userId);

    @GET("/comment/isUserRatedPositive/{commentId}")
    Call<JsonObject> isUserRatedPositive(@Header("authorization") String accessToken,@Query("commentId") String commentId,@Query("userId") String userId);

    @POST("/comment/edit/{id}")
    Call<Void> editComment(@Header("authorization") String accessToken, @Body HashMap<String, String> map, @Path("id") String id);

    @POST("/comment/delete/{id}")
    Call<Void> deleteComment(@Header("authorization") String accessToken, @Path("id") String id);

    @POST("/comment/up/{id}")
    Call<Void> upComment(@Header("authorization") String accessToken, @Path("id") String id, @Body HashMap<String, String> map);

    @POST("/comment/down/{id}")
    Call<Void> downComment(@Header("authorization") String accessToken, @Path("id") String id, @Body HashMap<String, String> map);

    /*------------------------------------------SOS----------------------------------------*/

	@POST("/sos/add")
    Call<JsonObject> addSos(@Header("authorization") String accessToken, @Body HashMap<String, String> map);

    @POST("/sos/approveVolunteer/{id}")
    Call<Void> approveVolunteer(@Header("authorization") String accessToken, @Path("id") String id, @Body HashMap<String, String> map);

    @POST("/sos/volunteer/{id}")
    Call<Void> volunteer(@Header("authorization") String accessToken, @Path("id") String id, @Body HashMap<String, String> map);

    @POST("/sos/cancelVolunteer/{id}")
    Call<Void> cancelVolunteer(@Header("authorization") String accessToken, @Path("id") String id, @Body HashMap<String, String> map);

    @GET("/sos/getSosVolunteers/{id}")
    Call<JsonArray> getSosVolunteers(@Header("authorization") String accessToken, @Query("id") String id);

    @POST("/sos/closeSos/{id}")
    Call<Void> closeSos(@Header("authorization") String accessToken, @Path("id") String id);

    @GET("/sos/getApprovedVolunteer/{id}")
    Call<JsonObject> getApprovedVolunteer(@Header("authorization") String accessToken, @Path("id") String id);

    /*------------------------------------------Notifications----------------------------------------*/

    @GET("/notification/allNotifications")
    Call<JsonArray> getAllNotifications(@Header("authorization") String accessToken);

    @GET("/notification/allNotificationsByID/{id}")
    Call<JsonArray> getAllNotificationsByID(@Header("authorization") String accessToken, @Path("id") String id);

    @POST("/notification/add")
    Call<Void> addNotification(@Header("authorization") String accessToken, @Body HashMap<String, String> map);

    @POST("/notification/sendNotification")
    Call<Void> sendNotification(@Header("authorization") String accessToken, @Body HashMap<String, String> map);

    @GET("/notification/numberOfNewNotifications/{id}")
    Call<JsonObject> numberOfNewNotifications(@Header("authorization") String accessToken, @Path("id") String userID, @Query("numFromDB") Integer numFromDB);

    @GET("/notification/updateUserNotifications/{id}")
    Call<JsonObject> updateUserNotifications(@Header("authorization") String accessToken, @Path("id") String userID, @Query("numFromDB") Integer numFromDB);
}

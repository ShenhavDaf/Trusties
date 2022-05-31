package com.example.trusties.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.example.trusties.CommonFunctions;
import com.example.trusties.MyApplication;
import com.example.trusties.RetrofitInterface;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ModelServer {

    private final RetrofitInterface retrofitInterface;
    final private static String BASE_URL = "http://10.0.2.2:4000";
    //final private static String BASE_URL = "http://193.106.55.119:4000";
    private String accessToken;
    private String firebaseToken;

    public ModelServer() {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }


    public interface allPostsListener {
        void onComplete(List<Post> postsList);
    }

    /* ------------------------------------------------------------------------- */

    public void handleLoginDialog(String email, String password, Model.loginListener listener, Context context) {

        FirebaseApp.initializeApp(MyApplication.getContext());
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            firebaseToken = task.getResult();

            HashMap<String, String> map = new HashMap<>();
            map.put("email", email);
            map.put("password", password);
            map.put("firebaseToken", firebaseToken);

            Log.d("TAG", "firebase token ---- " + firebaseToken);

            Call<JsonObject> call = retrofitInterface.executeLogin(map);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    JsonObject user = new JsonObject();
                    String resMessage = "";

                    if (response.code() == 200) {
                        accessToken = "JWT " + response.body().get("accessToken").getAsString();
                        user = response.body().get("user").getAsJsonObject();
                        User.create(user);
                        resMessage = "user exists";
                    } else if (response.code() == 400) {
                        JSONObject jObjError = null;
                        try {
                            jObjError = new JSONObject(response.errorBody().string());
                            resMessage = jObjError.get("error").toString();
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }

                    user.addProperty("message", resMessage);
                    listener.onComplete(response.code(), user);
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    new CommonFunctions().myPopup(context, "Error", t.getMessage());
                }
            });
        });

    }

    /* ------------------------------------------------------------------------- */

    public void handleSignupDialog(HashMap<String, String> map, Model.signupListener listener, Context context) {

        Call<JsonObject> signUpCall = retrofitInterface.executeSignup(map);
        Call<Void> verifyCall = retrofitInterface.verifyEmail(map);

        signUpCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    accessToken = "JWT " + response.body().get("accessToken").getAsString();
                    String randomCodeFromServer = response.body().get("randomCode").toString();
                    System.out.println(randomCodeFromServer);

                    verifyCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            listener.onComplete(randomCodeFromServer);

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            new CommonFunctions().myPopup(context, "oops..", "didn't send email!");
                        }
                    });
                } else if (response.code() == 400) {
                    String msg = "The email address is already in use by another user";
                    new CommonFunctions().myPopup(context, "Error", msg);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                new CommonFunctions().myPopup(context, "Error", t.getMessage());
            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void resendEmail(HashMap<String, String> map, Model.resendEmailListener listener, Context context) {

        Call<String> resendCall = retrofitInterface.resendEmail();
        Call<Void> verifyCall = retrofitInterface.verifyEmail(map);

        resendCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String randomCodeFromServer = response.body();
                if (response.code() == 200) {
                    verifyCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            listener.onComplete(randomCodeFromServer);
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            new CommonFunctions().myPopup(context, "oops..", "didn't send email!");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                new CommonFunctions().myPopup(context, "Error", t.getMessage());
            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void verifiedUser(HashMap<String, String> map, Model.verifiedUserListener listener, Context context) {
        Call<Void> afterVerify = retrofitInterface.verifiedUser(map);
        afterVerify.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete("true");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void forgotPassword(String email, Model.forgotPasswordListener listener) {

        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);

        Call<Void> forgotPassword = retrofitInterface.forgotPassword(email);
        //TODO: don't know if we need "verifyCall"
//        Call<Void> verifyCall = retrofitInterface.verifyEmail(map);

        forgotPassword.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void findUserByEmail(String email, Model.findUserByEmailListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);

        Call<JsonObject> findUser = retrofitInterface.findUserByEmail(email);

        findUser.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("TAG", response.body().get("name").toString());
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void findUserById(String id, Model.findUserByIdListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", id);

        Call<JsonObject> findUser = retrofitInterface.findUserById(id);

        findUser.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void getFriendsList(String userID, Integer circle, Model.friendsListListener listener) {
        retrofitInterface.getFriendsList(userID, circle).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                System.out.println("--- failed\n" + t.getMessage());
            }
        });
    }
    /* ------------------------------------------------------------------------- */

    public void getMyRelatedPosts(String userID, Model.getMyRelatedPostsListener listener) {
        retrofitInterface.getMyRelatedPosts(accessToken,userID).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                System.out.println("--- failed\n" + t.getMessage());
            }
        });
    }


    /* ------------------------------------------------------------------------- */

    public void addPost(HashMap<String, String> map, Model.addPostListener listener) {

        Call<JsonObject> add = retrofitInterface.addPost(accessToken, map);

        add.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void addPhotosToPost(ArrayList<String> photos, String id, Model.addPhotosToPostListener listener) {

        Call<Void> add = retrofitInterface.addPhotosToPost(/*accessToken,*/ photos, id);

        add.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("TAG", t.getMessage());
            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void addSos(HashMap<String, String> map, Model.addSosListener listener) {

        Call<JsonObject> add = retrofitInterface.addSos(accessToken, map);

        add.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void getAllPosts(allPostsListener listener) {
//        Model.instance.postsListLoadingState.setValue(Model.LoadingState.loading);

        System.out.println("---------------1---------------");
//        retrofitInterface.getAllPosts().enqueue(new Callback<JsonArray>() {
        retrofitInterface.getAllPosts(accessToken).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                System.out.println("---------------2---------------");
                String currentUserModel = Model.instance.getCurrentUserModel().userID; //V
                Log.d("TAG", " curr user ~~~~" + currentUserModel);
                List<Post> filteredList = new ArrayList<>();
                Log.d("TAG", " response.body ~~~~" + response.body());
                List<Post> list = new ArrayList<>();
                for (JsonElement element : response.body()) {
                    if (!element.getAsJsonObject().get("isDeleted").getAsBoolean()) {
                        list.add(Post.create(element.getAsJsonObject()));
                    }
                }

//                if (list.isEmpty())
//                    Model.instance.postsListLoadingState.setValue(Model.LoadingState.loaded);

                for (Post post : list) {
                    System.out.println("---------------3---------------");
//                    Model.instance.postsListLoadingState.setValue(Model.LoadingState.loading);
                    getFriendsList(post.getAuthorID(), post.getCircle(), friendsList -> {

                        if (post.getAuthorID().equals(currentUserModel))
                            filteredList.add(post);
                        else {
                            for (JsonElement friend : friendsList) {
                                findUserById(friend.toString().replace("\"", ""), user -> System.out.println("888 " + user.get("name")));
                                if (friend.toString().replace("\"", "").equals(currentUserModel)) {
                                    filteredList.add(post);
                                }
                            }
                        }
                    });
//                    Model.instance.postsListLoadingState.setValue(Model.LoadingState.loaded);
                    System.out.println("---------------4---------------");
                }

//                if (Model.instance.getPostsListLoadingState().getValue() == Model.LoadingState.loaded) {
                System.out.println("---------------5---------------");
                Collections.reverse(filteredList);
                listener.onComplete(filteredList);
//                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
            }
        });

    }

    /* ------------------------------------------------------------------------- */

    public void getPostById(String postId, Model.getPostByIdListener listener) {

        Call<JsonObject> postDetails = retrofitInterface.getPostById(accessToken, postId);
        postDetails.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    /* ------------------------------------------------------------------------- */

    public void deletePost(String postId, Model.deletePostListener listener) {
        Call<Void> deletePost = retrofitInterface.deletePost(accessToken, postId);

        deletePost.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void editPost(HashMap<String, String> map, String postId, Model.editPostListener listener) {
        Call<JsonObject> editPost = retrofitInterface.editPost(accessToken, map, postId);

        editPost.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void addComment(HashMap<String, String> map, Model.addCommentListener listener) {
        Log.d("TAG", map.get("content"));
        Call<Void> add = retrofitInterface.addComment(accessToken, map);

        add.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });

    }

    /* ------------------------------------------------------------------------- */

//    public void getAllComments(Model.allCommentsListener listener) {
//
//        retrofitInterface.getAllComments(accessToken).enqueue(new Callback<JsonArray>() {
//            @Override
//            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
//
//                List<Comment> list = new ArrayList<>();
//                for (JsonElement element : response.body()) {
//                    if (!element.getAsJsonObject().get("isDeleted").getAsBoolean())
//                        list.add(Comment.create(element.getAsJsonObject()));
//                }
//
//                Collections.reverse(list);
//                listener.onComplete(list);
//            }
//
//            @Override
//            public void onFailure(Call<JsonArray> call, Throwable t) {
//            }
//        });
//    }

    /* ------------------------------------------------------------------------- */

    public void getPostComments(String postId, Model.allCommentsListener listener) {

        retrofitInterface.getPostComments(accessToken, postId).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                List<Comment> list = new ArrayList<>();
                for (JsonElement element : response.body()) {
                    if (!element.getAsJsonObject().get("isDeleted").getAsBoolean())
                        list.add(Comment.create(element.getAsJsonObject()));
                }
                listener.onComplete(list);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
            }
        });
    }

    /* ------------------------------------------------------------------------- */
    public void editComment(HashMap<String, String> map, String id, Model.editCommentListener listener) {

        Call<Void> editComment = retrofitInterface.editComment(accessToken, map, id);


        editComment.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
    /* ------------------------------------------------------------------------- */

    public void deleteComment(String id, Model.deleteCommentListener listener) {

        Call<Void> deleteComment = retrofitInterface.deleteComment(accessToken, id);

        deleteComment.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void upComment(String id, HashMap<String, String> map, Model.upCommentListener listener) {

        Call<Void> deleteComment = retrofitInterface.upComment(accessToken, id, map);

        deleteComment.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    /* ------------------------------------------------------------------------- */
    public void downComment(String id, HashMap<String, String> map, Model.downCommentListener listener) {

        Call<Void> deleteComment = retrofitInterface.downComment(accessToken, id, map);

        deleteComment.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    /* ------------------------------------------------------------------------- */
    public void getMyPosts(String id, Model.getMyPostsListener listener) {

        Call<JsonArray> getMyPosts = retrofitInterface.getMyPosts(accessToken, id);

        getMyPosts.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                List<Post> list = new ArrayList<>();
                for (JsonElement element : response.body()) {
                    if (!element.getAsJsonObject().get("isDeleted").getAsBoolean())
                        list.add(Post.create(element.getAsJsonObject()));
                }


                Collections.reverse(list);
                listener.onComplete(list);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void editUser(HashMap<String, String> map, String id,Context context, Model.editUserListener listener) {
        String userId = Model.instance.getCurrentUserModel().userID;
        Call<Void> editUser = retrofitInterface.editUser(accessToken, map, userId);

        editUser.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if(response.code()== 400)
                {
                    Log.d("TAG","inside wrong curr password");
                    String msg = "current password is not right!";
                    new CommonFunctions().myPopup(context, "Error", msg);
                }
                else {
                    listener.onComplete();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("--- failed\n" + t.getMessage());

            }
        });

    }


    /* ------------------------------------------------------------------------- */

    public void getAllUsers(Model.allUsersListener listener) {

        retrofitInterface.getAllUsers(accessToken).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                List<User> list = new ArrayList<>();
                for (JsonElement element : response.body()) {
                    if (element.getAsJsonObject().get("verified").getAsBoolean())
                        list.add(User.create(element.getAsJsonObject()));
                }

                listener.onComplete(list);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void getSecondCircle(String userID, Model.secondCircleListener listener) {
        retrofitInterface.getSecondCircle(userID).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                System.out.println("--- failed\n" + t.getMessage());
            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void getThirdCircle(String userID, Model.thirdCircleListener listener) {
        retrofitInterface.getThirdCircle(userID).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                System.out.println("--- failed\n" + t.getMessage());
            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void addFriendToMyContacts(String myID, String hisID, Model.addFriendListener listener) {

        retrofitInterface.addFriendToMyContacts(myID, hisID).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void removeFriendFromMyContacts(String myID, String hisID, Model.removeFriendListener listener) {

        retrofitInterface.removeFriendFromMyContacts(myID, hisID).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    /* ------------------------------------------------------------------------- */
    public void encodeBitMapImg(Bitmap imageBitmap, Model.encodeBitMapImgListener listener) {

        Bitmap scaledBitmap = getScaledBitmap(imageBitmap, 450, 450);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos);
        byte[] bitmapdata = bos.toByteArray();
        String encodedImage = Base64.encodeToString(bitmapdata, Base64.NO_WRAP);
        Log.d("TAG", "encoded" + encodedImage);
        listener.onComplete(encodedImage);

    }

    private Bitmap getScaledBitmap(Bitmap b, int reqWidth, int reqHeight) {
        int bWidth = b.getWidth();
        int bHeight = b.getHeight();

        int nWidth = bWidth;
        int nHeight = bHeight;

        if (nWidth > reqWidth) {
            int ratio = bWidth / reqWidth;
            if (ratio > 0) {
                nWidth = reqWidth;
                nHeight = bHeight / ratio;
            }
        }

        if (nHeight > reqHeight) {
            int ratio = bHeight / reqHeight;
            if (ratio > 0) {
                nHeight = reqHeight;
                nWidth = bWidth / ratio;
            }
        }

        return Bitmap.createScaledBitmap(b, nWidth, nHeight, true);
    }

    /* ------------------------------------------------------------------------- */

    public void addNotification(HashMap<String, String> map, Model.addNotificationListener listener) {
        Log.d("TAG", "ModelServer --> add notification");
        Call<Void> notification = retrofitInterface.addNotification(accessToken, map);

        notification.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }

        });
    }
    /* ------------------------------------------------------------------------- */


    public void approveVolunteer(String id, HashMap<String, String> map, Model.approveVolunteerListener listener) {

        Call<Void> approveVolunteer_retrofit = retrofitInterface.approveVolunteer(accessToken, id, map);

        approveVolunteer_retrofit.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }


    /* ------------------------------------------------------------------------- */

    public void getSosVolunteers(String id, Model.getSosVolunteersListener listener) {

        Call<JsonArray> getSosVolunteers_retrofit = retrofitInterface.getSosVolunteers(accessToken, id);
        getSosVolunteers_retrofit.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                List<User> list = new ArrayList<>();

                if (response.body() != null) {
                    for (JsonElement element : response.body()) {
                        list.add(User.create(element.getAsJsonObject()));
                    }
                }
                listener.onComplete(list);
            }


            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
            }
        });
    }
    /* ------------------------------------------------------------------------- */


    public void cancelVolunteer(String id, HashMap<String, String> map, Model.cancelVolunteerListener listener) {

        Call<Void> cancelVolunteer_retrofit = retrofitInterface.cancelVolunteer(accessToken, id, map);

        cancelVolunteer_retrofit.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void volunteer(String id, HashMap<String, String> map, Model.volunteerListener listener) {

        Call<Void> volunteer_retrofit = retrofitInterface.volunteer(accessToken, id, map);

        volunteer_retrofit.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
    /* ------------------------------------------------------------------------- */

    public void getAllNotifications(Model.allNotificationsListener listener) {
        String currentUserModel = Model.instance.getCurrentUserModel().userID;
        List<Notification> filteredList = new ArrayList<>();

        retrofitInterface.getAllNotifications(accessToken).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                List<Notification> list = new ArrayList<>();
                for (JsonElement element : response.body()) {
                    list.add(Notification.create(element.getAsJsonObject()));
                }

                for (Notification notification : list) {
                    if (notification.getAuthorID().equals(currentUserModel) &&
                            (notification.getType().equals("comment") || notification.getType().equals("like")))
                        filteredList.add(notification);
                    else {
                        getFriendsList(notification.getAuthorID(), Integer.valueOf(notification.getCircle()), friendsList -> {
                            for (JsonElement friend : friendsList) {
                                if (friend.toString().replace("\"", "").equals(currentUserModel)) {
                                    filteredList.add(notification);
                                }
                            }
                        });
                    }

                }
                Collections.reverse(list);
                listener.onComplete(list);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
            }
        });

    }
    /* ------------------------------------------------------------------------- */

    public void sendNotification(HashMap<String, String> map, Model.sendNotificationListener listener) {
        retrofitInterface.sendNotification(accessToken, map).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("TAG", "send notification successfully");
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("TAG", t.getMessage());
                Log.d("TAG", "send notification failed");
            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void closeSos(String id, Model.closeSosListener listener) {


        Call<Void> closeSosRetro = retrofitInterface.closeSos(accessToken, id);


        closeSosRetro.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
    /* ------------------------------------------------------------------------- */

    public void getApprovedVolunteer(String id, Model.getApprovedVolunteerListener listener) {

        Call<JsonObject> retrofit = retrofitInterface.getApprovedVolunteer(accessToken, id);
        retrofit.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
    /* ------------------------------------------------------------------------- */

    public void rateMyHelp(String userId, HashMap<String, String> map, Model.rateMyHelpListener listener) {


        Call<Void> rateMyHelpRetro = retrofitInterface.rateMyHelp(userId, map);


        rateMyHelpRetro.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void getRating(String id, Model.getRatingListener listener) {

        Call<JsonObject> retrofit = retrofitInterface.getRating(id);
        retrofit.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
    /* ------------------------------------------------------------------------- */


    public void signOut(String userId, Model.signOutListener listener) {
        Call<Void> signOut = retrofitInterface.logout(userId);
        signOut.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("TAG"," inside logout - client side");
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public int flag = 0;

    public void isSignedIn(Model.isSignedInListener listener) {
        Call<JsonObject> call = retrofitInterface.getCurrUser();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                Log.d("TAG", response.body().toString());
                if(!response.body().get("id").toString().replace("\"","").equals("null")) {
                    Log.d("TAG", response.body().get("id").toString().replace("\"",""));
                    Log.d("TAG", "yes! signed in!");
                    retrofitInterface.findUserById(response.body().get("id").toString().replace("\"","")).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response2) {
                            Log.d("TAG", "bodyydydy" + response2.body());
                            User usr = new User(response.body().get("id").toString().replace("\"",""), response2.body().get("name").toString().replace("\"",""), response2.body().get("email").toString().replace("\"",""),
                                    response2.body().get("phone").toString().replace("\"",""));
                            Model.instance.setCurrentUserModel(usr);
                            accessToken = "JWT " + response.body().get("accessToken").getAsString();
                        }
                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

                        }
                    });

                    flag = 1;
                }
                listener.onComplete(flag);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }



}

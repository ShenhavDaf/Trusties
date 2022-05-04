package com.example.trusties.model;

import android.content.Context;
import android.util.Log;

import com.example.trusties.CommonFunctions;
import com.example.trusties.RetrofitInterface;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

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

    public ModelServer() {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    /* ------------------------------------------------------------------------- */

    public void handleLoginDialog(String email, String password, Model.loginListener listener, Context context) {

        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("password", password);


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

    public void addPost(HashMap<String, String> map, Model.addPostListener listener) {
        Call<Void> add = retrofitInterface.addPost(accessToken, map);

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

    public void getAllPosts(Model.allPostsListener listener) {

        retrofitInterface.getAllPosts(accessToken).enqueue(new Callback<JsonArray>() {
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
        Call<Void> editPost = retrofitInterface.editPost(accessToken, map, postId);

        editPost.enqueue(new Callback<Void>() {
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

    public void getAllComments(Model.allCommentsListener listener) {

        retrofitInterface.getAllComments(accessToken).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                List<Comment> list = new ArrayList<>();
                for (JsonElement element : response.body()) {
                    if (!element.getAsJsonObject().get("isDeleted").getAsBoolean())
                        list.add(Comment.create(element.getAsJsonObject()));
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


    //Chnage to MAP instead of String.
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

    public void editUser(HashMap<String, String> map, String id, Model.editUserListener listener) {
        String userId = Model.instance.getCurrentUserModel().userID;
        Call<Void> editUser = retrofitInterface.editUser(accessToken, map, userId);

        editUser.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

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
}

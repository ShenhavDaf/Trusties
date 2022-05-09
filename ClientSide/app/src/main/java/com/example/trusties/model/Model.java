package com.example.trusties.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;

import androidx.core.os.HandlerCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.http.Body;

public class Model {

    public static final Model instance = new Model();
    ModelServer modelServer = new ModelServer();

    public Executor executor = Executors.newFixedThreadPool(1);
    public Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());


    /* ---------------------------------------------------------------------------- */
    User currentUserModel;

    public User getCurrentUserModel() {
        return currentUserModel;
    }

    public void setCurrentUserModel(User currentUserModel) {
        this.currentUserModel = currentUserModel;
    }

    /* ---------------------------------------------------------------------------- */

    public interface loginListener {
        void onComplete(Integer statusCode, JsonObject user);
    }

    public void login(String email, String password, loginListener listener, Context context) {
        modelServer.handleLoginDialog(email, password, listener, context);
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

    public void resendEmail(HashMap<String, String> map, resendEmailListener listener, Context context) {
        modelServer.resendEmail(map, listener, context);
    }

    /* ---------------------------------------------------------------------------- */

    public interface verifiedUserListener {
        void onComplete(String str);
    }

    public void verifiedUser(@Body HashMap<String, String> map, verifiedUserListener listener, Context context) {
        modelServer.verifiedUser(map, listener, context);
    }

    /* ---------------------------------------------------------------------------- */

    public interface forgotPasswordListener {
        void onComplete();
    }

    public void forgotPassword(String email, forgotPasswordListener listener) {
        modelServer.forgotPassword(email, listener);
    }

    /* ---------------------------------------------------------------------------- */

    public interface findUserByEmailListener {
        void onComplete(JsonObject user);
    }

    public void findUserByEmail(String email, findUserByEmailListener listener) {
        modelServer.findUserByEmail(email, listener);
    }

    /* ---------------------------------------------------------------------------- */
    public interface findUserByIdListener {
        void onComplete(JsonObject user);
    }

    public void findUserById(String id, findUserByIdListener listener) {
        modelServer.findUserById(id, listener);
    }

    /* ---------------------------------------------------------------------------- */
    public interface friendsListListener {
        void onComplete(JsonArray friendsList);
    }

    public void getFriendsList(String userID, Integer circle, friendsListListener listener) {
        modelServer.getFriendsList(userID, circle, listener);
    }

    /* ---------------------------------------------------------------------------- */

    public interface addPostListener {
        void onComplete();
    }

    public void addPost(HashMap<String, String> map, addPostListener listener) {
        modelServer.addPost(map, listener);
    }

    /* ---------------------------------------------------------------------------- */

    public interface allPostsListener {
        void onComplete(List<Post> postsList);
    }

    public void getAllPosts(allPostsListener listener) {
        modelServer.getAllPosts(listener);
    }

//    MutableLiveData<List<Post>> allPostsList = new MutableLiveData<List<Post>>();
//
//    public LiveData<List<Post>> getAllPosts() {
//        if (allPostsList.getValue() == null) {
//            refreshPostsList();
//        }
//        return allPostsList;
//    }
//
//    public void refreshPostsList() {
//
//        /*---------- firebase - get all updates since localUpdateDate ----------*/
//        modelServer.getAllPosts(new ModelServer.GetAllPostsListener() {
//
//            @Override
//            public void onComplete(List<Post> list) {
//
//                executor.execute(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        List<Post> filteredList = new ArrayList<>();
//
//                        /*---------- add all records to local db ----------*/
//
//                        for (Post post : list) {
//                            if (post.getAuthorID().equals(currentUserModel.getId()))
//                                filteredList.add(post);
//                            else {
//                                modelServer.getFriendsList(post.getAuthorID(), post.getCircle(), friendsList -> {
//                                    for (JsonElement friend : friendsList) {
//                                        if(friend.toString().equals(currentUserModel.getId())){
//                                            filteredList.add(post);
//                                        }
//                                    }
//                                });
//                            }
//                        }
//
//                        allPostsList.postValue(filteredList);
//
//                    }
//                });
//
//            }
//        });
//
//    }

    /* ---------------------------------------------------------------------------- */

    public interface getPostByIdListener {
        void onComplete(JsonObject post);
    }

    public void getPostById(String id, getPostByIdListener listener) {
        modelServer.getPostById(id, listener);
    }

    /* ---------------------------------------------------------------------------- */

    public interface deletePostListener {
        void onComplete();
    }

    public void deletePost(String id, deletePostListener listener) {
        modelServer.deletePost(id, listener);
    }

    /* ---------------------------------------------------------------------------- */

    public interface editPostListener {
        void onComplete();
    }

    public void editPost(HashMap<String, String> map, String id, editPostListener listener) {
        modelServer.editPost(map, id, listener);
    }

    /* ---------------------------------------------------------------------------- */

    public interface addCommentListener {
        void onComplete();
    }

    public void addComment(HashMap<String, String> map, addCommentListener listener) {
        Log.d("TAG", "2222");
        modelServer.addComment(map, listener);
    }

    /* ---------------------------------------------------------------------------- */
    public interface allCommentsListener {
        void onComplete(List<Comment> commentsList);
    }

    public void getAllComments(allCommentsListener listener) {
        modelServer.getAllComments(listener);
    }


    /* ---------------------------------------------------------------------------- */
    public interface getPostComments {
        void onComplete(List<Comment> commentsList);
    }

    public void getPostComments(String postId, allCommentsListener listener) {
        modelServer.getPostComments(postId, listener);
    }

    /* ---------------------------------------------------------------------------- */
    public interface editCommentListener {
        void onComplete();
    }

    public void editComment(HashMap<String, String> map, String id, editCommentListener listener) {
        modelServer.editComment(map, id, listener);
    }

    /* ---------------------------------------------------------------------------- */

    public interface deleteCommentListener {
        void onComplete();
    }

    public void deleteComment(String id, deleteCommentListener listener) {
        modelServer.deleteComment(id, listener);
    }

    /* ---------------------------------------------------------------------------- */

    public interface getMyPostsListener {
        void onComplete(List<Post> postsList);
    }

    public void getMyPosts(String id, getMyPostsListener listener) {
        modelServer.getMyPosts(id, listener);
    }

    /* ---------------------------------------------------------------------------- */

    public interface editUserListener {
        void onComplete();
    }

    public void editUser(HashMap<String, String> map, String id, editUserListener listener) {
        modelServer.editUser(map, id, listener);
    }


    /* ---------------------------------------------------------------------------- */

    public interface allUsersListener {
        void onComplete(List<User> usersList);
    }

    public void getAllUsers(allUsersListener listener) {
        modelServer.getAllUsers(listener);
    }

    /* ---------------------------------------------------------------------------- */

    public interface secondCircleListener {
        void onComplete(JsonArray friendsList);
    }

    public void getSecondCircle(String userID, secondCircleListener listener) {
        modelServer.getSecondCircle(userID, listener);
    }

    /* ---------------------------------------------------------------------------- */

    public interface thirdCircleListener {
        void onComplete(JsonArray friendsList);
    }

    public void getThirdCircle(String userID, thirdCircleListener listener) {
        modelServer.getThirdCircle(userID, listener);
    }

    /* ---------------------------------------------------------------------------- */

    public interface addFriendListener {
        void onComplete();
    }

    public void addFriendToMyContacts(String myID, String hisID, addFriendListener listener) {
        modelServer.addFriendToMyContacts(myID, hisID, listener);
    }

    public interface SaveImageListener {
        void onComplete(String url);
    }

    public void saveUserImage(Bitmap imageBitmap, String imageName, SaveImageListener listener) {

        modelServer.saveUserImage(imageBitmap, imageName, listener);
    }


}

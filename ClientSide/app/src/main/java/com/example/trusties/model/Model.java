package com.example.trusties.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
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
        void onComplete(JsonObject res);

    }

    public void addPost(HashMap<String, String> map, addPostListener listener) {
        modelServer.addPost(map, listener);
    }

    public interface addPhotosToPostListener {
        void onComplete();
    }

    public void addPhotosToPost(ArrayList<String> photos,String id, addPhotosToPostListener listener) {
        modelServer.addPhotosToPost(photos,id, listener);
    }

    /* ---------------------------------------------------------------------------- */
    public interface addSosListener {
        void onComplete();
    }

    public void addSos(HashMap<String, String> map, addSosListener listener) {
        modelServer.addSos(map, listener);
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
        void onComplete(JsonObject post);
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
    public interface upCommentListener {
        void onComplete();
    }

    public void upComment(String id,HashMap<String, String> map, upCommentListener listener) {
        modelServer.upComment(id,map, listener);
    }
    /* ---------------------------------------------------------------------------- */
    public interface downCommentListener {
        void onComplete();
    }

    public void downComment(String id,HashMap<String, String> map, downCommentListener listener) {
        modelServer.downComment(id,map, listener);
    }

    /* ---------------------------------------------------------------------------- */
//    public interface getCommentRate {
//        void onComplete(JsonObject comment);
//    }
//
//    public void getCommentRate(String id,getCommentRate listener) {
//        modelServer.getCommentRate(id,listener);
//    }



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

    /* ---------------------------------------------------------------------------- */

    public interface removeFriendListener {
        void onComplete();
    }

    public void removeFriendFromMyContacts(String myID, String hisID, removeFriendListener listener) {
        modelServer.removeFriendFromMyContacts(myID, hisID, listener);
    }



    /* ---------------------------------------------------------------------------- */

//    public interface getAllPostsInHomePageListener {
//        void onComplete(List<Post> postsList);
//    }
//
//    public void getAllPostsInHomePage(getAllPostsInHomePageListener listener) {
//        modelServer.getAllPostsInHomePage(listener);
//    }


    public interface encodeBitMapImgListener {
        void onComplete(String url);
    }

    public void encodeBitMapImg(Bitmap imageBitmap,  encodeBitMapImgListener listener) {

        modelServer.encodeBitMapImg(imageBitmap, listener);
    }


    /* ---------------------------------------------------------------------------- */

    public interface addNotificationListener {
        void onComplete();
    }

    public void addNotification(HashMap<String, String> map, addNotificationListener listener) {
        Log.d("TAG", "Model --> addNotification");
        modelServer.addNotification(map, listener);
    }

    public interface allNotificationsListener {
        void onComplete(List<Notification> notificationsList);
    }

    public void getAllNotifications(allNotificationsListener listener) {
        Log.d("TAG", "Model --> getAllNotifications");
        modelServer.getAllNotifications(listener);
    }

    public interface sendNotificationListener {
        void onComplete();
    }

    public void sendNotification(HashMap<String, String> map, sendNotificationListener listener) {
        Log.d("TAG", "Model --> send notifications");
        modelServer.sendNotification(map, listener);
    }
    /* ---------------------------------------------------------------------------- */

    public interface approveVolunteerListener {
        void onComplete();
    }

    public void approveVolunteer(String id,HashMap<String, String> map, approveVolunteerListener listener) {
        modelServer.approveVolunteer(id,map, listener);
    }

    public interface cancelVolunteerListener {
        void onComplete();
    }

    public void cancelVolunteer(String id,HashMap<String, String> map, cancelVolunteerListener listener) {
        modelServer.cancelVolunteer(id,map, listener);
    }

    public interface volunteerListener {
        void onComplete();
    }

    public void volunteer(String id,HashMap<String, String> map, volunteerListener listener) {
        modelServer.volunteer(id,map, listener);
    }

    public interface getSosVolunteersListener  {
        void onComplete(List<User> volunteers);
        //void onComplete(JsonArray volunteers);
    }

    public void getSosVolunteers(String id, getSosVolunteersListener listener) {
        modelServer.getSosVolunteers(id, listener);
    }


}

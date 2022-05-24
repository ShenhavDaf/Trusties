package com.example.trusties.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trusties.CommonFunctions;
import com.example.trusties.R;
import com.example.trusties.model.Model;
import com.example.trusties.model.Post;
import com.example.trusties.model.User;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;

    TextView choiceTV, choiceFriend, choicePost, postTopTV, categoriesBtn, statusBtn, authorBtn, descriptionBtn;
    Button searchBtn;
    ConstraintLayout categoriesLayout, statusLayout;
    EditText authorET, descriptionET, newFriendET;
    CheckBox carCB, deliveryCB, toolsCB, houseCB, openCB, waitingCB, closeCB;

    String flag;
    SwipeRefreshLayout swipeRefreshUsers;
    SwipeRefreshLayout swipeRefreshPosts;
    UserAdapter userAdapter;
    List<User> lst_users = new LinkedList<>();
    PostAdapter postAdapter;
    List<Post> lst_posts = new LinkedList<>();

    Bitmap decodedByte;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        /*--------------------------------------View--------------------------------------*/

        choiceTV = view.findViewById(R.id.search_textViewChoiceTOP);
        choiceFriend = view.findViewById(R.id.search_friend_choice_tv);
        choicePost = view.findViewById(R.id.search_post_choice_tv);

        postTopTV = view.findViewById(R.id.search_textViewPostsTOP);
        categoriesBtn = view.findViewById(R.id.search_categories_tv);
        categoriesLayout = view.findViewById(R.id.search_categories_layout);
        carCB = view.findViewById(R.id.search_car_cb);
        deliveryCB = view.findViewById(R.id.search_delivery_cb);
        toolsCB = view.findViewById(R.id.search_tools_cb);
        houseCB = view.findViewById(R.id.search_house_cb);


        statusBtn = view.findViewById(R.id.search_status_tv);
        statusLayout = view.findViewById(R.id.search_status_layout);
        openCB = view.findViewById(R.id.search_open_cb);
        waitingCB = view.findViewById(R.id.search_waiting_cb);
        closeCB = view.findViewById(R.id.search_close_cb);


        authorBtn = view.findViewById(R.id.search_author_tv);
        authorET = view.findViewById(R.id.search_author_et);


        descriptionBtn = view.findViewById(R.id.search_description_tv);
        descriptionET = view.findViewById(R.id.search_description_et);

        newFriendET = view.findViewById(R.id.search_friend_et);

        searchBtn = view.findViewById(R.id.search_btn);

        swipeRefreshUsers = view.findViewById(R.id.search_swiperefresh_users);
        swipeRefreshUsers.setOnRefreshListener(() -> friendModeFunction());

        swipeRefreshPosts = view.findViewById(R.id.search_swiperefresh_posts);
        swipeRefreshPosts.setOnRefreshListener(() -> postModeFunction());

        /*--------------------------------------RecyclerView--------------------------------------*/

        RecyclerView usersListRV = view.findViewById(R.id.search_users_list_rv);
        usersListRV.setHasFixedSize(true);
        usersListRV.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter();
        usersListRV.setAdapter(userAdapter);

        userAdapter.setOnItemClickListener((v, position) -> {
            Model.instance.findUserByEmail(searchViewModel.getUsersData().get(position).getEmail(), user -> {
                String userId = user.get("_id").toString().replace("\"", "");
                if (userId.equals(Model.instance.getCurrentUserModel().getId())) {
                    Navigation.findNavController(v).navigate(SearchFragmentDirections.actionGlobalNavigationDashboard());
                } else
                    Navigation.findNavController(v).navigate(SearchFragmentDirections.actionSearchFragmentToOthersProfileFragment(userId));
            });

        });
        /*--- --- --- --- --- --- --- --- --- ---- ---- --- --- --- --- --- --- --- --- --- --- */

        RecyclerView postsListRV = view.findViewById(R.id.search_posts_list_rv);
        postsListRV.setHasFixedSize(true);
        postsListRV.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter();
        postsListRV.setAdapter(postAdapter);

        postAdapter.setOnItemClickListener((v, position) -> {
            String postId = searchViewModel.getPostsData().get(position).getId();
            Navigation.findNavController(v).navigate(SearchFragmentDirections.actionSearchFragmentToDetailsPostFragment(postId));
        });


        /*----------------------------------------OnClickListeners--------------------------------------*/
        choiceFriend.setOnClickListener(v -> {
            flag = "friend";
            switchVisibility(newFriendET);
            switchVisibility(searchBtn);
            switchVisibility(choiceTV);
            switchVisibility(choiceFriend);
            switchVisibility(choicePost);
            swipeRefreshUsers.setVisibility(View.VISIBLE);
        });


        choicePost.setOnClickListener(v -> {
            flag = "post";
            switchVisibility(categoriesBtn);
            switchVisibility(statusBtn);
            switchVisibility(authorBtn);
            switchVisibility(descriptionBtn);
            switchVisibility(searchBtn);
            switchVisibility(postTopTV);
            switchVisibility(choiceTV);
            switchVisibility(choiceFriend);
            switchVisibility(choicePost);
            swipeRefreshPosts.setVisibility(View.VISIBLE);
        });

        categoriesBtn.setOnClickListener(v -> switchVisibility(categoriesLayout));
        statusBtn.setOnClickListener(v -> switchVisibility(statusLayout));
        authorBtn.setOnClickListener(v -> switchVisibility(authorET));
        descriptionBtn.setOnClickListener(v -> switchVisibility(descriptionET));

        searchBtn.setOnClickListener(v -> {
            if (flag.equals("friend")) {
                friendModeFunction();
            } else if (flag.equals("post")) {
                postModeFunction();
            }
        });

        return view;
        /*----------------------------------------End of onCreateView--------------------------------------*/
    }


    /* *************************************** Functions *************************************** */

    private void switchVisibility(View object) {
        if (object.getVisibility() == View.GONE)
            object.setVisibility(View.VISIBLE);
        else object.setVisibility(View.GONE);
    }

    /*--- --- --- --- --- --- --- --- --- ---- --- --- --- --- --- --- --- --- --- --- --- --- --- --- */

    private void refresh() {
        if (flag.equals("friend")) {
            searchViewModel.usersData = lst_users;
            userAdapter.notifyDataSetChanged();
            swipeRefreshUsers.setRefreshing(false);
        }

        if (flag.equals("post")) {
            searchViewModel.postsData = lst_posts;
            postAdapter.notifyDataSetChanged();
            swipeRefreshPosts.setRefreshing(false);
        }

    }

    /*--- --- --- --- --- --- --- --- --- ---- --- --- --- --- --- --- --- --- --- --- --- --- --- --- */

    private void friendModeFunction() {
        lst_users.clear();
        String friendName = newFriendET.getText().toString().toLowerCase().trim();
        if (!friendName.equals("")) {
            Model.instance.getAllUsers(usersList -> {
                for (User user : usersList) {
                    if (user.getFullName().toLowerCase().contains(friendName)) {
                        lst_users.add(user);
                        userAdapter.notifyDataSetChanged();
                    }
                }

                searchViewModel.usersData = lst_users;
            });
        } else {
            new CommonFunctions().myPopup(this.getContext(), "Search error", "No search member name received");
        }
        swipeRefreshUsers.setRefreshing(false);
        refresh();
    }

    /*--- --- --- --- --- --- --- --- --- ---- --- --- --- --- --- --- --- --- --- --- --- --- --- --- */

    private void postModeFunction() {
        lst_posts.clear();
        List<Post> allPosts = Model.instance.getAll().getValue();

        List<String> categoriesArray = new ArrayList<>();
        if (carCB.isChecked()) categoriesArray.add("car");
        if (houseCB.isChecked()) categoriesArray.add("house");
        if (toolsCB.isChecked()) categoriesArray.add("tools");
        if (deliveryCB.isChecked()) categoriesArray.add("delivery");

        List<String> statusArray = new ArrayList<>();
        if (openCB.isChecked()) statusArray.add("open");
        if (waitingCB.isChecked()) statusArray.add("waiting");
        if (closeCB.isChecked()) statusArray.add("close");

        List<Post> afterCategory = new ArrayList<>();
        List<Post> afterStatus = new ArrayList<>();
        List<Post> afterAuthor = new ArrayList<>();
        List<Post> afterDescription = new ArrayList<>();

    /* --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
       --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- */

        if (!categoriesArray.isEmpty()) {
            for (Post post : allPosts) {
                String postCategory = post.getCategory().toLowerCase();
                for (String category : categoriesArray) {
                    if (postCategory.equals(category)) {
                        afterCategory.add(post);
                    }
                }
            }
        } else afterCategory.addAll(allPosts);

    /* --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
       --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- */

        if (!statusArray.isEmpty()) {
            for (Post post : afterCategory) {
                String postStatus = post.getStatus().toLowerCase();
                for (String status : statusArray) {
                    if (postStatus.equals(status)) {
                        afterStatus.add(post);
                    }
                }
            }
        } else afterStatus.addAll(afterCategory);

    /* --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
       --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- */

        String authorText = authorET.getText().toString().toLowerCase().trim();
        if (!authorText.equals("")) {
            for (Post post : afterStatus) {
                if (post.getAuthorName().toLowerCase().contains(authorText)) {
                    afterAuthor.add(post);
                }
            }

        } else
            afterAuthor.addAll(afterStatus);

        String descriptionText = descriptionET.getText().toString().toLowerCase().trim();
        if (!descriptionText.equals("")) {
            for (Post post : afterAuthor) {
                if (post.getTitle().toLowerCase().contains(descriptionText) ||
                        post.getDescription().toLowerCase().contains(descriptionText)) {
                    afterDescription.add(post);
                }
            }
        } else afterDescription.addAll(afterAuthor);

    /* --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
       --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- */

        if (afterDescription.isEmpty())
            new CommonFunctions().myPopup(this.getContext(),
                    "Oops..",
                    "we did not find a match for your filter. Please try something else.");


        lst_posts.addAll(afterDescription);
        postAdapter.notifyDataSetChanged();
        searchViewModel.postsData = lst_posts;
        swipeRefreshPosts.setRefreshing(false);

        refresh();
    }





    /* *************************************** Holder *************************************** */

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, numberConnections;

        public UserViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            //TODO: user image
            userName = itemView.findViewById(R.id.connectionListRow_userName_tv);
            numberConnections = itemView.findViewById(R.id.connectionListRow_mutual);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                listener.onItemClick(v, pos);
            });
        }


        @SuppressLint("SimpleDateFormat")
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void bind(User user) {
            userName.setText(user.getFullName());
            Model.instance.findUserByEmail(user.getEmail(), new Model.findUserByEmailListener() {
                @Override
                public void onComplete(JsonObject freind) {
                    Model.instance.getFriendsList(freind.get("_id").toString().replace("\"", ""), 1, new Model.friendsListListener() {
                        @Override
                        public void onComplete(JsonArray friendsList) {
                            numberConnections.setText(friendsList.size() + " connections");
                        }
                    });
                }
            });
        }
    }


    /* *************************************** Adapter *************************************** */

    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

        OnItemClickListener listener;

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = getLayoutInflater().inflate(R.layout.connection_list_row, parent, false);
            UserViewHolder holder = new UserViewHolder(view, listener);
            return holder;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = searchViewModel.getUsersData().get(position);
            holder.bind(user);
        }

        @Override
        public int getItemCount() {
            if (searchViewModel.getUsersData() == null) {
                return 0;
            }
            return searchViewModel.getUsersData().size();
        }
    }

    /*
     *
     *
     * */


    class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userName, title, description, time, commentNumber, category, status, volunteer_txt, volunteer_count;
        ImageView photo, userImage, plusOne;

        //        TextView userName, title, description, time, commentNumber,
        Button volunteer, sos;

        public PostViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            userName = itemView.findViewById(R.id.listrow_username_tv);
            userImage = itemView.findViewById(R.id.listrow_avatar_imv);
            time = itemView.findViewById(R.id.listrow_date_tv);
            title = itemView.findViewById(R.id.listrow_post_title_tv);
            description = itemView.findViewById(R.id.listrow_post_description_tv);
            commentNumber = itemView.findViewById(R.id.listrow_comment_num_tv);
            category = itemView.findViewById(R.id.listrow_category_tv);
            status = itemView.findViewById(R.id.listrow_post_status_tv);
            photo = itemView.findViewById(R.id.listrow_post_img);
            plusOne = itemView.findViewById(R.id.listrow_plus_one_image);
            sos = itemView.findViewById(R.id.listrow_sos_btn);


            volunteer = itemView.findViewById(R.id.postListRow_volunteer);
            volunteer_txt = itemView.findViewById(R.id.post_listRow_volunteer_Tv);
            volunteer_count = itemView.findViewById(R.id.post_listRow_volunteerCount_Tv);


            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                listener.onItemClick(v, pos);
            });
            volunteer.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                Post post = searchViewModel.getPostsData().get(pos);
                String id = post.getId();

                HashMap<String, String> map = new HashMap<>();
                map.put("vol_id", Model.instance.getCurrentUserModel().getId());

                Model.instance.volunteer(id, map, () -> {
                    refresh();
                });
            });

        }


        @SuppressLint("SimpleDateFormat")
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void bind(Post post) {

            // ##TYPE :SOS
            if (post.getRole().equals("SOS")) {
                sos.setVisibility(View.VISIBLE);
                MaterialCardView card = (MaterialCardView) itemView;
                int volunteersSize = 0;
                if (!Model.instance.getCurrentUserModel().getId().equals(post.getAuthorID())) {
                    if (post.getStatus().equals("OPEN")) {
                        //if the status is close & the current user is NOT the post sender
                        volunteer.setVisibility(View.VISIBLE);
                    }
                }

                Model.instance.getSosVolunteers(post.getId(), list ->
                {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getEmail().equals(Model.instance.getCurrentUserModel().getEmail())) {
                            volunteer.setVisibility(View.GONE);
                            volunteer_txt.setVisibility(View.VISIBLE);
                        }
                    }
                    volunteer_count.setText(list.size() + "Volunteers");
                    volunteer_count.setVisibility(View.VISIBLE);
                });

            }
            // ##TYPE :SOS+QUES

            Model.instance.findUserById(post.getAuthorID(), user -> {
                userName.setText(user.get("name").getAsString());
                if (user.get("photo") != null) {
                    String photoBase64 = user.get("photo").getAsString();
                    byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    userImage.setImageBitmap(decodedByte);
                }
            });

            title.setText(post.getTitle());
            if (post.getDescription().length() > 150)
                description.setText(post.getDescription().substring(0, 150) + "...");
            else
                description.setText(post.getDescription());

            String newTime = post.getTime().substring(0, 16).replace("T", "  ").replace("-", "/");
            time.setText(newTime);

            Model.instance.getPostComments(post.getId(), commentsList -> {
                commentNumber.setText(commentsList.size() + " Comments ");
            });


            /* TODO: Update the "Post" model and use getters & setters instead of using getPostById
                Unnecessary server calls */
            Model.instance.getPostById(post.getId(), new Model.getPostByIdListener() {
                @Override
                public void onComplete(JsonObject post) {

                    status.setText(post.get("status").getAsString());
                    if (status.getText().equals("OPEN")) {
                        status.setBackgroundColor(status.getContext().getColor(R.color.green));
                    }
                    category.setText(post.get("category").getAsString());

                    if (post.get("photo").getAsJsonArray().size() > 0) {// CHANGED
                        if (post.get("photo").getAsJsonArray().size() == 2)
                            plusOne.setVisibility(View.VISIBLE);
                        String photoBase64 = post.get("photo").getAsJsonArray().get(0).getAsString();
                        if (photoBase64 != null) {
                            byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            photo.setImageBitmap(decodedByte);
                        }
                    } else {
                        photo.setVisibility(View.GONE);
                    }
                }
            });

            // implement the ViewFactory interface and implement
            // unimplemented method that returns an imageView


//            comment.setOnClickListener(v -> {
//                HashMap<String,String> map = new HashMap<>();
//                map.put("email", "shenhav.dafadi@gmail.com");
//                map.put("postID", post.getId());
//                map.put("time", "123");
//                map.put("message", "new comment");
//
//                Model.instance.addComment(map, () -> {
//                    System.out.println("----------------------- blablka");
//                });
//            });
        }
    }

    /* *************************************** Adapter *************************************** */

    class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

        OnItemClickListener listener;

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = getLayoutInflater().inflate(R.layout.post_list_row, parent, false);
            PostViewHolder holder = new PostViewHolder(view, listener);
            return holder;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            Post post = searchViewModel.getPostsData().get(position);
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            if (searchViewModel.getPostsData() == null) {
                return 0;
            }
            return searchViewModel.getPostsData().size();
        }

    }
}

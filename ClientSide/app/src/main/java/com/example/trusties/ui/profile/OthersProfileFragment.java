package com.example.trusties.ui.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.trusties.R;
import com.example.trusties.databinding.FragmentDashboardBinding;
import com.example.trusties.model.Model;
import com.example.trusties.model.Post;
import com.example.trusties.model.User;
import com.google.gson.JsonObject;

import java.util.HashMap;


public class OthersProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentDashboardBinding binding;
    TextView userName;
    MyAdapter adapter;
    TextView connections;
    SwipeRefreshLayout swipeRefresh;
    JsonObject profileUser;
    Button add;
    String userId;
    User currUser;
    Button unFriend, acceptFriend, waitingFriend;
    ImageView userImage;
    Bitmap decodedByte;
    RatingBar ratingBar;
    TextView disableView;
    View gif;
    View line;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        userId = OthersProfileFragmentArgs.fromBundle(getArguments()).getUserId();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_others_profile, container, false);

        /**********************************/

        userName = root.findViewById(R.id.Othersprofile_name);
        userImage = root.findViewById(R.id.Othersprofile_image);
        unFriend = root.findViewById(R.id.othersProfile_unfriend_btn);
        acceptFriend = root.findViewById(R.id.othersProfile_Accept_friend_btn);
        waitingFriend = root.findViewById(R.id.othersProfile_Waiting_friend_btn);
        ratingBar = root.findViewById(R.id.otherProfile_ratingBar);
        disableView = root.findViewById(R.id.other_profile_disable_txt);
        line = root.findViewById(R.id.Othersprofile_line);
        RecyclerView postsList = root.findViewById(R.id.profile_postlist_rv);
        gif = root.findViewById(R.id.gif);
        gif.setVisibility(View.GONE);


        currUser = Model.instance.getCurrentUserModel();

        Model.instance.findUserById(userId, new Model.findUserByIdListener() {
            @Override
            public void onComplete(JsonObject user) {
                userName.setText(user.get("name").toString().replace("\"", ""));
                profileUser = user;

                if (user.get("photo") != null) {
                    String photoBase64 = user.get("photo").getAsString();
                    byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    userImage.setImageBitmap(decodedByte);
                }
                String friends = user.get("friends") + "";
                if (!friends.contains(currUser.getId())) {
                    disableView.setVisibility(View.VISIBLE);
                    gif.setVisibility(View.VISIBLE);
                    line.setVisibility(View.GONE);
                    postsList.setVisibility(View.GONE);
                }
            }
        });

        Model.instance.getRating(userId, obj -> {
            String rating_Str = obj.get("rating").toString().replace("\"", "");
            ratingBar.setRating(Float.parseFloat(rating_Str));
        });

        connections = root.findViewById(R.id.Othersprofile_connections);
        Model.instance.getFriendsList(userId, 1, friendsList -> {
            connections.setText(friendsList.size() + " connections");
            for (int i = 0; i < friendsList.size(); i++) {
                if (friendsList.get(i).toString().replace("\"", "").equals(currUser.getId())) {
                    Log.d("TAG", friendsList.get(i).toString());
                    unFriend.setVisibility(View.VISIBLE);
                    unFriend.setClickable(true);
                    acceptFriend.setVisibility(View.GONE);
                    waitingFriend.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                    postsList.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    disableView.setVisibility(View.GONE);
                    gif.setVisibility(View.GONE);
                }
            }
        });

        //Waiting
        Model.instance.getWaitingList(userId, waitingList -> {
            System.out.println("## getWaitingList :: Waiting " + waitingList.size());
            for (int i = 0; i < waitingList.size(); i++) {
                if (waitingList.get(i).toString().replace("\"", "").equals(currUser.getId())) {

                    waitingFriend.setVisibility(View.VISIBLE);
                    acceptFriend.setVisibility(View.GONE);
                    unFriend.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                }
            }
        });

        //Accept
        Model.instance.getWaitingList(currUser.getId(), waitingList -> {
            System.out.println("## getWaitingList :: Accept " + waitingList.size());
            for (int i = 0; i < waitingList.size(); i++) {
                if (waitingList.get(i).toString().replace("\"", "").equals(userId)) {

                    acceptFriend.setVisibility(View.VISIBLE);
                    waitingFriend.setVisibility(View.GONE);
                    unFriend.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                }
            }
        });

        add = root.findViewById(R.id.Othersprofile_add_btn);
        add.setOnClickListener(v -> {
            Model.instance.addFriendToMyContacts(currUser.getId(), userId, () -> {
                //##change1
                //unFriend.setVisibility(View.VISIBLE);
                waitingFriend.setVisibility(View.VISIBLE);
                gif.setVisibility(View.VISIBLE);
                add.setVisibility(View.GONE);
                disableView.setVisibility(View.GONE);
                postsList.setVisibility(View.GONE);
                line.setVisibility(View.GONE);

                /* ------ Add Notification ------ */
                HashMap<String, String> notification = new HashMap<>();
                notification.put("sender", currUser.getId());
                notification.put("post", userId);
                notification.put("time", (new Long(0)).toString());
                notification.put("type", "friendRequest");
                notification.put("circle", "0");

                Model.instance.addNotification(notification, () -> {
                    System.out.println("## Back from server :: addNotification");
                });

            });
        });

        acceptFriend.setOnClickListener(v -> {
            Model.instance.approveFriend(currUser.getId(), userId, () -> {

                unFriend.setVisibility(View.VISIBLE);
                postsList.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
                disableView.setVisibility(View.GONE);
                gif.setVisibility(View.GONE);
                acceptFriend.setVisibility(View.GONE);
                waitingFriend.setVisibility(View.GONE);
                add.setVisibility(View.GONE);

                /* ------ Add Notification ------ */
                HashMap<String, String> notification = new HashMap<>();
                notification.put("sender", currUser.getId());
                notification.put("post", userId);
                notification.put("time", (new Long(0)).toString());
                notification.put("type", "approveFriendRequest");
                notification.put("circle", "0");

                Model.instance.addNotification(notification, () -> {
                    System.out.println("## Back from server :: addNotification");
                });

                refresh();
            });
        });
        unFriend.setOnClickListener(v -> {
            Model.instance.removeFriendFromMyContacts(currUser.getId(), userId, () -> {
                unFriend.setVisibility(View.GONE);
                add.setVisibility(View.VISIBLE);
                disableView.setVisibility(View.VISIBLE);
                gif.setVisibility(View.VISIBLE);
                line.setVisibility(View.GONE);
                postsList.setVisibility(View.GONE);
                refresh();
            });
        });

/**********************************/
        swipeRefresh = root.findViewById(R.id.Othersprofile_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> refresh());

        RecyclerView list = root.findViewById(R.id.profile_postlist_rv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener((v, position) -> {
            String postId = profileViewModel.getData().get(position).getId();
            Navigation.findNavController(v).navigate(OthersProfileFragmentDirections.actionOthersProfileFragmentToDetailsPostFragment(postId));
        });

        refresh();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void refresh() {
        Model.instance.getFriendsList(userId, 1, friendsList -> {
            connections.setText(friendsList.size() + " connections");
        });
        Model.instance.getMyPosts(userId, postsList -> {
            profileViewModel.data = postsList;
            adapter.notifyDataSetChanged();
        });

        swipeRefresh.setRefreshing(false);
    }

    /* *************************************** Holder *************************************** */

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName, title, description, time, commentNumber, category, status, volunteer_txt, volunteer_count;
        ImageView photo, userImage;
        Button volunteer, cancelVolunteer, sos;

        public MyViewHolder(@NonNull View itemView, OthersProfileFragment.OnItemClickListener listener) {
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
            sos = itemView.findViewById(R.id.listrow_sos_btn);


            volunteer = itemView.findViewById(R.id.postListRow_volunteer);
            volunteer_txt = itemView.findViewById(R.id.post_listRow_volunteer_Tv);
            volunteer_count = itemView.findViewById(R.id.post_listRow_volunteerCount_Tv);
            cancelVolunteer = itemView.findViewById(R.id.postListRow_cancel_volunteer);


            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                listener.onItemClick(v, pos);
            });

            volunteer.setOnClickListener(v -> {
                volunteer.setText("Volunteered");
                cancelVolunteer.setVisibility(View.GONE);

                int pos = getAdapterPosition();
                Post post = profileViewModel.getData().get(pos);
                String id = post.getId();

                HashMap<String, String> map = new HashMap<>();
                map.put("vol_id", Model.instance.getCurrentUserModel().getId());

                Model.instance.volunteer(id, map, () -> {
                    refresh();
                });
            });

            cancelVolunteer.setOnClickListener(v -> {
                cancelVolunteer.setVisibility(View.GONE);
                volunteer_txt.setVisibility(View.GONE);

                int pos = getAdapterPosition();
                Post post = profileViewModel.getData().get(pos);
                String id = post.getId();

                HashMap<String, String> map = new HashMap<>();
                map.put("vol_id", currUser.getId());

                Model.instance.cancelVolunteer(id, map, () -> refresh());
            });

        }


        @SuppressLint("SimpleDateFormat")
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void bind(Post post) {
            cancelVolunteer.setVisibility(View.GONE);

            if (post.getRole().equals("SOS")) {
                sos.setVisibility(View.VISIBLE);
                if (!Model.instance.getCurrentUserModel().getId().equals(post.getAuthorID())) {
                    if (post.getStatus().replace("\"", "").equals("OPEN")) {
                        volunteer.setVisibility(View.VISIBLE);
                    }
                }

                Model.instance.getSosVolunteers(post.getId(), list -> {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getEmail().equals(Model.instance.getCurrentUserModel().getEmail())) {
                            volunteer.setVisibility(View.GONE);
                            if (!post.getAuthorID().equals(currUser.getId()))
                                cancelVolunteer.setVisibility(View.VISIBLE);
                            volunteer_txt.setVisibility(View.VISIBLE);
                        }
                    }
                    volunteer_count.setText(list.size() + " Volunteers");
                    volunteer_count.setVisibility(View.VISIBLE);
                });
            }

            Model.instance.findUserById(userId, user -> {
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

            Model.instance.getPostById(post.getId(), new Model.getPostByIdListener() {
                @Override
                public void onComplete(JsonObject post) {

                    if (post.get("role").toString().replace("\"", "").equals("SOS")) {
                        status.setText(post.get("status").getAsString());
                        if (status.getText().equals("OPEN")) {
                            status.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_green));
                        } else if (status.getText().equals("WAITING")) {
                            status.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_orange));
                        } else if (status.getText().equals("CLOSE")) {
                            status.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_red));

                        }
                    } else
                        status.setVisibility(View.GONE);

                    String currCategory = post.get("category").getAsString();
                    Drawable categoryImg = null;

                    if (currCategory.equals("Tools")) {
                        categoryImg = getContext().getResources().getDrawable(R.drawable.tools);
                    } else if (currCategory.equals("Delivery")) {
                        categoryImg = getContext().getResources().getDrawable(R.drawable.delivery);
                    } else if (currCategory.equals("House")) {
                        categoryImg = getContext().getResources().getDrawable(R.drawable.house);
                    } else if (currCategory.equals("Car")) {
                        categoryImg = getContext().getResources().getDrawable(R.drawable.car);
                    }

                    category.setBackground(categoryImg);

                    if (post.get("photo").getAsJsonArray().size() > 0) {
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
        }

    }



    /* *************************************** Adapter *************************************** */

    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    class MyAdapter extends RecyclerView.Adapter<OthersProfileFragment.MyViewHolder> {

        OthersProfileFragment.OnItemClickListener listener;

        public void setOnItemClickListener(OthersProfileFragment.OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public OthersProfileFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = getLayoutInflater().inflate(R.layout.post_list_row, parent, false);
            OthersProfileFragment.MyViewHolder holder = new OthersProfileFragment.MyViewHolder(view, listener);
            return holder;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(@NonNull OthersProfileFragment.MyViewHolder holder, int position) {
            Post post = profileViewModel.getData().get(position);
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            if (profileViewModel.getData() == null) {
                return 0;
            }
            return profileViewModel.getData().size();
        }
    }
}
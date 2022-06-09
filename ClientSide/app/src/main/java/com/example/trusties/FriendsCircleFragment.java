package com.example.trusties;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trusties.model.Model;
import com.example.trusties.model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.LinkedList;
import java.util.List;


public class FriendsCircleFragment extends Fragment {

    RecyclerView usersListRV_circle_1, usersListRV_circle_2, usersListRV_circle_3;
    SwipeRefreshLayout swipeRefreshUsers_circle_1, swipeRefreshUsers_circle_2, swipeRefreshUsers_circle_3;
    UserAdapter userAdapter1, userAdapter2, userAdapter3;

    List<User> lst_users_1 = new LinkedList<>();
    List<User> lst_users_2 = new LinkedList<>();
    List<User> lst_users_3 = new LinkedList<>();

    String currUserID;
    Integer circle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_circle, container, false);
        circle = FriendsCircleFragmentArgs.fromBundle(getArguments()).getCircle();
        currUserID = Model.instance.getCurrentUserModel().getId();

        /* ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- */

        List<User> list1 = getFirstListFunction();

        usersListRV_circle_1 = view.findViewById(R.id.friendsCircle_first_RecyclerView);
        usersListRV_circle_1.setHasFixedSize(true);
        usersListRV_circle_1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        userAdapter1 = new UserAdapter(list1);
        usersListRV_circle_1.setAdapter(userAdapter1);

        swipeRefreshUsers_circle_1 = view.findViewById(R.id.friendsCircle_first_swipeRefresh);
//        swipeRefreshUsers_circle_1.setOnRefreshListener(() -> getFirstListFunction());

        userAdapter1.setOnItemClickListener((v, position) -> {
            Model.instance.findUserByEmail(list1.get(position).getEmail(), user -> {
                String userId = user.get("_id").toString().replace("\"", "");
                Navigation.findNavController(v).navigate(
                        FriendsCircleFragmentDirections.actionFriendsCircleFragmentToOthersProfileFragment(userId));
            });

        });

        /* ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- */

        List<User> list2 = getSecondListFunction();

        usersListRV_circle_2 = view.findViewById(R.id.friendsCircle_second_RecyclerView);
        usersListRV_circle_2.setHasFixedSize(true);
        usersListRV_circle_2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        userAdapter2 = new UserAdapter(list2);
        usersListRV_circle_2.setAdapter(userAdapter2);

        swipeRefreshUsers_circle_2 = view.findViewById(R.id.friendsCircle_second_swipeRefresh);
//        swipeRefreshUsers_circle_2.setOnRefreshListener(() -> getSecondListFunction());

        userAdapter2.setOnItemClickListener((v, position) -> {
            Model.instance.findUserByEmail(list2.get(position).getEmail(), user -> {
                String userId = user.get("_id").toString().replace("\"", "");
                Navigation.findNavController(v).navigate(
                        FriendsCircleFragmentDirections.actionFriendsCircleFragmentToOthersProfileFragment(userId));
            });

        });

        /* ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- */

        List<User> list3 = getThirdListFunction();

        usersListRV_circle_3 = view.findViewById(R.id.friendsCircle_third_RecyclerView);
        usersListRV_circle_3.setHasFixedSize(true);
        usersListRV_circle_3.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        userAdapter3 = new UserAdapter(list3);
        usersListRV_circle_3.setAdapter(userAdapter3);

        swipeRefreshUsers_circle_3 = view.findViewById(R.id.friendsCircle_third_swipeRefresh);
//        swipeRefreshUsers_circle_3.setOnRefreshListener(() -> getThirdListFunction());

        userAdapter3.setOnItemClickListener((v, position) -> {
            Model.instance.findUserByEmail(list3.get(position).getEmail(), user -> {
                String userId = user.get("_id").toString().replace("\"", "");
                Navigation.findNavController(v).navigate(
                        FriendsCircleFragmentDirections.actionFriendsCircleFragmentToOthersProfileFragment(userId));
            });

        });
        /* ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- */

        return view;
    }


    private void refresh() {

    }

    private List<User> getFirstListFunction() {
        lst_users_1.clear();

        Model.instance.findUserById(currUserID, user -> {
            JsonArray friendsList = user.get("friends").getAsJsonArray();

            for (JsonElement id : friendsList) {
                String friendID = id.toString().replace("\"", "");
                Model.instance.findUserById(friendID, friend -> {
                    if (!lst_users_1.contains(friend)) {
                        friend.addProperty("_id", friendID);
                        lst_users_1.add(User.create(friend));
                        userAdapter1.notifyDataSetChanged();
                    }
                });
            }
        });


        refresh();
        return lst_users_1;
    }

    private List<User> getSecondListFunction() {
        lst_users_2.clear();

        Model.instance.getSecondCircle(currUserID, friendsList -> {
            for (JsonElement id : friendsList) {
                String friendID = id.toString().replace("\"", "");
                Model.instance.findUserById(friendID, user -> {
                    if (!lst_users_2.contains(user)) {
                        user.addProperty("_id", friendID);
                        lst_users_2.add(User.create(user));
                        userAdapter2.notifyDataSetChanged();
                    }
                });
            }
        });

        refresh();
        return lst_users_2;
    }


    private List<User> getThirdListFunction() {

        lst_users_3.clear();

        Model.instance.getThirdCircle(currUserID, friendsList -> {
            for (JsonElement id : friendsList) {
                String friendID = id.toString().replace("\"", "");
                Model.instance.findUserById(friendID, user -> {
                    if (!lst_users_3.contains(user)) {
                        user.addProperty("_id", friendID);
                        lst_users_3.add(User.create(user));
                        userAdapter3.notifyDataSetChanged();
                    }
                });
            }
        });

        refresh();
        return lst_users_3;
    }


    /* *************************************** Interface *************************************** */

    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }


    /* *************************************** Holder *************************************** */

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView userImg;

        public UserViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            userName = itemView.findViewById(R.id.friendsCard_name_tv);
            userImg = itemView.findViewById(R.id.friendsCard_img);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                listener.onItemClick(v, pos);
            });
        }


        @SuppressLint("SimpleDateFormat")
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void bind(User user) {

            Model.instance.findUserByEmail(user.getEmail(), friend -> {
                String name = friend.get("name").toString().replace("\"", "");
                userName.setText(name);

                userImg.setImageResource(R.drawable.avatar);
                if (friend.get("photo") != null) {
                    String photoBase64 = friend.get("photo").getAsString();
                    byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    userImg.setImageBitmap(decodedByte);
                }

            });
        }
    }



    /* *************************************** Adapters *************************************** */

    class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

        OnItemClickListener listener;
        List<User> list;

        public UserAdapter(List<User> list) {
            this.list = list;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = getLayoutInflater().inflate(R.layout.friend_card, parent, false);
            UserViewHolder holder = new UserViewHolder(view, listener);
            return holder;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = list.get(position);
            holder.bind(user);
        }

        @Override
        public int getItemCount() {
            if (list == null) {
                return 0;
            }
            return list.size();
        }
    }
}

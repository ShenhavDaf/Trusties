package com.example.trusties;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trusties.model.Model;
import com.example.trusties.model.User;
import com.example.trusties.ui.home.SearchViewModel;
import com.google.gson.JsonElement;

import java.util.LinkedList;
import java.util.List;


public class FriendsCircleFragment extends Fragment {

    private SearchViewModel searchViewModel;

    // TODO: sliderRecycler
    RecyclerView usersListRV_circle_1, usersListRV_circle_2, usersListRV_circle_3;
    SwipeRefreshLayout swipeRefreshUsers_circle_1, swipeRefreshUsers_circle_2, swipeRefreshUsers_circle_3;
    UserAdapter userAdapter;
    List<User> lst_users = new LinkedList<>();
    String currUserID;
    Integer circle;
    int columnsNumber;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_circle, container, false);
        circle = FriendsCircleFragmentArgs.fromBundle(getArguments()).getCircle();
        currUserID = Model.instance.getCurrentUserModel().getId();


        userAdapter = new UserAdapter();

        userAdapter.setOnItemClickListener((v, position) -> {
            Model.instance.findUserByEmail(searchViewModel.getUsersData().get(position).getEmail(), user -> {
                String userId = user.get("_id").toString().replace("\"", "");
                Navigation.findNavController(v).navigate(
                        FriendsCircleFragmentDirections.actionFriendsCircleFragmentToOthersProfileFragment(userId));
            });

        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            columnsNumber = 5;
        else columnsNumber = 3;
        /* ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- */

        usersListRV_circle_1 = view.findViewById(R.id.friendsCircle_first_RecyclerView);
        usersListRV_circle_1.setHasFixedSize(true);
        usersListRV_circle_1.setLayoutManager(new GridLayoutManager(getContext(), columnsNumber));
        usersListRV_circle_1.setAdapter(userAdapter);

        swipeRefreshUsers_circle_1 = view.findViewById(R.id.friendsCircle_first_swipeRefresh);
        swipeRefreshUsers_circle_1.setOnRefreshListener(() -> updateFunction());

        /* ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- */

        usersListRV_circle_2 = view.findViewById(R.id.friendsCircle_second_RecyclerView);
        usersListRV_circle_2.setHasFixedSize(true);
        usersListRV_circle_2.setLayoutManager(new GridLayoutManager(getContext(), columnsNumber));
        usersListRV_circle_2.setAdapter(userAdapter);

        swipeRefreshUsers_circle_2 = view.findViewById(R.id.friendsCircle_second_swipeRefresh);
        swipeRefreshUsers_circle_2.setOnRefreshListener(() -> updateFunction());

        /* ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- */

        usersListRV_circle_3 = view.findViewById(R.id.friendsCircle_third_RecyclerView);
        usersListRV_circle_3.setHasFixedSize(true);
        usersListRV_circle_3.setLayoutManager(new GridLayoutManager(getContext(), columnsNumber));
        usersListRV_circle_3.setAdapter(userAdapter);

        swipeRefreshUsers_circle_3 = view.findViewById(R.id.friendsCircle_third_swipeRefresh);
        swipeRefreshUsers_circle_3.setOnRefreshListener(() -> updateFunction());

        /* ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- */

        updateFunction();
        return view;
    }

    private void refresh() {
        searchViewModel.usersData = lst_users;
        userAdapter.notifyDataSetChanged();
        swipeRefreshUsers_circle_1.setRefreshing(false);
        swipeRefreshUsers_circle_2.setRefreshing(false);
        swipeRefreshUsers_circle_3.setRefreshing(false);
    }

    private void updateFunction() {
        lst_users.clear();

        // Hardcoded test
        /* ******************************* */
        User newUser = new User("62a05455abf6f43ad5c28ae5", "Shenhav", "shenhav.dafadi@gmail.com", "0520000000");
        lst_users.add(newUser);
        newUser = new User("62a0555eabf6f43ad5c28b11", "Ortal", "ortallik@gmail.com", "0520000000");
        lst_users.add(newUser);
        newUser = new User("62a11a9eabf6f43ad5c29733", "Adi", "adi@gmail.com", "0520000000");
        lst_users.add(newUser);
        newUser = new User("62a11b07abf6f43ad5c29758", "Hen", "hen@gmail.com", "0520000000");
        lst_users.add(newUser);

        userAdapter.notifyDataSetChanged();
        /* ******************************* */


        if (circle == 1) {

        } else if (circle == 2) {

            Model.instance.getFriendsCircleAsObjects(currUserID, 2, friendsList -> {
                for (JsonElement user : friendsList) {
                    Log.d("TAG", "------- " + user);
//                    lst_users.add(user);
//                    userAdapter.notifyDataSetChanged();
                }
//                searchViewModel.usersData = lst_users;
            });


        } else if (circle == 3) {

        }

        searchViewModel.usersData = lst_users;
        refresh();
    }


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

            View view = getLayoutInflater().inflate(R.layout.friend_card, parent, false);
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
}

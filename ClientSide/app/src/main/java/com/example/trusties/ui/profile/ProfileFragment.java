package com.example.trusties.ui.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.trusties.MyApplication;
import com.example.trusties.R;
import com.example.trusties.databinding.FragmentDashboardBinding;
import com.example.trusties.login.LoginActivity;
import com.example.trusties.model.Model;
import com.example.trusties.model.Post;
import com.example.trusties.model.User;
import com.example.trusties.ui.home.HomeFragmentDirections;
import com.google.gson.JsonObject;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentDashboardBinding binding;
    TextView userName;
    MyAdapter adapter;
    TextView connections;
    SwipeRefreshLayout swipeRefresh;
    User currUser;
    Button edit, logout;
    ImageView userImage;
    Bitmap decodedByte;
    RatingBar ratingBar;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        currUser = Model.instance.getCurrentUserModel();

        userName = root.findViewById(R.id.profile_name);
        userImage = root.findViewById(R.id.profile_image);
        ratingBar = root.findViewById(R.id.ratingBar_dashBoard);
        connections = root.findViewById(R.id.profile_connections);
        edit = root.findViewById(R.id.profile_edit_btn);
        logout = root.findViewById(R.id.profile_logout_btn);


        Model.instance.findUserById(currUser.getId(), user -> {
            userName.setText(user.get("name").toString().replace("\"", ""));

            if (user.get("photo") != null) {
                String photoBase64 = user.get("photo").getAsString();
                byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                userImage.setImageBitmap(decodedByte);
            }
        });

        swipeRefresh = root.findViewById(R.id.profile_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> refresh());

        RecyclerView list = root.findViewById(R.id.profile_postlist_rv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener((v, position) -> {
            String postId = profileViewModel.getData().get(position).getId();
            Navigation.findNavController(v).navigate(ProfileFragmentDirections.actionNavigationDashboardToDetailsPostFragment(postId));
        });

        Model.instance.getRating(currUser.getId(), obj -> {
            String rating_Str = obj.get("rating").toString().replace("\"", "");
            ratingBar.setRating(Float.parseFloat(rating_Str));
        });

        Model.instance.getFriendsList(currUser.getId(), 1, friendsList -> connections.setText(friendsList.size() + " connections"));

        connections.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(ProfileFragmentDirections.actionNavigationDashboardToConnectionsFragment()));

        edit.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(ProfileFragmentDirections.actionNavigationDashboardToEditProfileFragment(currUser.getId())));


        logout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Logout").setMessage("You sure, that you want to logout?");
            builder.setPositiveButton("Yes", (dialog, id) ->
                    Model.instance.signOut(currUser.getId(), () -> {
                        Intent i = new Intent(MyApplication.getContext(),
                                LoginActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }));
            builder.setNegativeButton("No", (dialog, id) -> dialog.cancel());
            AlertDialog alert11 = builder.create();
            alert11.show();
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
        Model.instance.getMyPosts(currUser.getId(), postsList -> {
            profileViewModel.data = postsList;
            adapter.notifyDataSetChanged();
        });

        swipeRefresh.setRefreshing(false);
    }

    /* *************************************** Holder *************************************** */

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName, title, description, time, commentNumber, category, status;
        ImageView photo, userImage;
        Button sos, friendsBtn;


        public MyViewHolder(@NonNull View itemView, ProfileFragment.OnItemClickListener listener) {
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
            friendsBtn = itemView.findViewById(R.id.listrow_friends_btn);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                listener.onItemClick(v, pos);
            });
        }


        @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void bind(Post post) {

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

            if (post.getRole().equals("sos")) {
                sos.setVisibility(View.VISIBLE);
            }

            friendsBtn.setVisibility(View.VISIBLE);
            friendsBtn.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate
                            (HomeFragmentDirections.actionGlobalFriendsCircleFragment().setCircle(post.getCircle()))
            );

            Model.instance.getPostComments(post.getId(), commentsList -> {
                commentNumber.setText(commentsList.size() + " Comments ");
            });


            Model.instance.getPostById(post.getId(), new Model.getPostByIdListener() {
                @Override
                public void onComplete(JsonObject post) {

                    status.setText(post.get("status").getAsString());
                    if (status.getText().equals("OPEN")) {
                        status.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_green));
                    } else if (status.getText().equals("WAITING")) {
                        status.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_orange));
                    } else if (status.getText().equals("CLOSE")) {
                        status.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_red));

                    }

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

    class MyAdapter extends RecyclerView.Adapter<ProfileFragment.MyViewHolder> {

        ProfileFragment.OnItemClickListener listener;

        public void setOnItemClickListener(ProfileFragment.OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public ProfileFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = getLayoutInflater().inflate(R.layout.post_list_row, parent, false);
            ProfileFragment.MyViewHolder holder = new ProfileFragment.MyViewHolder(view, listener);
            return holder;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(@NonNull ProfileFragment.MyViewHolder holder, int position) {
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
package com.example.trusties.ui.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.trusties.R;
import com.example.trusties.databinding.FragmentDashboardBinding;
import com.example.trusties.model.Model;
import com.example.trusties.model.Post;
import com.example.trusties.model.User;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonObject;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentDashboardBinding binding;
    TextView userName;
    MyAdapter adapter;
    TextView connections;
    SwipeRefreshLayout swipeRefresh;
    User currUser;
    Button edit;
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

        /**********************************/

        userName = root.findViewById(R.id.profile_name);
        userImage = root.findViewById(R.id.profile_image);
        ratingBar = root.findViewById(R.id.ratingBar_dashBoard);

        currUser = Model.instance.getCurrentUserModel();
        Model.instance.findUserById(Model.instance.getCurrentUserModel().getId(), new Model.findUserByIdListener() {
            @Override
            public void onComplete(JsonObject user) {
                userName.setText(user.get("name").toString().replace("\"", ""));

                if (user.get("photo") != null) {
                    String photoBase64 = user.get("photo").getAsString();
                    byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    userImage.setImageBitmap(decodedByte);
                }

            }
        });

        //## Set rating bar
        Model.instance.getRating(Model.instance.getCurrentUserModel().getId(), new Model.getRatingListener() {
            @Override
            public void onComplete(JsonObject obj) {
                String rating_Str=obj.get("rating").toString().replace("\"", "");
                Float rating=Float.valueOf(rating_Str);
                ratingBar.setRating(rating);

            }
        });

        connections = root.findViewById(R.id.profile_connections);
        Model.instance.getFriendsList(currUser.getId(), 1, friendsList -> {
            connections.setText(friendsList.size() + " connections");
        });
        connections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(ProfileFragmentDirections.actionNavigationDashboardToConnectionsFragment());
            }
        });
        edit = root.findViewById(R.id.profile_edit_btn);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(ProfileFragmentDirections.actionNavigationDashboardToEditProfileFragment(currUser.getId()));
            }
        });

        /**********************************/
        swipeRefresh = root.findViewById(R.id.profile_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> refresh());

        RecyclerView list = root.findViewById(R.id.Othersprofile_postlist_rv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyAdapter();
        list.setAdapter(adapter);


        adapter.setOnItemClickListener((v, position) -> {
            System.out.println("the POSITION is:  " + position);

            String postId = profileViewModel.getData().get(position).getId();
            System.out.println("the postID is:  " + postId);
            Navigation.findNavController(v).navigate(ProfileFragmentDirections.actionNavigationDashboardToDetailsPostFragment(postId));
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

//        Model.instance.getMyPosts(currUser.get,postsList -> {
//            dashboardViewModel.data = postsList;
//            adapter.notifyDataSetChanged();
//        });

        swipeRefresh.setRefreshing(false);
    }

    /* *************************************** Holder *************************************** */

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName, title, description, time, commentNumber, category, status;
        ImageView photo, userImage;


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

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                listener.onItemClick(v, pos);
            });
        }


        @SuppressLint("SimpleDateFormat")
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void bind(Post post) {

            if (post.getRole().toLowerCase().equals("sos")) {
                //TODO: if role == sos change to "sos layout"
//                getLayoutInflater().inflate(R.layout.sos_list_row, (ViewGroup) itemView,true); // double
                MaterialCardView card = (MaterialCardView) itemView;
                card.setCardBackgroundColor(card.getContext().getColor(R.color.sosCardBackground));
            }
            //TODO: change userName from post title to author name
            Model.instance.findUserById(post.getAuthorID(), user -> {
                        userName.setText(user.get("name").getAsString());

                        if (user.get("photo") != null) {
                            String photoBase64 = user.get("photo").getAsString();
                            byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            userImage.setImageBitmap(decodedByte);
                        }
                    }
            );
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

                    status.setText(post.get("status").getAsString());
                    if (status.getText().equals("OPEN")) {
                        status.setBackgroundColor(status.getContext().getColor(R.color.green));
                    }
                    category.setText(post.get("category").getAsString());

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
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
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonElement;
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
    Button unFriend;
    ImageView userImage;
    Bitmap decodedByte;
    RatingBar ratingBar;
    TextView disableView;



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
        ratingBar=root.findViewById(R.id.otherProfile_ratingBar);
        disableView = root.findViewById(R.id.other_profile_disable_txt);
        RecyclerView postsList = root.findViewById(R.id.profile_postlist_rv);


        currUser = Model.instance.getCurrentUserModel();

        Model.instance.findUserById(userId, new Model.findUserByIdListener() {
            @Override
            public void onComplete(JsonObject user) {
                userName.setText(user.get("name").toString().replace("\"", ""));
                profileUser = user;

                if (user.get("photo") != null) {

                    Log.d("TAG","photototot###");
                    String photoBase64 = user.get("photo").getAsString();
                    byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    userImage.setImageBitmap(decodedByte);
                }
                Log.d("TAG",user.get("friends") + "");
                String friends = user.get("friends") + "";
                if( !friends.contains(currUser.getId())){
                    disableView.setVisibility(View.VISIBLE);
                    postsList.setVisibility(View.GONE);
                }
            }
        });


        //## Set rating bar
        Model.instance.getRating(userId, new Model.getRatingListener() {
            @Override
            public void onComplete(JsonObject obj) {
                String rating_Str=obj.get("rating").toString().replace("\"", "");
                Float rating=Float.valueOf(rating_Str);
                ratingBar.setRating(rating);
            }
        });

        connections = root.findViewById(R.id.Othersprofile_connections);
        Model.instance.getFriendsList(userId, 1, friendsList -> {
            connections.setText(friendsList.size() + " connections");
            for (int i = 0; i < friendsList.size(); i++) {
                if (friendsList.get(i).toString().replace("\"", "").equals(currUser.getId())) {
                    Log.d("TAG", friendsList.get(i).toString());
                    unFriend.setVisibility(View.VISIBLE);
                    unFriend.setClickable(true);
                    add.setVisibility(View.GONE);
                }
            }
        });


        add = root.findViewById(R.id.Othersprofile_add_btn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.instance.addFriendToMyContacts(currUser.getId(), userId, new Model.addFriendListener() {
                    @Override
                    public void onComplete() {
                        unFriend.setVisibility(View.VISIBLE);
                        add.setVisibility(View.GONE);
                        disableView.setVisibility(View.GONE);
                        postsList.setVisibility(View.VISIBLE);
                        refresh();
                    }
                });
            }
        });

        unFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.instance.removeFriendFromMyContacts(currUser.getId(), userId, new Model.removeFriendListener() {
                    @Override
                    public void onComplete() {
                        unFriend.setVisibility(View.GONE);
                        add.setVisibility(View.VISIBLE);
                        disableView.setVisibility(View.VISIBLE);
                        postsList.setVisibility(View.GONE);
                        refresh();
                    }
                });
            }
        });

/**********************************/
                swipeRefresh=root.findViewById(R.id.Othersprofile_swiperefresh);
                        swipeRefresh.setOnRefreshListener(()->

                        refresh());

                        RecyclerView list=root.findViewById(R.id.profile_postlist_rv);
                        list.setHasFixedSize(true);
                        list.setLayoutManager(new

                        LinearLayoutManager(getContext()));
                        adapter=new

                        MyAdapter();
                        list.setAdapter(adapter);


                        adapter.setOnItemClickListener((v,position)->

                        {
                        System.out.println("the POSITION is:  "+position);

                        String postId=profileViewModel.getData().get(position).getId();
                        System.out.println("the postID is:  "+postId);
                        Navigation.findNavController(v).navigate(OthersProfileFragmentDirections.actionOthersProfileFragmentToDetailsPostFragment(postId));
                        });

                        refresh();
                        return root;
                        }

@Override
public void onDestroyView(){
        super.onDestroyView();
        binding=null;
        }

private void refresh(){
        Model.instance.getFriendsList(userId,1,friendsList->{
        connections.setText(friendsList.size()+" connections");
        });
        Model.instance.getMyPosts(userId,postsList->{
        profileViewModel.data=postsList;
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
    TextView userName, title, description, time, commentNumber, category, status, volunteer_txt, volunteer_count;
    ImageView photo, userImage;
    Button volunteer, sos;

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


        itemView.setOnClickListener(v -> {
            int pos = getAdapterPosition();
            listener.onItemClick(v, pos);
        });

        volunteer.setOnClickListener(v -> {
            volunteer.setText("Volunteered");
            int pos = getAdapterPosition();
            Post post = profileViewModel.getData().get(pos);
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

        if (post.getRole().toLowerCase().equals("sos")) {
            sos.setVisibility(View.VISIBLE);
            //TODO: if role == sos change to "sos layout"
//                getLayoutInflater().inflate(R.layout.sos_list_row, (ViewGroup) itemView,true); // double
            MaterialCardView card = (MaterialCardView) itemView;
//            card.setCardBackgroundColor(card.getContext().getColor(R.color.sosCardBackground));
            if (!Model.instance.getCurrentUserModel().getId().equals(post.getAuthorID())) {
                if (post.getStatus().toString().replace("\"","").equals("OPEN")) {
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
                volunteer_count.setText(list.size() + " Volunteers");
                volunteer_count.setVisibility(View.VISIBLE);
            });
        }
        //TODO: change userName from post title to author name
        Model.instance.findUserById(userId, user -> {
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
                    status.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_green));
                } else if(status.getText().equals("WAITING")) {
                    status.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_orange));
                } else if(status.getText().equals("CLOSE")) {
                    status.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_red));

                }

                String currCategory = post.get("category").getAsString();
                Drawable categoryImg = null;

                if(currCategory.equals("Tools")) {
                    categoryImg = getContext().getResources().getDrawable(R.drawable.tools);
                } else if(currCategory.equals("Delivery")) {
                    categoryImg = getContext().getResources().getDrawable(R.drawable.delivery);
                } else if(currCategory.equals("House")) {
                    categoryImg = getContext().getResources().getDrawable(R.drawable.house);
                } else if(currCategory.equals("Car")) {
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
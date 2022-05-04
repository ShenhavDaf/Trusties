package com.example.trusties.ui.profile;

import android.annotation.SuppressLint;
import android.content.Context;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.trusties.R;
import com.example.trusties.databinding.FragmentDashboardBinding;
import com.example.trusties.model.Model;
import com.example.trusties.model.Post;
import com.example.trusties.model.User;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonObject;


public class OthersProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentDashboardBinding binding;
    TextView userName;
    MyAdapter adapter;
    TextView connections;
    SwipeRefreshLayout swipeRefresh;
    JsonObject currUser;
    Button edit;
    String userId;


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
        Model.instance.findUserById(userId, new Model.findUserByIdListener() {
            @Override
            public void onComplete(JsonObject user) {
                userName.setText(user.get("name").toString().replace("\"", ""));
                currUser = user;
            }
        });
//        currUser = Model.instance.getCurrentUserModel();
//        Model.instance.findUserById(Model.instance.getCurrentUserModel().getId(), new Model.findUserByIdListener() {
//            @Override
//            public void onComplete(JsonObject user) {
//                userName.setText(user.get("name").toString().replace("\"", ""));
//            }
//        });

        connections = root.findViewById(R.id.Othersprofile_connections);
        Model.instance.getFriendsList(userId, 1, friendsList -> {
            connections.setText( friendsList.size() + " connections");
        });

        edit = root.findViewById(R.id.Othersprofile_add_btn);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Navigation.findNavController(v).navigate(ProfileFragmentDirections.actionNavigationDashboardToEditProfileFragment(currUser.getId()));
            }
        });

        /**********************************/
        swipeRefresh = root.findViewById(R.id.Othersprofile_swiperefresh);
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
        Model.instance.getMyPosts(userId,postsList -> {
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
        TextView userName, description, time, commentNumber;

        public MyViewHolder(@NonNull View itemView, OthersProfileFragment.OnItemClickListener listener) {
            super(itemView);

            userName = itemView.findViewById(R.id.listrow_username_tv);
            time = itemView.findViewById(R.id.listrow_date_tv);
            description = itemView.findViewById(R.id.listrow_post_text_tv);
            commentNumber = itemView.findViewById(R.id.listrow_comment_num_tv);

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
            userName.setText(post.getTitle());
            description.setText(post.getDescription());
            String newTime = post.getTime().substring(0, 16).replace("T", "  ").replace("-", "/");
            time.setText(newTime);

            Model.instance.getPostComments(post.getId(), commentsList -> {
                commentNumber.setText(commentsList.size() + " Comments ");
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
package com.example.trusties.posts;

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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.trusties.R;
import com.example.trusties.databinding.FragmentDetailsPostBinding;
import com.example.trusties.databinding.FragmentHomeBinding;
import com.example.trusties.model.Comment;
import com.example.trusties.model.Model;
import com.example.trusties.model.Post;
import com.example.trusties.model.User;
import com.example.trusties.ui.home.HomeFragment;
import com.example.trusties.ui.home.HomeFragmentDirections;
import com.example.trusties.ui.home.HomeViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonObject;

import java.util.HashMap;


public class DetailsPostFragment extends Fragment {

    TextView titleEt;
    TextView timeEt;
    TextView authorEt;
    TextView descriptionEt;
    TextView statusEt;
    TextView roleEt;
    EditText comment;
    // TODO: Add location (SOS Call)
    Button editBtn;
    Button deleteBtn;
    String postId;
    ProgressBar progressBar;
    ImageView postImg;
    ImageView imgUser;
    ImageView sendCommentBtn;
    View line;

    private DetailsPostViewModel postViewModel;
    private FragmentDetailsPostBinding binding;

    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        postId = DetailsPostFragmentArgs.fromBundle(getArguments()).getPostId();
        postViewModel = new ViewModelProvider(this, new MyViewModelFactory(postId)).get(DetailsPostViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_details_post, container, false);
        binding = FragmentDetailsPostBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        progressBar = view.findViewById(R.id.postdetails_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        titleEt = view.findViewById(R.id.postdetails_title_tv);
        timeEt = view.findViewById(R.id.postdetails_time_tv);
        authorEt = view.findViewById(R.id.postdetails_author_tv);
        descriptionEt=view.findViewById(R.id.postdetails_description_tv);
        roleEt = view.findViewById(R.id.postdetails_role_tv);
        statusEt = view.findViewById(R.id.postdetails_status_tv);
        editBtn = view.findViewById(R.id.postdetails_edit_btn);
        deleteBtn = view.findViewById(R.id.postdetails_delete_btn);
        postImg = view.findViewById(R.id.postDetails_post_img);
        postImg.setVisibility(View.GONE);
        line = view.findViewById(R.id.postdetails_line);
        comment = view.findViewById(R.id.postdetails_comment_et);
        sendCommentBtn = view.findViewById(R.id.postdetails_sendComment_btn);
        imgUser = view.findViewById(R.id.postdetails_imgUser_img);

        updateUI(View.INVISIBLE);

        Model.instance.getPostById(postId, new Model.getPostByIdListener() {
            @Override
            public void onComplete(JsonObject post) {

                String title = post.get("title").toString().replace("\"", "");
                String description = post.get("description").toString().replace("\"", "");
//                String time = post.get("time").toString().replace("\"", "");
                String time = post.get("time").getAsString().substring(0, 16).replace("T", "  ").replace("-", "/");
                String senderId = post.get("sender").toString().replace("\"", "");
                String status = post.get("status").toString().replace("\"", "");
                String role = post.get("role").toString().replace("\"", "");
                displayPost(title, description, time,senderId, status, role);
                progressBar.setVisibility(View.GONE);
            }
        });

        deleteBtn.setOnClickListener(v -> Model.instance.deletePost(postId, () -> {
//                        Model.instance.refresh;//TODO: ADD REFRESH
            Log.d("TAG", "delete");
            Navigation.findNavController(v).navigateUp();
        }));

        editBtn.setOnClickListener(v -> Navigation.findNavController(v).navigate(DetailsPostFragmentDirections.actionDetailsPostFragmentToEditPostFragment(postId)));

        sendCommentBtn.setOnClickListener(v -> {
            String content = comment.getText().toString();
            User user = Model.instance.getCurrentUserModel();

            HashMap<String, String> map = new HashMap<>();
            map.put("postId", postId);
            map.put("sender", user.getEmail());
            map.put("content", content);
            map.put("currentTime", (new Long(0)).toString());

            Model.instance.addComment(map, () -> {
                // TODO: Add comment to local DB ??
                // TODO: Refresh recycle view
            });
        });

        swipeRefresh = view.findViewById(R.id.comment_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> refresh());

        RecyclerView list = view.findViewById(R.id.postdetails_rv_comment);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DetailsPostFragment.MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener((v, position) -> {
            String postId = postViewModel.getData().get(position).getPostId();
            System.out.println("the postID is:  " + postId);
        });


        refresh();
        return view;
    }

    private void refresh() {
        Model.instance.getPostComments(postId,commentsList -> {
            System.out.println("Comments" + commentsList.size());
            postViewModel.data = commentsList;
            adapter.notifyDataSetChanged();

        });
        swipeRefresh.setRefreshing(false);
    }


    public void displayPost(String title, String description, String time,String senderId, String status, String role)
    {
        Model.instance.findUserById(senderId, user -> {
            titleEt.setText(title);
            descriptionEt.setText(description);
            timeEt.setText(time);
            authorEt.setText(user.get("name").toString().replace("\"", "")); //TODO: find user by ID
            statusEt.setText(status);
            roleEt.setText(role);
            //        if(!post.getPhoto().contentEquals("")) {
            //            Picasso.get()
            //                    .load(post.getPhoto())
            //                    .into(postImg);
            //        }

            updateUI(View.VISIBLE);

            if(role == "SOS") {
                // TODO: Display specific details of SOS call
            }
        });

    }

    public void updateUI(int type) {
        titleEt.setVisibility(type);
        timeEt.setVisibility(type);
        authorEt.setVisibility(type);
        descriptionEt.setVisibility(type);
        roleEt.setVisibility(type);
        statusEt.setVisibility(type);
        line.setVisibility(type);
        postImg.setVisibility(type);
        sendCommentBtn.setVisibility(type);
        imgUser.setVisibility(type);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username,content, time;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            username = itemView.findViewById(R.id.coomentListRow_userName_tv);
            time = itemView.findViewById(R.id.coomentListRow_time_tv);
            content = itemView.findViewById(R.id.coomentListRow_content_tv);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                listener.onItemClick(v, pos);
            });
        }


        @SuppressLint("SimpleDateFormat")
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void bind(Comment comment) {
            System.out.println("Comment bind");
            System.out.println(comment);

            Model.instance.findUserById(comment.getSender(), new Model.findUserByIdListener() {

                //Get Usr By Id.
                @Override
                public void onComplete(JsonObject user) {
                    Model.instance.setCurrentUserModel(new User(user.get("name").toString().replace("\"", ""), user.get("email").toString().replace("\"", ""), user.get("phone").toString().replace("\"", "")));

                    username.setText(user.get("name").toString().replace("\"", ""));

                }
            });

            content.setText(comment.getContent());
            String newTime = comment.getCurrentTime().substring(0, 16).replace("T", "  ").replace("-", "/");
            time.setText(newTime);

        }
    }

    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

      OnItemClickListener listener;

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = getLayoutInflater().inflate(R.layout.comment_list_row, parent, false);
            DetailsPostFragment.MyViewHolder holder = new DetailsPostFragment.MyViewHolder(view, listener);
            return holder;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Comment comment = postViewModel.getData().get(position);
            System.out.println("Comment onBindViewHolder");
            System.out.println(comment);
            holder.bind(comment);
        }

        @Override
        public int getItemCount() {
            if (postViewModel.getData() == null) {
                return 0;
            }
            return postViewModel.getData().size();
        }
    }


}
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
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.trusties.R;
import com.example.trusties.databinding.FragmentDetailsPostBinding;
import com.example.trusties.model.Comment;
import com.example.trusties.model.Model;
import com.example.trusties.model.User;
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
    Button requestsBtn;
    Button closeBtn;
    String postId;

    ProgressBar progressBar;
    ImageView postImg;
    ImageView imgUser;
    ImageView sendCommentBtn;

    View line;
    String senderId;
    User currUser;

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
        binding = FragmentDetailsPostBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        progressBar = view.findViewById(R.id.postdetails_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        titleEt = view.findViewById(R.id.postdetails_title_tv);
        timeEt = view.findViewById(R.id.postdetails_time_tv);
        authorEt = view.findViewById(R.id.postdetails_author_tv);
        descriptionEt = view.findViewById(R.id.postdetails_description_tv);
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
        requestsBtn= view.findViewById(R.id.postdetails_view_requests_btn);
        closeBtn= view.findViewById(R.id.postdetails_close_btn);

        updateUI(View.INVISIBLE);
        Model.instance.getPostById(postId, new Model.getPostByIdListener() {
            @Override
            public void onComplete(JsonObject post) {

                String title = post.get("title").toString().replace("\"", "");
                String description = post.get("description").toString().replace("\"", "");
//                String time = post.get("time").toString().replace("\"", "");
                String time = post.get("time").getAsString().substring(0, 16).replace("T", "  ").replace("-", "/");
                senderId = post.get("sender").toString().replace("\"", "");
                String status = post.get("status").toString().replace("\"", "");
                String role = post.get("role").toString().replace("\"", "");
                displayPost(title, description, time, senderId, status, role);
                progressBar.setVisibility(View.GONE);

                //Checking if the Current user is the sender of the post for enabling the - EditBtn and DeleteBtn-
                Model.instance.findUserById(post.get("sender").toString().replace("\"", ""), new Model.findUserByIdListener() {
                    @Override
                    public void onComplete(JsonObject user) {
                        if (user.get("email").toString().replace("\"", "").compareTo(Model.instance.getCurrentUserModel().getEmail()) == 0) {
                            deleteBtn.setEnabled(true);
                            deleteBtn.setEnabled(true);
                            if(role.compareTo("SOS")==0){
                                closeBtn.setVisibility(View.VISIBLE);
                                requestsBtn.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            deleteBtn.setEnabled(false);
                            editBtn.setEnabled(false);
                        }

                    }
                });
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
                comment.setText("");
                refresh();
            });
        });

        swipeRefresh = view.findViewById(R.id.comment_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> refresh());

        RecyclerView list = view.findViewById(R.id.postdetails_rv_comment);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DetailsPostFragment.MyAdapter();
        list.setAdapter(adapter);
        currUser = Model.instance.getCurrentUserModel();

        authorEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (senderId.equals(currUser.getId()))
                    Navigation.findNavController(v).navigate(DetailsPostFragmentDirections.actionDetailsPostFragmentToNavigationDashboard());
                else
                    Navigation.findNavController(v).navigate(DetailsPostFragmentDirections.actionDetailsPostFragmentToOthersProfileFragment(senderId));
            }
        });

        refresh();
        return view;
    }

    private void refresh() {
        Model.instance.getPostComments(postId, commentsList -> {
            if(commentsList.size() ==0)
                swipeRefresh.setVisibility(View.GONE);
            else {
                System.out.println("Comments" + commentsList.size());
                postViewModel.data = commentsList;
                adapter.notifyDataSetChanged();
            }

        });
        swipeRefresh.setRefreshing(false);
    }


    public void displayPost(String title, String description, String time, String senderId, String status, String role) {
        Model.instance.findUserById(senderId, user -> {
            titleEt.setText(title);
            descriptionEt.setText(description);
            timeEt.setText(time);
            authorEt.setText(user.get("name").toString().replace("\"", "")); //TODO: find user by ID
            statusEt.setText(status);
            roleEt.setText(role);
            updateUI(View.VISIBLE);

            if (role == "SOS") {
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


    //#### Comments ViewHolder ####

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username,time,rate,correct;
        EditText content;
        Button delete,edit,editsave,positive,negative;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            username = itemView.findViewById(R.id.connectionListRow_userName_tv);
            time = itemView.findViewById(R.id.coomentListRow_time_tv);
            content = itemView.findViewById(R.id.coomentListRow_content_ev);

            delete=itemView.findViewById(R.id.coomentListRow_deleteBtn);
            edit=itemView.findViewById(R.id.coomentListRow_editBtn);
            editsave=itemView.findViewById(R.id.coomentListRow_saveEditBtn);
            positive=itemView.findViewById(R.id.coomentListRow_upBtn);
            negative=itemView.findViewById(R.id.coomentListRow_downBtn);
            rate=itemView.findViewById(R.id.coomentListRow_rateTv);
            correct=itemView.findViewById(R.id.coomentListRow_approvedTv);

            edit.setOnClickListener(v -> {
                //TODO: ADD REFRESH
                content.setEnabled(true);
                edit.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                editsave.setVisibility(View.VISIBLE);
            });

            editsave.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                Comment comment = postViewModel.getData().get(pos);

                HashMap<String, String> map = new HashMap<>();
                map.put("content", content.getText().toString());
                String id = comment.getCommentId().toString();

                Model.instance.editComment(map,id, () -> {
                    // TODO: Add comment to local DB ??
                    content.setEnabled(false);
                    edit.setVisibility(View.VISIBLE);
                    editsave.setVisibility(View.GONE);
                    delete.setVisibility(View.VISIBLE);
                    refresh();
                });

            });
            delete.setOnClickListener(v -> {
                //TODO: ADD REFRESH
                int pos = getAdapterPosition();
                Comment comment = postViewModel.getData().get(pos);

                String id = comment.getCommentId().toString();

                        Model.instance.deleteComment(id, () -> {
                        refresh();
                        });
                    });
            positive.setOnClickListener(v->{
                int pos=getAdapterPosition();
                Comment comment=postViewModel.getData().get(pos);
                String id=comment.getCommentId().toString();

                HashMap<String, String> map = new HashMap<>();
                map.put("user_rate", Model.instance.getCurrentUserModel().getId());

                Model.instance.upComment(id,map, () -> {
                    positive.setVisibility(View.GONE);
                    negative.setVisibility(View.VISIBLE);
                    refresh();
                });
            });
            negative.setOnClickListener(v->{
               int pos=getAdapterPosition();
                Comment comment=postViewModel.getData().get(pos);
                String id=comment.getCommentId().toString();

                HashMap<String, String> map = new HashMap<>();
                map.put("user_rate", Model.instance.getCurrentUserModel().getId());

                Model.instance.downComment(id, map,() -> {
                    positive.setVisibility(View.GONE);
                    negative.setVisibility(View.VISIBLE);
                    refresh();
                });
            });
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
            });
        }

        @SuppressLint("SimpleDateFormat")
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void bind(Comment comment) {
            Model.instance.findUserById(comment.getSender(), new Model.findUserByIdListener() {
                @Override

                public void onComplete(JsonObject user) {
                    username.setText(user.get("name").toString().replace("\"", ""));

                    /*  ## if login user is the same as the comment.sender user
                        ## hide the ability to rate the comment
                        ## show the ability to delete and edit */
                     if(user.get("email").toString().replace("\"", "").compareTo(Model.instance.getCurrentUserModel().getEmail())==0){
                         delete.setVisibility(View.VISIBLE);
                         edit.setVisibility(View.VISIBLE);
                         positive.setVisibility(View.GONE);
                         negative.setVisibility(View.GONE); }
                     else{
                         delete.setVisibility(View.GONE);
                         edit.setVisibility(View.GONE);
                         positive.setVisibility(View.VISIBLE);
                         negative.setVisibility(View.VISIBLE); }
                }
            });


            // # check if the login user already rated
            if(comment.IsUserRated_negative(Model.instance.getCurrentUserModel().getId())){
                negative.setVisibility(View.GONE); }
            else if(comment.IsUserRated_positive(Model.instance.getCurrentUserModel().getId())) {
                positive.setVisibility(View.GONE); }

            //# check what is the rate of the comment - calc in model
            int rate_val=comment.getCommentRate();
            rate.setText(String.valueOf(rate_val));

            //# check if the comment IsCorrect
            if(comment.IsCorrect().compareTo("true")==0){
                correct.setVisibility(View.VISIBLE); }
            else{
                correct.setVisibility(View.GONE); }


            content.setText(comment.getContent());
            String newTime = comment.getCurrentTime().substring(0, 16).replace("T", "  ").replace("-", "/");
            time.setText(newTime);


        }
    }

    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

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
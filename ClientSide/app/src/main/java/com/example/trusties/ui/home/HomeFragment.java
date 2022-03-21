package com.example.trusties.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.trusties.MainActivity;
import com.example.trusties.R;
import com.example.trusties.model.Post;
import com.example.trusties.model.User;
import com.example.trusties.databinding.FragmentHomeBinding;
import com.example.trusties.model.Model;
import com.google.gson.JsonObject;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    String usersEmail;
    public static User connectedUser;

    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /***********************************/
        usersEmail = MainActivity.usersEmail;
        TextView userName = root.findViewById(R.id.home_userName_tv);
        Model.instance.findUserByEmail(usersEmail, new Model.findUserByEmailListener() {
            @Override
            public void onComplete(JsonObject user) {
                connectedUser = new User(user.get("name").toString(), user.get("email").toString(), user.get("phone").toString());
                userName.setText(connectedUser.getFullName().replace("\"", ""));
            }
        });
        /************************************/

        swipeRefresh = root.findViewById(R.id.home_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> refresh());

        RecyclerView list = root.findViewById(R.id.home_postlist_rv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyAdapter();
        list.setAdapter(adapter);

        Model.instance.getAllPosts(postsList -> {

        });

        adapter.setOnItemClickListener((v, position) -> {
            String postId = homeViewModel.getData().get(position).getId();
            System.out.println("the postID is:  " + postId);
            Navigation.findNavController(v).navigate(HomeFragmentDirections.actionNavigationHomeToDetailsPostFragment(postId));
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void refresh() {
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }
    /* *************************************** Holder *************************************** */

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            title = itemView.findViewById(R.id.listrow_username_tv);
            description = itemView.findViewById(R.id.listrow_post_text_tv);


            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                listener.onItemClick(v, pos);
            });
        }

        public void bind(Post post) {

            title.setText(post.getTitle());
            description.setText(post.getDescription());

        }
    }

    /* *************************************** Adapter *************************************** */

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

            View view = getLayoutInflater().inflate(R.layout.post_list_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view, listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Post post = homeViewModel.getData().get(position);
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            if (homeViewModel.getData() == null) {
                return 0;
            }
            return homeViewModel.getData().size();
        }
    }
}
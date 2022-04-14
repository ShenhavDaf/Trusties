package com.example.trusties.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import com.example.trusties.ui.home.HomeFragment;
import com.example.trusties.ui.home.HomeFragmentDirections;
import com.example.trusties.ui.home.HomeViewModel;
import com.google.android.material.card.MaterialCardView;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    TextView userName;
    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;
    User currUser;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /**********************************/

        userName = root.findViewById(R.id.profile_name);
        if (Model.instance.getCurrentUserModel() != null) {
            userName.setText(Model.instance.getCurrentUserModel().getFullName());
            currUser = Model.instance.getCurrentUserModel();
            Log.d("TAG",currUser.toString());
        }
        else
            userName.setText("Guest");

        /**********************************/
        swipeRefresh = root.findViewById(R.id.profile_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> refresh());

        RecyclerView list = root.findViewById(R.id.profile_postlist_rv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyAdapter();
        list.setAdapter(adapter);


        adapter.setOnItemClickListener((v, position) -> {
            System.out.println("the POSITION is:  " + position);

            String postId = dashboardViewModel.getData().get(position).getId();
            System.out.println("the postID is:  " + postId);
            Navigation.findNavController(v).navigate(DashboardFragmentDirections.actionNavigationDashboardToDetailsPostFragment(postId));
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
        Model.instance.getMyPosts(currUser.getId(),postsList -> {
            dashboardViewModel.data = postsList;
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

        public MyViewHolder(@NonNull View itemView, DashboardFragment.OnItemClickListener listener) {
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

    class MyAdapter extends RecyclerView.Adapter<DashboardFragment.MyViewHolder> {

        DashboardFragment.OnItemClickListener listener;

        public void setOnItemClickListener(DashboardFragment.OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public DashboardFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = getLayoutInflater().inflate(R.layout.post_list_row, parent, false);
            DashboardFragment.MyViewHolder holder = new DashboardFragment.MyViewHolder(view, listener);
            return holder;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(@NonNull DashboardFragment.MyViewHolder holder, int position) {
            Post post = dashboardViewModel.getData().get(position);
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            if (dashboardViewModel.getData() == null) {
                return 0;
            }
            return dashboardViewModel.getData().size();
        }
    }
}
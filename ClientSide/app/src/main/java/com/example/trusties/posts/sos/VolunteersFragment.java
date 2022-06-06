package com.example.trusties.posts.sos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.trusties.databinding.FragmentVolunteersBinding;
import com.example.trusties.model.Model;
import com.example.trusties.model.User;
import com.example.trusties.ui.home.HomeFragmentDirections;
import com.google.gson.JsonObject;

import java.util.HashMap;

public class VolunteersFragment extends Fragment {

    private VolunteersViewModel viewModel;
    private FragmentVolunteersBinding binding;
    private String postId;
    private User logInUser;


    //TODO:CHANGE IT
    VolunteersFragment.MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        postId = VolunteersFragmentArgs.fromBundle(getArguments()).getPostId();
        viewModel = new ViewModelProvider(this, new VolunteersModelFactory(postId)).get(VolunteersViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =FragmentVolunteersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        swipeRefresh = view.findViewById(R.id.requests_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> refresh());

        RecyclerView list = view.findViewById(R.id.requests_rv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VolunteersFragment.MyAdapter();
        list.setAdapter(adapter);
        logInUser = Model.instance.getCurrentUserModel();

        adapter.setOnItemClickListener((v, position) -> {
            String senderId = viewModel.getData().get(position).getId();
            Log.d("TAG", "SENDER~~~~~~~   "+ senderId);
            Navigation.findNavController(v).navigate(VolunteersFragmentDirections.actionVolunteersFragmentToOthersProfileFragment(senderId));
        });


        refresh();
        return view;
    }

    private void refresh() {
        Model.instance.getSosVolunteers(postId,VolunteersList ->{
            if(VolunteersList.size() ==0) {
                swipeRefresh.setVisibility(View.GONE);
            }
            else{
                viewModel.data =VolunteersList;
                adapter.notifyDataSetChanged();
            }
        });
        swipeRefresh.setRefreshing(false);

//
//        adapter.notifyDataSetChanged();
//        swipeRefresh.setRefreshing(false);
    }

    public VolunteersFragment() {
        // Required empty public constructor
    }

    //#### Comments ViewHolder ####

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName, numberConnections;
        Button approve;
        RatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView, VolunteersFragment.OnItemClickListener listener) {
            super(itemView);


            userName = itemView.findViewById(R.id.volunteerListRow_userName_tv);
            numberConnections = itemView.findViewById(R.id.volunteerListRow_mutual);
            approve = itemView.findViewById(R.id.volunteerListRow_approveBtn);
            ratingBar = itemView.findViewById(R.id.volunteersListRow_ratingBar);

            approve.setOnClickListener(v->{
                approve.setBackgroundColor(Color.parseColor("#FF4CAF50"));
                int pos=getAdapterPosition();
                User vol=viewModel.getData().get(pos);

                HashMap<String, String> map = new HashMap<>();
                map.put("vol_id",vol.getId().toString());

                Model.instance.approveVolunteer(postId,map, () -> {
                    approve.setVisibility(View.GONE);
                    refresh();
                });
            });


            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                listener.onItemClick(v, pos);
            });
        }

        @SuppressLint("SimpleDateFormat")
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void bind(User user) {
            userName.setText(user.getFullName());
            Model.instance.findUserByEmail(user.getEmail(), new Model.findUserByEmailListener() {
                @Override
                public void onComplete(JsonObject user) {
                    String rate = user.get("rating").toString().replace("\"", "");
                    ratingBar.setRating(Float.parseFloat(rate));

                }
            });


            Model.instance.getFriendsList(user.getId(),1, friends -> {
                System.out.println("friends");
                System.out.println(friends);
                numberConnections.setText(friends.size() +" connections");
//                refresh();
            });

//            numberConnections.setText(user.getFriends().size() + " connections");


        }
    }

    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    class MyAdapter extends RecyclerView.Adapter<VolunteersFragment.MyViewHolder> {

            VolunteersFragment.OnItemClickListener listener;

            public void setOnItemClickListener(VolunteersFragment.OnItemClickListener listener) {
                this.listener = listener;
            }

            @NonNull
            @Override
            public VolunteersFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = getLayoutInflater().inflate(R.layout.volunteer_list_row, parent, false);
                VolunteersFragment.MyViewHolder holder = new VolunteersFragment.MyViewHolder(view, listener);
                return holder;
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onBindViewHolder(@NonNull VolunteersFragment.MyViewHolder holder, int position) {
                User user = viewModel.getData().get(position);
                holder.bind(user);
            }

            @Override
            public int getItemCount() {
                if (viewModel.getData() == null) {
                    return 0;
                }
                return viewModel.getData().size();
            }
        }



}
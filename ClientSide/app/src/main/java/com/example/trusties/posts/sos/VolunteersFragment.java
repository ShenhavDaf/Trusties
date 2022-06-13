package com.example.trusties.posts.sos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.gson.JsonObject;

import java.util.HashMap;

public class VolunteersFragment extends Fragment {

    private VolunteersViewModel viewModel;
    private FragmentVolunteersBinding binding;
    private String postId;
    private User logInUser;
    private User approveVolunteer_db = null;


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
        binding = FragmentVolunteersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        swipeRefresh = view.findViewById(R.id.requests_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> refresh());

        RecyclerView list = view.findViewById(R.id.requests_rv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VolunteersFragment.MyAdapter();
        list.setAdapter(adapter);
        logInUser = Model.instance.getCurrentUserModel();

        Model.instance.getApprovedVolunteer(postId, volunteer -> {
            if (volunteer != null) {
                approveVolunteer_db = User.create(volunteer);
            }
        });

        adapter.setOnItemClickListener((v, position) -> {
            String senderId = viewModel.getData().get(position).getId();
            Log.d("TAG", "SENDER~~~~~~~   " + senderId);
            Navigation.findNavController(v).navigate(VolunteersFragmentDirections.actionVolunteersFragmentToOthersProfileFragment(senderId));
        });

        refresh();
        return view;
    }

    private void refresh() {
        Model.instance.getSosVolunteers(postId, VolunteersList -> {
            if (VolunteersList.size() == 0) {
                swipeRefresh.setVisibility(View.GONE);
            } else {
                viewModel.data = VolunteersList;
                adapter.notifyDataSetChanged();
            }
        });
    }

    public VolunteersFragment() {
        // Required empty public constructor
    }

    //#### Comments ViewHolder ####

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName, numberConnections;
        Button approve, cancel;
        RatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView, VolunteersFragment.OnItemClickListener listener) {
            super(itemView);


            userName = itemView.findViewById(R.id.volunteerListRow_userName_tv);
            numberConnections = itemView.findViewById(R.id.volunteerListRow_mutual);
            approve = itemView.findViewById(R.id.volunteerListRow_approveBtn);
            cancel = itemView.findViewById(R.id.volunteerListRow_cancelBtn);
            ratingBar = itemView.findViewById(R.id.volunteersListRow_ratingBar);

            cancel.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                User vol = viewModel.getData().get(pos);

                HashMap<String, String> map = new HashMap<>();
                map.put("vol_id", vol.getId());

                Model.instance.cancelVolunteer(postId, map, () -> {
                    Navigation.findNavController(v).navigate(
                            VolunteersFragmentDirections.actionVolunteersFragmentToDetailsPostFragment(postId));
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

            if (approveVolunteer_db == null) {
                approve.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
            } else {
                if (user.getEmail().compareTo(approveVolunteer_db.getEmail()) == 0) {
                    approve.setVisibility(View.GONE);
                    cancel.setVisibility(View.VISIBLE);
                } else {
                    approve.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
                }

            }


            Model.instance.findUserByEmail(user.getEmail(), new Model.findUserByEmailListener() {
                @Override
                public void onComplete(JsonObject user) {
                    String rate = user.get("rating").toString().replace("\"", "");
                    ratingBar.setRating(Float.parseFloat(rate));
                }
            });


            Model.instance.getFriendsList(user.getId(), 1, friends -> {
                System.out.println("friends");
                System.out.println(friends);
                numberConnections.setText(friends.size() + " connections");
            });

            approve.setOnClickListener(v -> {
//                approve.setBackgroundColor(Color.parseColor("#FF4CAF50"));
                int pos = getAdapterPosition();
                User vol = viewModel.getData().get(pos);

                HashMap<String, String> map = new HashMap<>();
                map.put("vol_id", vol.getId());

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Approve volunteer").
                        setMessage("Are you sure you want to approve " + user.getFullName() + " to come and help you?");
                builder.setPositiveButton("Yes", (dialog, id) -> {
                    Model.instance.approveVolunteer(postId, map, () -> {
                        Navigation.findNavController(v).navigate(
                                VolunteersFragmentDirections.actionVolunteersFragmentToDetailsPostFragment(postId));
                    });
                });
                builder.setNegativeButton("No", (dialog, id) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.show();
                /* ------ Add Notification ------ */
                HashMap<String, String> notification = new HashMap<>();
                notification.put("sender", vol.getId());
                notification.put("post", postId);
                notification.put("time", (new Long(0)).toString());
                notification.put("type", "approveVolunteer");
                notification.put("circle", "0");

                Model.instance.addNotification(notification, () -> {
                    System.out.println("## Back from server :: addNotification");
                });
            });


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
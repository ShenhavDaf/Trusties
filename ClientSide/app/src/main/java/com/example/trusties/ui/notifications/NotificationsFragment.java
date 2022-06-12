package com.example.trusties.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
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
import com.example.trusties.databinding.FragmentNotificationsBinding;
import com.example.trusties.model.Model;
import com.example.trusties.model.Notification;
import com.example.trusties.model.Post;
import com.example.trusties.model.User;
import com.example.trusties.posts.AddPostFragmentDirections;
import com.example.trusties.ui.profile.OthersProfileFragmentArgs;
import com.google.android.material.card.MaterialCardView;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;

    NotificationsFragment.MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;

    User user;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        user = Model.instance.getCurrentUserModel();

        swipeRefresh = root.findViewById(R.id.notification_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> refresh());

        RecyclerView list = root.findViewById(R.id.notification_rv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationsFragment.MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener((v, position) -> {
            String type = notificationsViewModel.getData().get(position).getType();

            if(type.equals("friendRequest") || type.equals("approveFriendRequest")) {
                String authorId = notificationsViewModel.getData().get(position).getAuthorID();
                Navigation.findNavController(v).navigate(NotificationsFragmentDirections.actionNavigationNotificationsToOthersProfileFragment(authorId));
            } else {
                String postId = notificationsViewModel.getData().get(position).getPostID();
                Navigation.findNavController(v).navigate(NotificationsFragmentDirections.actionNavigationNotificationsToDetailsPostFragment(postId));
            }
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
        Model.instance.getAllNotificationsById(user.getId(), notificationsList -> {
            notificationsViewModel.data = notificationsList;
            adapter.notifyDataSetChanged();
        });
        swipeRefresh.setRefreshing(false);
    }

    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView description, time;

        public MyViewHolder(@NonNull View itemView, NotificationsFragment.OnItemClickListener listener) {
            super(itemView);

            time = itemView.findViewById(R.id.notification_time_tv);
            description = itemView.findViewById(R.id.notification_description_tv);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                listener.onItemClick(v, pos);
            });
        }


        @SuppressLint("SimpleDateFormat")
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void bind(Notification notification) {
            String type = notification.getType();

            if (type.equals("sos")) {
                MaterialCardView card = (MaterialCardView) itemView;
                card.setCardBackgroundColor(card.getContext().getColor(R.color.sosCardBackground));
            }

            Model.instance.findUserById(notification.getAuthorID(), user -> {
                    String userName = user.get("name").getAsString();
                    if(type.equals("sos")) {
                        description.setText(userName + " shared a SOS call");
                    }
                    else if(type.equals("friendRequest")) {
                        description.setText(userName + " sent you a friend request");
                    }
                    else if(type.equals("comment")) {
                        description.setText(userName + " commented on your post");
                    }
                    else if(type.equals("volunteer")) {
                        description.setText(userName + " volunteered to help you");
                    }
                    else if(type.equals("approveFriendRequest")) {
                        description.setText(userName + " approve your friend request");
                    }
                }
            );

            String newTime = notification.getTime().substring(0, 16).replace("T", "  ").replace("-", "/");
            time.setText(newTime);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<NotificationsFragment.MyViewHolder> {

        NotificationsFragment.OnItemClickListener listener;

        public void setOnItemClickListener(NotificationsFragment.OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public NotificationsFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = getLayoutInflater().inflate(R.layout.notification_list_row, parent, false);
            NotificationsFragment.MyViewHolder holder = new NotificationsFragment.MyViewHolder(view, listener);
            return holder;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(@NonNull NotificationsFragment.MyViewHolder holder, int position) {
            Notification notification = notificationsViewModel.getData().get(position);
            holder.bind(notification);
        }

        @Override
        public int getItemCount() {
            if (notificationsViewModel.getData() == null) {
                return 0;
            }
            return notificationsViewModel.getData().size();
        }
    }
}
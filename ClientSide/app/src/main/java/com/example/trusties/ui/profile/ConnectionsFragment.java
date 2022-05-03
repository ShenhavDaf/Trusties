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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.trusties.R;
import com.example.trusties.databinding.FragmentConnectionsBinding;
import com.example.trusties.databinding.FragmentDashboardBinding;
import com.example.trusties.model.Model;
import com.example.trusties.model.Post;
import com.example.trusties.model.User;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;


public class ConnectionsFragment extends Fragment {

    private ConnectionsViewModel connectionsViewModel;
    private FragmentConnectionsBinding binding;
    SwipeRefreshLayout swipeRefresh;
    MyAdapter adapter;
    User currUser;
    Button secondCircle;
    Button firstCircle;
    Button thirdCircle;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        connectionsViewModel = new ViewModelProvider(this).get(ConnectionsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConnectionsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
//        View view =  inflater.inflate(R.layout.fragment_connections, container, false);
        /**********************************/

        currUser = Model.instance.getCurrentUserModel();
        swipeRefresh = root.findViewById(R.id.connections_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> refreshFirstCircle(1));

        RecyclerView list = root.findViewById(R.id.connectionListRow_mutual);
        Log.d("TAG", getContext().toString());
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyAdapter();
        list.setAdapter(adapter);


        adapter.setOnItemClickListener((v, position) -> {
            System.out.println("the POSITION is:  " + position);

            String userId = connectionsViewModel.getData().get(position).get("id").toString().replace("\"", "");
            System.out.println("the userID is:  " + userId);
           // Navigation.findNavController(v).navigate(ProfileFragmentDirections.actionNavigationDashboardToDetailsPostFragment(postId));
        });

        secondCircle = root.findViewById(R.id.connections_secondcircle_btn);
        secondCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshFirstCircle(2);
            }
        });

        firstCircle =  root.findViewById(R.id.connections_firstcircle_btn);
        firstCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshFirstCircle(1);
            }
        });

        thirdCircle =  root.findViewById(R.id.connections_thirdcircle_btn);
        thirdCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                refreshFirstCircle(3);
            }
        });

//
//        refreshFirstCircle(1);
        return root;
    }

    private void refreshFirstCircle(int circle) {
        List<JsonObject> userId = new LinkedList<>();
        Model.instance.getFriendsList(currUser.getId(), circle, new Model.friendsListListener() {
            @Override
            public void onComplete(JsonArray friendsList) {
//              connectionsViewModel.data = friendsList;
                for (JsonElement elem:friendsList){
//                    Log.d("TAG", " elem " + elem.toString().replace("\"", ""));
                    Model.instance.findUserById(elem.toString().replace("\"", ""), new Model.findUserByIdListener() {
                        @Override
                        public void onComplete(JsonObject user) {
                            Log.d("TAG", " complete " + user);
                            userId.add(user);
                        }
                    });

                }
                connectionsViewModel.data = userId;
//                adapter.notifyDataSetChanged();
            }
        });
//        adapter.notifyDataSetChanged();
//        Model.instance.getAllUsers(usersList -> {
//            connectionsViewModel.data = usersList;
            adapter.notifyDataSetChanged();
//        });

        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    /* *************************************** Holder *************************************** */

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName, numberConnections;

        public MyViewHolder(@NonNull View itemView, ConnectionsFragment.OnItemClickListener listener) {
            super(itemView);

            userName = itemView.findViewById(R.id.connectionListRow_userName_tv);
            numberConnections = itemView.findViewById(R.id.connectionListRow_mutual);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                listener.onItemClick(v, pos);
            });
        }


        @SuppressLint("SimpleDateFormat")
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void bind(JsonObject user) {
            userName.setText(user.get("name").toString().replace("\"", ""));
            numberConnections.setText("100 connections");
        }
    }



    /* *************************************** Adapter *************************************** */

    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    class MyAdapter extends RecyclerView.Adapter<ConnectionsFragment.MyViewHolder> {

        ConnectionsFragment.OnItemClickListener listener;

        public void setOnItemClickListener(ConnectionsFragment.OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public ConnectionsFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = getLayoutInflater().inflate(R.layout.connection_list_row, parent, false);
            ConnectionsFragment.MyViewHolder holder = new ConnectionsFragment.MyViewHolder(view, listener);
            return holder;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(@NonNull ConnectionsFragment.MyViewHolder holder, int position) {
            JsonObject user = connectionsViewModel.getData().get(position);
            Log.d("TAG","user - " + user);
            holder.bind(user);
        }

        @Override
        public int getItemCount() {
            if (connectionsViewModel.getData() == null) {
                return 0;
            }
            return connectionsViewModel.getData().size();
        }
    }
}
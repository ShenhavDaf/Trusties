package com.example.trusties.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.trusties.CommonFunctions;
import com.example.trusties.R;
import com.example.trusties.model.Model;
import com.example.trusties.model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.List;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;

    TextView categoriesBtn, statusBtn, authorBtn, descriptionBtn;
    Button findFriendBtn, searchBtn;
    ConstraintLayout categoriesLayout, statusLayout;
    EditText authorET, descriptionET, newFriendET;
    CheckBox carCB, deliveryCB, toolsCB, houseCB, openCB, waitingCB, closeCB;

    SwipeRefreshLayout swipeRefresh;
    MyAdapter adapter;
    List<User> lst = new LinkedList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        categoriesBtn = view.findViewById(R.id.search_categories_tv);
        categoriesLayout = view.findViewById(R.id.search_categories_layout);
        carCB = view.findViewById(R.id.search_car_cb);
        deliveryCB = view.findViewById(R.id.search_delivery_cb);
        toolsCB = view.findViewById(R.id.search_tools_cb);
        houseCB = view.findViewById(R.id.search_house_cb);


        statusBtn = view.findViewById(R.id.search_status_tv);
        statusLayout = view.findViewById(R.id.search_status_layout);
        openCB = view.findViewById(R.id.search_open_cb);
        waitingCB = view.findViewById(R.id.search_waiting_cb);
        closeCB = view.findViewById(R.id.search_close_cb);


        authorBtn = view.findViewById(R.id.search_author_tv);
        authorET = view.findViewById(R.id.search_author_et);


        descriptionBtn = view.findViewById(R.id.search_description_tv);
        descriptionET = view.findViewById(R.id.search_description_et);


        findFriendBtn = view.findViewById(R.id.search_friend_btn);
        newFriendET = view.findViewById(R.id.search_friend_et);

        searchBtn = view.findViewById(R.id.search_btn);

        swipeRefresh = view.findViewById(R.id.search_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> searchFunction(view));

        RecyclerView list = view.findViewById(R.id.search_list_rv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener((v, position) -> {
            Model.instance.findUserByEmail(searchViewModel.getData().get(position).getEmail(), user -> {
                String userId = user.get("_id").toString().replace("\"", "");
                if (userId.equals(Model.instance.getCurrentUserModel().getId())) {
                    Navigation.findNavController(v).navigate(SearchFragmentDirections.actionGlobalNavigationDashboard());
                } else
                    Navigation.findNavController(v).navigate(SearchFragmentDirections.actionSearchFragmentToOthersProfileFragment(userId));
            });

        });

        /*------------------------------------------------------------------*/
        categoriesBtn.setOnClickListener(v -> {
            switchVisibility(categoriesLayout);
        });

        statusBtn.setOnClickListener(v -> {
            switchVisibility(statusLayout);
        });

        authorBtn.setOnClickListener(v -> {
            switchVisibility(authorET);
        });

        descriptionBtn.setOnClickListener(v -> {
            switchVisibility(descriptionET);
        });

        findFriendBtn.setOnClickListener(v -> {
            switchVisibility(newFriendET);
            switchVisibility(searchBtn);
        });

        searchBtn.setOnClickListener(v -> searchFunction(v));

        return view;
    }

    private void switchVisibility(View object) {
        if (object.getVisibility() == View.GONE)
            object.setVisibility(View.VISIBLE);
        else object.setVisibility(View.GONE);
    }

    private void refresh() {
        searchViewModel.data = lst;
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    private void searchFunction(View v) {
        lst.clear();
        String friendName = newFriendET.getText().toString().toLowerCase().trim();
        if (!friendName.equals("")) {
            Model.instance.getAllUsers(usersList -> {
                for (User user : usersList) {
                    if (user.getFullName().toLowerCase().contains(friendName)) {
                        lst.add(user);
                        adapter.notifyDataSetChanged();
                    }
                }

                searchViewModel.data = lst;
            });
        } else {
            new CommonFunctions().myPopup(this.getContext(), "Search error", "No search member name received");
        }
        swipeRefresh.setRefreshing(false);
        refresh();
    }


    /* *************************************** Holder *************************************** */

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName, numberConnections;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
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
        public void bind(User user) {
            userName.setText(user.getFullName());
            Model.instance.findUserByEmail(user.getEmail(), new Model.findUserByEmailListener() {
                @Override
                public void onComplete(JsonObject freind) {
                    Model.instance.getFriendsList(freind.get("_id").toString().replace("\"", ""), 1, new Model.friendsListListener() {
                        @Override
                        public void onComplete(JsonArray friendsList) {
                            numberConnections.setText(friendsList.size() + " connections");
                        }
                    });
                }
            });
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

            View view = getLayoutInflater().inflate(R.layout.connection_list_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view, listener);
            return holder;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            User user = searchViewModel.getData().get(position);
            holder.bind(user);
        }

        @Override
        public int getItemCount() {
            if (searchViewModel.getData() == null) {
                return 0;
            }
            return searchViewModel.getData().size();
        }
    }

}

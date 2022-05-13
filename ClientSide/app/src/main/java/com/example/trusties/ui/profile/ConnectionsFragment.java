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
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.trusties.CommonFunctions;
import com.example.trusties.R;
import com.example.trusties.databinding.FragmentConnectionsBinding;
import com.example.trusties.model.Model;
import com.example.trusties.model.User;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import java.util.LinkedList;
import java.util.List;


public class ConnectionsFragment extends Fragment {

    private ConnectionsViewModel connectionsViewModel;
    private FragmentConnectionsBinding binding;
    SwipeRefreshLayout swipeRefresh;
    MyAdapter adapter;
    Button firstCircle, secondCircle, thirdCircle;
    ImageButton searchBtn, refreshBtn;
    TextView searchBar;
    User currUser;

    String userId;
    int flagSecond = 0;
    int flagFirst = 0;
    int flagThird = 0;
    List<JsonObject> lst = new LinkedList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        connectionsViewModel = new ViewModelProvider(this).get(ConnectionsViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConnectionsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        /**********************************/

        currUser = Model.instance.getCurrentUserModel();
        swipeRefresh = root.findViewById(R.id.connections_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> refreshFirstCircle(1));

        RecyclerView list = root.findViewById(R.id.connectionListRow_mutual);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener((v, position) -> {
            Model.instance.findUserByEmail(connectionsViewModel.getData().get(position).get("email").toString().replace("\"", ""), new Model.findUserByEmailListener() {
                @Override
                public void onComplete(JsonObject user) {
                    userId = user.get("_id").toString().replace("\"", "");
                    removeFromLst();
                    Navigation.findNavController(v).navigate(ConnectionsFragmentDirections.actionConnectionsFragmentToOthersProfileFragment(userId));
                }
            });

        });


        searchBar = root.findViewById(R.id.connections_search_tv);
        searchBtn = root.findViewById(R.id.connections_search_btn);
        refreshBtn = root.findViewById(R.id.connections_refresh_btn);
        refreshBtn.setVisibility(View.GONE);

        searchBtn.setOnClickListener(v -> searchFunction());
        refreshBtn.setOnClickListener(v -> refreshFunction());


        firstCircle = root.findViewById(R.id.connections_firstcircle_btn);
        firstCircle.setOnClickListener(v -> {
            refreshFirstCircle(1);
        });

        secondCircle = root.findViewById(R.id.connections_secondcircle_btn);
        secondCircle.setOnClickListener(v -> {
            adapter.notifyDataSetChanged();
            getSecondCircle();
            swipeRefresh.setRefreshing(false);
        });

        thirdCircle = root.findViewById(R.id.connections_thirdcircle_btn);
        thirdCircle.setOnClickListener(v -> {
            getThirdCircle();
            adapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);

        });
        firstCircle.setBackgroundColor(firstCircle.getContext().getColor(R.color.lightGray));
        secondCircle.setBackgroundColor(secondCircle.getContext().getColor(R.color.lightGray));
        thirdCircle.setBackgroundColor(thirdCircle.getContext().getColor(R.color.lightGray));

        refreshFirstCircle(1);
        return root;
    }


    private void searchFunction() {
        String str = searchBar.getText().toString().toLowerCase();

        if (flagFirst == 0 && flagSecond == 0 && flagThird == 0) {
            new CommonFunctions().myPopup(this.getContext(), "Error", "Please select a circle number first..");
        } else if (str.equals("")) {
            new CommonFunctions().myPopup(this.getContext(), "Error", "Please enter name of friend..");
        } else {

            List<JsonObject> filteredList = new LinkedList<>();

            for (JsonObject user : lst) {
                if (user.get("name").getAsString().toLowerCase().contains(str))
                    filteredList.add(user);
            }

            connectionsViewModel.data = filteredList;
            adapter.notifyDataSetChanged();
            refreshBtn.setVisibility(View.VISIBLE);
            firstCircle.setEnabled(false);
            secondCircle.setEnabled(false);
            thirdCircle.setEnabled(false);

        }
    }

    private void refreshFunction() {
        connectionsViewModel.data = lst;
        adapter.notifyDataSetChanged();

        refreshBtn.setVisibility(View.GONE);
        firstCircle.setEnabled(true);
        secondCircle.setEnabled(true);
        thirdCircle.setEnabled(true);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void refreshFirstCircle(int circle) {
        if (flagFirst == 0) {
            flagFirst = 1;
            firstCircle.setBackgroundColor(firstCircle.getContext().getColor(R.color.titleColor));
            Model.instance.getFriendsList(currUser.getId(), circle, new Model.friendsListListener() {
                @Override
                public void onComplete(JsonArray friendsList) {
                    for (JsonElement elem : friendsList) {
                        Model.instance.findUserById(elem.toString().replace("\"", ""), new Model.findUserByIdListener() {
                            @Override
                            public void onComplete(JsonObject user) {
                                lst.add(user);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    connectionsViewModel.data = lst;
                }
            });
        } else {
            firstCircle.setBackgroundColor(firstCircle.getContext().getColor(R.color.lightGray));
            flagFirst = 0;
            Model.instance.getFriendsList(currUser.getId(), circle, new Model.friendsListListener() {
                @Override
                public void onComplete(JsonArray friendsList) {
                    for (JsonElement elem : friendsList) {
                        Model.instance.findUserById(elem.toString().replace("\"", ""), new Model.findUserByIdListener() {
                            @Override
                            public void onComplete(JsonObject user) {
                                lst.remove(user);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    connectionsViewModel.data = lst;
                }
            });
        }
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public List<JsonObject> getSecondCircle() {
        if (flagSecond == 0) {
            flagSecond = 1;
            secondCircle.setBackgroundColor(firstCircle.getContext().getColor(R.color.titleColor));
            Model.instance.getSecondCircle(currUser.getId(), new Model.secondCircleListener() {
                @Override
                public void onComplete(JsonArray friendsList) {

                    for (JsonElement elem : friendsList) {
                        Model.instance.findUserById(elem.toString().replace("\"", ""), new Model.findUserByIdListener() {
                            @Override
                            public void onComplete(JsonObject user) {
                                adapter.notifyDataSetChanged();
                                lst.add(user);
                            }
                        });
                    }
                    connectionsViewModel.data = lst;
                }
            });
        } else {
            secondCircle.setBackgroundColor(firstCircle.getContext().getColor(R.color.lightGray));
            flagSecond = 0;
            Model.instance.getSecondCircle(currUser.getId(), new Model.secondCircleListener() {
                @Override
                public void onComplete(JsonArray friendsList) {

                    for (JsonElement elem : friendsList) {
                        Model.instance.findUserById(elem.toString().replace("\"", ""), new Model.findUserByIdListener() {
                            @Override
                            public void onComplete(JsonObject user) {
                                adapter.notifyDataSetChanged();
                                lst.remove(user);

                            }
                        });
                    }
                    connectionsViewModel.data = lst;
                }
            });
        }
        return lst;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    List<JsonObject> getThirdCircle() {
        if (flagThird == 0) {
            flagThird = 1;
            thirdCircle.setBackgroundColor(firstCircle.getContext().getColor(R.color.titleColor));
            Model.instance.getThirdCircle(currUser.getId(), new Model.thirdCircleListener() {
                @Override
                public void onComplete(JsonArray friendsList) {
                    for (JsonElement elem : friendsList) {
                        Model.instance.findUserById(elem.toString().replace("\"", ""), new Model.findUserByIdListener() {
                            @Override
                            public void onComplete(JsonObject user) {
                                lst.add(user);
                                adapter.notifyDataSetChanged();

                            }
                        });
                    }
                    connectionsViewModel.data = lst;
                }
            });
        } else {
            thirdCircle.setBackgroundColor(firstCircle.getContext().getColor(R.color.lightGray));
            flagThird = 0;
            Model.instance.getThirdCircle(currUser.getId(), new Model.thirdCircleListener() {
                @Override
                public void onComplete(JsonArray friendsList) {
                    for (JsonElement elem : friendsList) {
                        Model.instance.findUserById(elem.toString().replace("\"", ""), new Model.findUserByIdListener() {
                            @Override
                            public void onComplete(JsonObject user) {
                                lst.remove(user);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    connectionsViewModel.data = lst;
                }
            });
        }
        return lst;
    }

    private void removeFromLst() {
        if (flagFirst == 1) {
            flagFirst = 0;
            Model.instance.getFriendsList(currUser.getId(), 1, new Model.friendsListListener() {
                @Override
                public void onComplete(JsonArray friendsList) {
                    for (JsonElement elem : friendsList) {
                        Model.instance.findUserById(elem.toString().replace("\"", ""), new Model.findUserByIdListener() {
                            @Override
                            public void onComplete(JsonObject user) {
                                lst.remove(user);
                               adapter.notifyDataSetChanged();

                            }
                        });
                    }
                    connectionsViewModel.data = lst;
                }
            });
        }
        if (flagSecond == 1) {
            flagSecond = 0;
            Model.instance.getSecondCircle(currUser.getId(), new Model.secondCircleListener() {
                @Override
                public void onComplete(JsonArray friendsList) {

                    for (JsonElement elem : friendsList) {
                        Model.instance.findUserById(elem.toString().replace("\"", ""), new Model.findUserByIdListener() {
                            @Override
                            public void onComplete(JsonObject user) {
                                adapter.notifyDataSetChanged();

                                lst.remove(user);

                            }
                        });
                    }
                    connectionsViewModel.data = lst;
                }
            });

        }
        if (flagThird == 1) {
            flagThird = 0;
            Model.instance.getThirdCircle(currUser.getId(), new Model.thirdCircleListener() {
                @Override
                public void onComplete(JsonArray friendsList) {
                    for (JsonElement elem : friendsList) {
                        Model.instance.findUserById(elem.toString().replace("\"", ""), new Model.findUserByIdListener() {
                            @Override
                            public void onComplete(JsonObject user) {
                                lst.remove(user);

                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    connectionsViewModel.data = lst;
                }
            });
        }
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
            Model.instance.findUserByEmail(user.get("email").toString().replace("\"", ""), new Model.findUserByEmailListener() {
                @Override
                public void onComplete(JsonObject user) {
                    Model.instance.getFriendsList(user.get("_id").toString().replace("\"", ""), 1, new Model.friendsListListener() {
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
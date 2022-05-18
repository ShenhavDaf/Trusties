package com.example.trusties.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.trusties.R;
import com.example.trusties.model.Comment;
import com.example.trusties.model.Post;
import com.example.trusties.databinding.FragmentHomeBinding;
import com.example.trusties.model.Model;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    //    String usersEmail;
//    public static User connectedUser;
    SearchView searchView;
    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;
    Bitmap decodedByte;
    List<Post> copyFullList;
    Boolean searchClose = false;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /***********************************/
//        usersEmail = MainActivity.usersEmail;
        TextView userName = root.findViewById(R.id.home_userName_tv);
        if (Model.instance.getCurrentUserModel() != null)
            userName.setText(Model.instance.getCurrentUserModel().getFullName());
        else
            userName.setText("Guest");


//        Model.instance.findUserByEmail(usersEmail, new Model.findUserByEmailListener() {
//            @Override
//            public void onComplete(JsonObject user) {
//                connectedUser = new User(user.get("name").toString(), user.get("email").toString(), user.get("phone").toString());
//                userName.setText(connectedUser.getFullName().replace("\"", ""));
//            }
//        });


        /************************************/

        swipeRefresh = root.findViewById(R.id.home_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> refresh());

        Model.instance.getAllPosts(postsList -> {
            homeViewModel.data = postsList;
            refresh();
        });


        RecyclerView list = root.findViewById(R.id.home_postlist_rv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyAdapter();
        list.setAdapter(adapter);


        adapter.setOnItemClickListener((v, position) -> {
            System.out.println("the POSITION is:  " + position);

            String postId = homeViewModel.getData().get(position).getId();
            System.out.println("the postID is:  " + postId);
            Navigation.findNavController(v).navigate(HomeFragmentDirections.actionNavigationHomeToDetailsPostFragment(postId));
        });


        searchView = root.findViewById(R.id.home_searchView);
        searchView.setOnClickListener(v->Navigation.findNavController(v).navigate(HomeFragmentDirections.actionGlobalSearchFragment()));
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return false;
//            }
//        });

//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//
//                searchClose = true;
////                homeViewModel.data.clear();
////                homeViewModel.data.addAll(copyFullList);
//////                refresh();
//                return false;
//            }
//        });

//        refresh();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void refresh() {
//        Model.instance.getAllPosts(postsList -> {
//            homeViewModel.data = postsList;
//
//        });
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    /* *************************************** Holder *************************************** */

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName, title, description, time, commentNumber, category, status,volunteer_txt,volunteer_count;
        ImageView photo, userImage,plusOne;

//        TextView userName, title, description, time, commentNumber,
        Button volunteer;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            userName = itemView.findViewById(R.id.listrow_username_tv);
            userImage = itemView.findViewById(R.id.listrow_avatar_imv);
            time = itemView.findViewById(R.id.listrow_date_tv);
            title = itemView.findViewById(R.id.listrow_post_title_tv);
            description = itemView.findViewById(R.id.listrow_post_description_tv);
            commentNumber = itemView.findViewById(R.id.listrow_comment_num_tv);
            category = itemView.findViewById(R.id.listrow_category_tv);
            status = itemView.findViewById(R.id.listrow_post_status_tv);
            photo = itemView.findViewById(R.id.listrow_post_img);
            plusOne = itemView.findViewById(R.id.listrow_plus_one_image);


            volunteer = itemView.findViewById(R.id.postListRow_volunteer);
            volunteer_txt= itemView.findViewById(R.id.post_listRow_volunteer_Tv);
            volunteer_count= itemView.findViewById(R.id.post_listRow_volunteerCount_Tv);


            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                listener.onItemClick(v, pos);
            });
            volunteer.setOnClickListener(v->{
                int pos=getAdapterPosition();
                Post post=homeViewModel.getData().get(pos);
                String id=post.getId();

                HashMap<String, String> map = new HashMap<>();
                map.put("vol_id", Model.instance.getCurrentUserModel().getId());

                Model.instance.volunteer(id,map, () -> {
                    refresh();
                });
            });

        }


        @SuppressLint("SimpleDateFormat")
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void bind(Post post) {

            // ##TYPE :SOS
            if (post.getRole().toLowerCase().equals("sos")) {

                MaterialCardView card = (MaterialCardView) itemView;
                card.setCardBackgroundColor(card.getContext().getColor(R.color.sosCardBackground));
                int volunteersSize=0;

                if (!Model.instance.getCurrentUserModel().getId().equals(post.getAuthorID())) {
                    if (post.getStatus().equals("OPEN")) {
                        //if the status is close & the current user is NOT the post sender
                        volunteer.setVisibility(View.VISIBLE);

                    }
                }

                Model.instance.getSosVolunteers(post.getId(), list ->
                {   for (int i = 0; i < list.size();i++) {
                        if (list.get(i).getEmail().equals(Model.instance.getCurrentUserModel().getEmail())) {
                            volunteer.setVisibility(View.GONE);
                            volunteer_txt.setVisibility(View.VISIBLE);
                        }
                    }
                    volunteer_count.setText(list.size()+"Volunteers");
                    volunteer_count.setVisibility(View.VISIBLE);
                });

            }
            // ##TYPE :SOS+QUES

            //TODO: change userName from post title to author name
            Model.instance.findUserById(post.getAuthorID(), user -> {
                userName.setText(user.get("name").getAsString());
                if (user.get("photo") != null) {
                    String photoBase64 = user.get("photo").getAsString();
                    byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    userImage.setImageBitmap(decodedByte);
                }
            });

            title.setText(post.getTitle());
            if (post.getDescription().length() > 150)
                description.setText(post.getDescription().substring(0, 150) + "...");
            else
                description.setText(post.getDescription());

            String newTime = post.getTime().substring(0, 16).replace("T", "  ").replace("-", "/");
            time.setText(newTime);

            Model.instance.getPostComments(post.getId(), commentsList -> {
                commentNumber.setText(commentsList.size() + " Comments ");
            });


            /* TODO: Update the "Post" model and use getters & setters instead of using getPostById
                Unnecessary server calls */
            Model.instance.getPostById(post.getId(), new Model.getPostByIdListener() {
                @Override
                public void onComplete(JsonObject post) {

                    status.setText(post.get("status").getAsString());
                    if (status.getText().equals("OPEN")) {
                        status.setBackgroundColor(status.getContext().getColor(R.color.green));
                    }
                    category.setText(post.get("category").getAsString());

                    if (post.get("photo").getAsJsonArray().size() > 0) {// CHANGED
                        if(post.get("photo").getAsJsonArray().size() == 2 )
                            plusOne.setVisibility(View.VISIBLE);
                        String photoBase64 = post.get("photo").getAsJsonArray().get(0).getAsString();
                        if (photoBase64 != null) {
                            byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            photo.setImageBitmap(decodedByte);
                        }
                    } else {
                        photo.setVisibility(View.GONE);
                    }
                }
            });

            // implement the ViewFactory interface and implement
            // unimplemented method that returns an imageView



//            comment.setOnClickListener(v -> {
//                HashMap<String,String> map = new HashMap<>();
//                map.put("email", "shenhav.dafadi@gmail.com");
//                map.put("postID", post.getId());
//                map.put("time", "123");
//                map.put("message", "new comment");
//
//                Model.instance.addComment(map, () -> {
//                    System.out.println("----------------------- blablka");
//                });
//            });
        }
    }

    /* *************************************** Adapter *************************************** */

    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements Filterable {

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

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Post post = homeViewModel.getData().get(position);
//            System.out.println(" ");
//            System.out.println("inside - {{{{{{{{{{{{{{{ homeViewModel.data }}}}}}}}}}}}} = " + homeViewModel.data);
//            System.out.println("inside - {{{{{{{{{{{{{{{ copyFullList }}}}}}}}}}}}} = " + copyFullList);
            copyFullList = new ArrayList<>(homeViewModel.getData());
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            if (homeViewModel.getData() == null) {
                return 0;
            }
            return homeViewModel.getData().size();
        }


        /* *************************************** Search *************************************** */
        @Override
        public Filter getFilter() {
            return myFilter;
        }

        private Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Post> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(copyFullList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Post item : copyFullList) {

                        System.out.println("=============== all list   " + copyFullList);

//                        Model.instance.getPostById(item.getId(), post -> {
//
//                            if (post.get("sender").toString().toLowerCase().contains(filterPattern)) {
//                                filteredList.add(item);
//                            } else if (post.get("title").toString().toLowerCase().contains(filterPattern)) {
//                                filteredList.add(item);
//                            } else if (post.get("description").toString().toLowerCase().contains(filterPattern)) {
//                                filteredList.add(item);
//                            } else if (post.get("status").toString().toLowerCase().contains(filterPattern)) {
//                                filteredList.add(item);
//                            } else if (post.get("role").toString().toLowerCase().contains(filterPattern)) {
//                                filteredList.add(item);
//                            }
//
//
//                        });

//                        if (item.sender.toLowerCase().contains(filterPattern)) {
//                            filteredList.add(item);
                        if (item.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        } else if (item.getDescription().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
//                        } else if (item.getCategory().toLowerCase().contains(filterPattern)) {
//                            filteredList.add(item);
                        } else if (item.getStatus().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                homeViewModel.data.clear();
                homeViewModel.data.addAll((List) results.values);
                if (searchClose) {
                    homeViewModel.data.addAll(copyFullList);
                }
                adapter.notifyDataSetChanged();
            }
        };
    }

}
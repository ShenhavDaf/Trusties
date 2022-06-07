package com.example.trusties.posts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.trusties.CommonFunctions;
import com.example.trusties.MyApplication;
import com.example.trusties.R;
import com.example.trusties.databinding.FragmentDetailsPostBinding;
import com.example.trusties.login.LoginActivity;
import com.example.trusties.model.Comment;
import com.example.trusties.model.Model;
import com.example.trusties.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.HashMap;


public class DetailsPostFragment extends Fragment implements OnMapReadyCallback {

    TextView titleEt, timeEt, authorEt, descriptionEt, statusEt, roleEt, addressEt;
    EditText comment;
    Button editBtn;
    ImageButton deleteBtn, closeBtn;
    Button requestsBtn;
    ProgressBar progressBar;
    ImageView postImg, imgUser, sendCommentBtn;
    View line;

    String currUserId;
    JsonObject currPost;

    String postId, senderId;
    User currUser;
    Bitmap decodedByte;

    private DetailsPostViewModel postViewModel;
    private FragmentDetailsPostBinding binding;

    MapView mapView;

    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;

    CarouselView carouselView;
    Bitmap[] sampleImages;
    String location = null;
    int isSOS = 0;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        postId = DetailsPostFragmentArgs.fromBundle(getArguments()).getPostId();
        postViewModel = new ViewModelProvider(this, new DetailsPostModelFactory(postId)).get(DetailsPostViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDetailsPostBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        currUserId = Model.instance.getCurrentUserModel().getId();

        progressBar = view.findViewById(R.id.postdetails_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        titleEt = view.findViewById(R.id.postdetails_title_tv);
        timeEt = view.findViewById(R.id.postdetails_time_tv);
        authorEt = view.findViewById(R.id.postdetails_author_tv);
        descriptionEt = view.findViewById(R.id.postdetails_description_tv);
        roleEt = view.findViewById(R.id.postdetails_role_tv);
        addressEt = view.findViewById(R.id.postdetails_address_tv);
        statusEt = view.findViewById(R.id.postdetails_status_tv);
        editBtn = view.findViewById(R.id.postdetails_edit_btn);
        deleteBtn = view.findViewById(R.id.postdetails_delete_btn);
//        postImg = view.findViewById(R.id.postDetails_post_img);
//        postImg.setVisibility(View.GONE);
        line = view.findViewById(R.id.postdetails_line);
        comment = view.findViewById(R.id.postdetails_comment_et);
        sendCommentBtn = view.findViewById(R.id.postdetails_sendComment_btn);
        imgUser = view.findViewById(R.id.postdetails_imgUser_img);
        requestsBtn = view.findViewById(R.id.postdetails_view_requests_btn);
        closeBtn = view.findViewById(R.id.postdetails_close_btn);
        mapView = view.findViewById(R.id.post_details_map);

        carouselView = view.findViewById(R.id.carouselView);

        updateUI(View.INVISIBLE);
        Model.instance.getPostById(postId, new Model.getPostByIdListener() {
            @Override
            public void onComplete(JsonObject post) {

                currPost = post;

                String address = null;

                String title = post.get("title").toString().replace("\"", "");
                String description = post.get("description").toString().replace("\"", "");
//                String time = post.get("time").toString().replace("\"", "");
                String time = post.get("time").getAsString().substring(0, 16).replace("T", "  ").replace("-", "/");
                senderId = post.get("sender").toString().replace("\"", "");
                String status = post.get("status").toString().replace("\"", "");
                String role = post.get("role").toString().replace("\"", "");
                if (role.equals("SOS")) {
                    isSOS = 1;
                    address = post.get("address").toString().replace("\"", "");
                    location = post.get("location").toString().replace("\"", "");
                    requestsBtn.setVisibility(View.VISIBLE);
                    closeBtn.setVisibility(View.VISIBLE);
                    String approved = post.get("approved_volunteer").toString().replace("\"", "");
                    if (!(currUserId.equals(senderId) || currUserId.equals(approved))) {
                        mapView.setVisibility(View.GONE);
                        String area = post.get("address").getAsString().split(",")[1] + ", " + post.get("address").getAsString().split(",")[2];
                        address = area;
                    }


                } else { // in post we don't have location
                    mapView.setVisibility(View.GONE);
                    addressEt.setVisibility(View.GONE);
                }
                if (status.equals("CLOSE"))
                    closeBtn.setVisibility(View.GONE);


                if (post.get("photo").getAsJsonArray().size() > 0) { // CHANGED
                    if (post.get("photo").getAsJsonArray().size() == 1)
                        sampleImages = new Bitmap[1];
                    if (post.get("photo").getAsJsonArray().size() == 2)
                        sampleImages = new Bitmap[2];
                    String photoBase64 = post.get("photo").getAsJsonArray().get(0).getAsString();
                    byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    sampleImages[0] = decodedByte;
                }
                if (post.get("photo").getAsJsonArray().size() == 2) { // CHANGED
                    String photoBase64 = post.get("photo").getAsJsonArray().get(1).getAsString();
                    byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    sampleImages[1] = decodedByte;
                }
                displayPost(title, description, time, senderId, status, role, sampleImages, address);
                progressBar.setVisibility(View.GONE);

                Model.instance.findUserById(currUser.getId(), new Model.findUserByIdListener() {
                    @Override
                    public void onComplete(JsonObject user) {
                        if (user.get("photo") != null) {
                            String photoBase64 = user.get("photo").getAsString();
                            byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imgUser.setImageBitmap(decodedByte);
                        }
                    }
                });

                //Checking if the Current user is the sender of the post for enabling the - EditBtn and DeleteBtn-
                Model.instance.findUserById(post.get("sender").toString().replace("\"", ""), new Model.findUserByIdListener() {
                    @Override
                    public void onComplete(JsonObject user) {

                        if (user.get("email").toString().replace("\"", "").compareTo(Model.instance.getCurrentUserModel().getEmail()) == 0) {
                            deleteBtn.setVisibility(View.VISIBLE);
                            editBtn.setVisibility(View.VISIBLE);
                            if (role.compareTo("SOS") == 0) {
//                                closeBtn.setVisibility(View.VISIBLE);
                                requestsBtn.setVisibility(View.VISIBLE);


                            }
                        } else {
//                            deleteBtn.setEnabled(false);
//                            editBtn.setEnabled(false);

                            deleteBtn.setVisibility(View.GONE);
                            editBtn.setVisibility(View.GONE);
                            closeBtn.setVisibility(View.GONE);
                            requestsBtn.setVisibility(View.GONE);
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
                comment.setText("");
                refresh();
            });

            /* ------ Add Notification ------ */
            HashMap<String, String> notification = new HashMap<>();
            notification.put("sender", user.getId());
            notification.put("post", postId);
            notification.put("time", (new Long(0)).toString());
            notification.put("type", "comment");
            notification.put("circle", "0");


            Model.instance.addNotification(notification, () -> {

            });

            Model.instance.sendNotification(notification, () -> {
            });

        });

        requestsBtn.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(DetailsPostFragmentDirections.actionDetailPostFragmentToVolunteersFragment(postId));
        });
        closeBtn.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Close SOS").
                    setMessage("You sure, that you want to close this SOS?");
            builder.setPositiveButton("Yes", (dialog, id) -> {
                // Change to "close"
                Model.instance.closeSos(postId, () -> {
                    // if someone approved
                    if (currPost.get("approved_volunteer").isJsonNull()) {
                        Navigation.findNavController(v).navigate(
                                DetailsPostFragmentDirections.actionGlobalNavigationHome(Model.instance.getCurrentUserModel().getFullName()));
                    } else {
                        Model.instance.getApprovedVolunteer(postId, (volunteer) -> {

                            String volunteerId = volunteer.get("_id").toString().replace("\"", "");
                            Navigation.findNavController(v).navigate(
                                    DetailsPostFragmentDirections.actionDetailsPostFragmentToFeedbackFragment(volunteerId));
                            refresh();
                        });
                    }
                });
            });
            builder.setNegativeButton("No", (dialog, id) -> dialog.cancel());
            AlertDialog alert11 = builder.create();
            alert11.show();
        });


        swipeRefresh = view.findViewById(R.id.comment_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> refresh());

        RecyclerView list = view.findViewById(R.id.postdetails_rv_comment);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyAdapter();
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

        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState); //NEED?


        refresh();
        return view;
    }

    private void refresh() {
        Model.instance.getPostComments(postId, commentsList -> {
//            if (commentsList.size() == 0)
//                swipeRefresh.setVisibility(View.GONE);
//
//            else {
            System.out.println("Comments" + commentsList.size());
            postViewModel.data = commentsList;
            adapter.notifyDataSetChanged();
//            }
        });
        swipeRefresh.setRefreshing(false);
    }

    public void displayPost(String title, String description, String time, String senderId, String status, String role, Bitmap[] bm, String address) {
        Model.instance.findUserById(senderId, user -> {
            titleEt.setText(title);
            descriptionEt.setText(description);
            timeEt.setText(time);
            authorEt.setText(user.get("name").toString().replace("\"", "")); //TODO: find user by ID
            statusEt.setText(status);
            roleEt.setText(role);

            if (address != null) // only if role == SOS
                addressEt.setText(address);

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    imageView.setImageBitmap(sampleImages[position]);
                }
            };
            if (bm != null) {
                carouselView.setImageListener(imageListener);
                carouselView.setPageCount(bm.length);
            } else
                carouselView.setVisibility(View.GONE);

            updateUI(View.VISIBLE);
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
//        postImg.setVisibility(type);
        sendCommentBtn.setVisibility(type);
        imgUser.setVisibility(type);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        if (isSOS == 1) {
            String str = location.substring(10, location.length() - 1);
            String[] latLong = str.split(",");
            Log.d("TAG", "latttt " + latLong[0] + " " + latLong[1]);
            LatLng sosLocation = new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1]));
            googleMap.addMarker(new MarkerOptions().position(sosLocation).title("here!"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sosLocation));
            float zoomLevel = 16.0f; //This goes up to 21
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sosLocation, zoomLevel));
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setScrollGesturesEnabled(true);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    //#### Comments ViewHolder ####

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, time, rate, correct;
        EditText content;
        Button delete, edit, editsave, positive, negative;
        ImageView userImage;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            username = itemView.findViewById(R.id.connectionListRow_userName_tv);
            time = itemView.findViewById(R.id.coomentListRow_time_tv);
            content = itemView.findViewById(R.id.coomentListRow_content_ev);

            delete = itemView.findViewById(R.id.coomentListRow_deleteBtn);
            edit = itemView.findViewById(R.id.coomentListRow_editBtn);
            editsave = itemView.findViewById(R.id.coomentListRow_saveEditBtn);
            positive = itemView.findViewById(R.id.coomentListRow_upBtn);
            negative = itemView.findViewById(R.id.coomentListRow_downBtn);
            rate = itemView.findViewById(R.id.coomentListRow_rateTv);
            correct = itemView.findViewById(R.id.coomentListRow_approvedTv);
            userImage = itemView.findViewById(R.id.commentListRow_userImg_img);

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

                Model.instance.editComment(map, id, () -> {
                    content.setEnabled(false);
                    edit.setVisibility(View.VISIBLE);
                    editsave.setVisibility(View.GONE);
                    delete.setVisibility(View.VISIBLE);
                    refresh();
                });

            });
            delete.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                Comment comment = postViewModel.getData().get(pos);

                String id = comment.getCommentId().toString();

                Model.instance.deleteComment(id, () -> {
                    refresh();
                });
            });
            positive.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                Comment comment = postViewModel.getData().get(pos);
                String id = comment.getCommentId().toString();

                HashMap<String, String> map = new HashMap<>();
                map.put("user_rate", Model.instance.getCurrentUserModel().getId());

                Model.instance.upComment(id, map, () -> {
                    positive.setVisibility(View.GONE);
                    negative.setVisibility(View.VISIBLE);
                    refresh();
                });
            });
            negative.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                Comment comment = postViewModel.getData().get(pos);
                String id = comment.getCommentId().toString();

                HashMap<String, String> map = new HashMap<>();
                map.put("user_rate", Model.instance.getCurrentUserModel().getId());

                Model.instance.downComment(id, map, () -> {
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
                    if (user.get("photo") != null) {
                        String photoBase64 = user.get("photo").getAsString();
                        byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                        decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        userImage.setImageBitmap(decodedByte);
                    }

                    /*  ## if login user is the same as the comment.sender user
                        ## hide the ability to rate the comment
                        ## show the ability to delete and edit */
                    if (user.get("email").toString().replace("\"", "").compareTo(Model.instance.getCurrentUserModel().getEmail()) == 0) {
                        delete.setVisibility(View.VISIBLE);
                        edit.setVisibility(View.VISIBLE);
                        positive.setVisibility(View.GONE);
                        negative.setVisibility(View.GONE);
                    } else {
                        delete.setVisibility(View.GONE);
                        edit.setVisibility(View.GONE);
                        positive.setVisibility(View.VISIBLE);
                        negative.setVisibility(View.VISIBLE);
                    }
                }
            });


            // # check if the login user already rated
            if (comment.IsUserRated_negative(Model.instance.getCurrentUserModel().getId())) {
                negative.setVisibility(View.GONE);
            } else if (comment.IsUserRated_positive(Model.instance.getCurrentUserModel().getId())) {
                positive.setVisibility(View.GONE);
            }

            //# check what is the rate of the comment - calc in model
            int rate_val = comment.getCommentRate();
            rate.setText(String.valueOf(rate_val));

            //# check if the comment IsCorrect
            if (comment.IsCorrect().compareTo("true") == 0) {
                correct.setVisibility(View.VISIBLE);
            } else {
                correct.setVisibility(View.GONE);
            }


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
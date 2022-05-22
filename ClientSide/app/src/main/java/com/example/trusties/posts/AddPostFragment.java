package com.example.trusties.posts;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trusties.CommonFunctions;
import com.example.trusties.R;
import com.example.trusties.model.Model;
import com.example.trusties.model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddPostFragment extends Fragment implements OnMapReadyCallback {

    EditText postTitle, description;
    TextView detailsTV;
    ImageView image, image2;
    ImageButton cameraBtn, galleryBtn, carBtn, deliveryBtn, toolsBtn, houseBtn;
    Button firstCircleBtn, secondCircleBtn, thirdCircleBtn, postBtn, sosBtn;
    ProgressBar progressBar;
    int flag = 0;
    int picFlag = 1;
    ConstraintLayout location_layout, circle_layout;
    MapView mapView;
    HashMap<String, String> map;
    GoogleMap mGoogleMap;
    String fullAddress;
    private GoogleApiClient googleApiClient;
    String address;

    private FusedLocationProviderClient fusedLocationProviderClientLocationClient;

    String category;

    Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;
    int PICK_IMAGE_MULTIPLE = 1;
    ArrayList<Uri> mArrayUri;
    LatLng locationOnMap;
    Geocoder geocoder;
    List<Address> addresses;

    Integer circle;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        mArrayUri = new ArrayList<Uri>();
        postTitle = view.findViewById(R.id.newpost_title_et);
        description = view.findViewById(R.id.newpost_description_et);
        image = view.findViewById(R.id.newpost_post_image);
        image2 = view.findViewById(R.id.newpost_post_image2);
        mapView = view.findViewById(R.id.post_edit_map);
        carBtn = view.findViewById(R.id.newpost_car_btn);
        toolsBtn = view.findViewById(R.id.newpost_tools_btn);
        deliveryBtn = view.findViewById(R.id.newpost_delivery_btn);
        houseBtn = view.findViewById(R.id.newpost_house_damage_btn);

        progressBar = view.findViewById(R.id.newpost_progressBar);
        progressBar.setVisibility(View.GONE);

        location_layout = view.findViewById(R.id.editpost_location_layout);
        location_layout.setVisibility(View.GONE);

        circle_layout = view.findViewById(R.id.newpost_circle_layout);
        circle_layout.setVisibility(View.GONE);

        detailsTV = view.findViewById(R.id.newpost_details_tv);
        detailsTV.setVisibility(View.GONE);

//        mapView.getMapAsync(this);
//        mapView.onCreate(savedInstanceState);

        carBtn.setOnClickListener(v -> {
            category = "Car";
            setColorsBtn(1, 0, 0, 0);
        });
        houseBtn.setOnClickListener(v -> {
            category = "House";
            setColorsBtn(0, 0, 0, 1);
        });
        deliveryBtn.setOnClickListener(v ->
        {
            category = "Delivery";
            setColorsBtn(0, 1, 0, 0);
        });
        toolsBtn.setOnClickListener(v -> {
            category = "Tools";
            setColorsBtn(0, 0, 1, 0);

        });


        cameraBtn = view.findViewById(R.id.newpost_camera_btn);
        galleryBtn = view.findViewById(R.id.newpost_gallery_btn);
        firstCircleBtn = view.findViewById(R.id.newpost_firstcircle_btn);
        secondCircleBtn = view.findViewById(R.id.newpost_secondcircle_btn);
        thirdCircleBtn = view.findViewById(R.id.newpost_thirdcircle_btn);
        postBtn = view.findViewById(R.id.newpost_post_btn);
        sosBtn = view.findViewById(R.id.newpost_sos_btn);

        cameraBtn.setOnClickListener(v -> OpenCamera());
        galleryBtn.setOnClickListener(v -> OpenGallery());
        firstCircleBtn.setOnClickListener(v -> FindFirstCircle());
        secondCircleBtn.setOnClickListener(v -> FindSecondCircle());
        thirdCircleBtn.setOnClickListener(v -> FindThirdCircle());

        postBtn.setOnClickListener(v -> PostQuestion(v));
        sosBtn.setOnClickListener(v -> postSOSCall(v));

        /*--------------------------categories---------------------------*/
        carBtn = view.findViewById(R.id.newpost_car_btn);
        deliveryBtn = view.findViewById(R.id.newpost_delivery_btn);
        toolsBtn = view.findViewById(R.id.newpost_tools_btn);
        houseBtn = view.findViewById(R.id.newpost_house_damage_btn);

        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState); //NEED?

        return view;
    }

    public void setColorsBtn(int flagCar, int flagDelivery, int flagTools, int flagHouse) {
        if (flagCar == 1)
            carBtn.setBackgroundTintList(carBtn.getContext().getResources().getColorStateList(R.color.btnClicked));
        if (flagCar == 0)
            carBtn.setBackgroundTintList(carBtn.getContext().getResources().getColorStateList(R.color.whiteColor));
        if (flagDelivery == 1)
            deliveryBtn.setBackgroundTintList(deliveryBtn.getContext().getResources().getColorStateList(R.color.btnClicked));
        if (flagDelivery == 0)
            deliveryBtn.setBackgroundTintList(deliveryBtn.getContext().getResources().getColorStateList(R.color.whiteColor));
        if (flagTools == 1)
            toolsBtn.setBackgroundTintList(toolsBtn.getContext().getResources().getColorStateList(R.color.btnClicked));
        if (flagTools == 0)
            toolsBtn.setBackgroundTintList(toolsBtn.getContext().getResources().getColorStateList(R.color.whiteColor));
        if (flagHouse == 1)
            houseBtn.setBackgroundTintList(houseBtn.getContext().getResources().getColorStateList(R.color.btnClicked));
        if (flagHouse == 0)
            houseBtn.setBackgroundTintList(houseBtn.getContext().getResources().getColorStateList(R.color.whiteColor));

    }

    private void OpenCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void OpenGallery() {
//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//        photoPickerIntent.setType("image/*");
//        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mArrayUri = new ArrayList<Uri>();
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                image2.setImageResource(R.drawable.image_place);
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                image.setImageBitmap(imageBitmap);

            }
        }
//         else if (requestCode == REQUEST_IMAGE_GALLERY) {
//        if (resultCode == RESULT_OK) {
//            try {
//                final Uri imageUri = data.getData();
//                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
//                imageBitmap = BitmapFactory.decodeStream(imageStream);
//                image.setImageBitmap(imageBitmap);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//    }
        else if (requestCode == REQUEST_IMAGE_GALLERY) {
            if (resultCode == RESULT_OK) {
                // Get the Image from data
                Log.d("TAG", 0 + "");
                if (data.getClipData() != null) { // multi pics
                    Log.d("TAG", 1 + "");
                    ClipData mClipData = data.getClipData();
                    int count = data.getClipData().getItemCount();
                    if (count > 2) {
                        Log.d("TAG", 2 + "");
                        mArrayUri = new ArrayList<Uri>();
                        picFlag = 0;
                        Toast.makeText(getContext(), "you can upload 1 or 2 photos only!", Toast.LENGTH_LONG).show();
                        postBtn.setEnabled(false);
                        sosBtn.setEnabled(false);
                        image.setImageResource(R.drawable.image_place);
                        image2.setImageResource(R.drawable.image_place);
                        mArrayUri = new ArrayList<Uri>();
                    } else {
                        for (int i = 0; i < count; i++) {
                            imageBitmap = null;
                            // adding imageuri in array
                            Uri imageurl = data.getClipData().getItemAt(i).getUri();
                            mArrayUri.add(imageurl);
                            postBtn.setEnabled(true);
                            sosBtn.setEnabled(true);
                        }
                        //setting 1st selected image into image switcher
                        image.setImageURI(mArrayUri.get(0));
                        if (count == 2)
                            image2.setImageURI(mArrayUri.get(1));
                    }
                } else if (data.getData() != null) { //only one pic
                    Log.d("TAG", 4 + "");
                    Uri imageurl = data.getData();
                    mArrayUri.add(imageurl);
                    image.setImageURI(mArrayUri.get(0));
                    postBtn.setEnabled(true);
                    sosBtn.setEnabled(true);
//                    position = 0;
                }
            }
        } else {
            Toast.makeText(getContext(), "Please try again!", Toast.LENGTH_LONG).show();

        }

    }

    private void FindFirstCircle() {

        circle = 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            firstCircleBtn.setBackgroundColor(firstCircleBtn.getContext().getColor(R.color.titleColor));
            secondCircleBtn.setBackgroundColor(secondCircleBtn.getContext().getColor(R.color.lightGray));
            thirdCircleBtn.setBackgroundColor(thirdCircleBtn.getContext().getColor(R.color.lightGray));
        }
    }

    private void FindSecondCircle() {
        FindFirstCircle();
        circle = 2;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            secondCircleBtn.setBackgroundColor(secondCircleBtn.getContext().getColor(R.color.titleColor));
            thirdCircleBtn.setBackgroundColor(thirdCircleBtn.getContext().getColor(R.color.lightGray));
        }
    }

    private void FindThirdCircle() {
        FindSecondCircle();
        circle = 3;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            thirdCircleBtn.setBackgroundColor(thirdCircleBtn.getContext().getColor(R.color.titleColor));
        }
    }

    private void PostQuestion(View view) {
        createPost(view, "QUESTION");
    }

    private void postSOSCall(View view) {

        detailsTV.setVisibility(View.VISIBLE);
        location_layout.setVisibility(View.VISIBLE);
        circle_layout.setVisibility(View.VISIBLE);
        postBtn.setEnabled(false);
        sosBtn.setOnClickListener(v -> {
            if (circle != null)
                createPost(v, "SOS");
            else {
                String msg = "You need to select a circle of friends to which you want to share your SOS call";
                new CommonFunctions().myPopup(getContext(), "Error", msg);
                postSOSCall(view);
            }
        });
    }

    private void createPost(View view, String type) {

        String currUserID = Model.instance.getCurrentUserModel().getId();

        if (circle == null) circle = 1;

        Model.instance.getFriendsList(currUserID, circle, friendsList -> {
            System.out.println(circle + "--> " + friendsList);
        });

        String title = postTitle.getText().toString();
        String message = description.getText().toString();
        User user = Model.instance.getCurrentUserModel();
        String email = user.getEmail().replace("\"", "");

        map = new HashMap<>();
        map.put("category", category);
        map.put("title", title);
        map.put("description", message);
        map.put("email", email);
        map.put("role", type);
        if (type.equals("SOS"))
            map.put("circle", circle.toString());

        ArrayList<String> photos = new ArrayList<>();

        if (imageBitmap != null) {
//            Log.d("TAG", imageBitmap.toString());
            Model.instance.encodeBitMapImg(imageBitmap, new Model.encodeBitMapImgListener() {
                @Override
                public void onComplete(String url) {
                    photos.add(url);
                    map.put("photo", url);
                }
            });

        }
        if (mArrayUri != null) {
            flag = 1;
            for (int i = 0; i < mArrayUri.size(); i++) {
                try {
                    Model.instance.encodeBitMapImg(BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(mArrayUri.get(i))), new Model.encodeBitMapImgListener() {
                        @Override
                        public void onComplete(String url) {
                            photos.add(url);
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            if (type.equals("SOS")) {
                map.put("location",locationOnMap.toString());
                map.put("address", fullAddress);

                Model.instance.addSos(map, new Model.addSosListener() {
                    @Override
                    public void onComplete(JsonObject res) {
                        Log.d("TAG","photossssss"+ photos.toString());
                        Log.d("TAG", "idddddd + " + res.get("_id").toString().replace("\"", ""));
                        Model.instance.addPhotosToPost(photos, res.get("_id").toString().replace("\"", ""), new Model.addPhotosToPostListener() {
                            @Override
                            public void onComplete() {
                                Log.d("TAG", "stopppppppppp");
                            }
                        });
                        Navigation.findNavController(view).navigate(AddPostFragmentDirections.actionGlobalNavigationHome(user.getFullName()));

                    }

                });
            } else {

                Model.instance.addPost(map, new Model.addPostListener() {
                    @Override
                    public void onComplete(JsonObject res) {
                        Log.d("TAG", "idddddd + " + res.get("_id").toString().replace("\"", ""));
                        Model.instance.addPhotosToPost(photos, res.get("_id").toString().replace("\"", ""), new Model.addPhotosToPostListener() {
                            @Override
                            public void onComplete() {
                                Log.d("TAG", "stopppppppppp");
                            }
                        });


                        Navigation.findNavController(view).navigate(AddPostFragmentDirections.actionGlobalNavigationHome(user.getFullName()));
                    }
                });
            }

        }


//        Model.instance.addPost(map, () ->        Navigation.findNavController(view).navigate(AddPostFragmentDirections.actionGlobalNavigationHome(user.getFullName())));

//        /* ------ Add Notification ------ */
//        HashMap<String, String> notification = new HashMap<>();
//        notification.put("sender", user.getEmail());
//        notification.put("post", postId);
//        notification.put("time", (new Long(0)).toString());
//        notification.put("type", "post");
//
//        Model.instance.addNotification(notification, () -> {
//
//        });
//        String token = Model.getToken();
//        Model.instance.sendNotification(notification, token, () -> {
//        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        System.out.println("------------------1------------------");

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }
        System.out.println("------------------2------------------");

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                System.out.println("------------------3------------------");
                // GPS location can be null if GPS is switched off
                if (location != null) {
//                    Toast.makeText(getContext(), "lat " + location.getLatitude() + "\nlong " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    System.out.println("lat " + location.getLatitude() + "\nlong " + location.getLongitude());
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
//                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    float zoomLevel = 16.0f; //This goes up to 21
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel));
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                    googleMap.getUiSettings().setCompassEnabled(true);
                    googleMap.getUiSettings().setScrollGesturesEnabled(true);
                    googleMap.setMyLocationEnabled(true);
                    locationOnMap = myLocation;
                    geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        addresses = geocoder.getFromLocation(myLocation.latitude, myLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        fullAddress = address;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng latLng) {
                            Log.d("TAG",latLng.toString());
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions().position(latLng));
                            geocoder = new Geocoder(getContext(), Locale.getDefault());
                            try {
                                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                fullAddress = address;
                                Log.d("TAG",fullAddress);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            locationOnMap = latLng;

                        }
                    });

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });


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

}
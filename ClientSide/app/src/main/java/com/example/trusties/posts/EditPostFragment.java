package com.example.trusties.posts;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trusties.R;
import com.example.trusties.model.Model;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class EditPostFragment extends Fragment implements OnMapReadyCallback {
    TextView titleEt;
    TextView descriptionEt;
    TextView titleTv;
    TextView descriptionTv;
    TextView tagsTv;
    TextView picturesTv;
    ProgressBar progressBar;
    ImageView image, image2;

    Spinner dropdown;
    Button saveBtn;
    Button cancelBtn;
    ImageButton cameraBtn;
    ImageButton galleryBtn;
    String postId;

    Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;
    ArrayList<Uri> mArrayUri;
    int flag = 0;
    int picFlag = 1;

    MapView mapView;
    HashMap<String, String> map;
    GoogleMap mGoogleMap;
    String fullAddress;
    LatLng locationOnMap;
    Geocoder geocoder;
    List<Address> addresses;
    private FusedLocationProviderClient fusedLocationProviderClientLocationClient;
    String address;
    String locationSOS = null;
    int isSOS =0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);

        progressBar = view.findViewById(R.id.editpost_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        titleEt = view.findViewById(R.id.editpost_title_show);
        descriptionEt = view.findViewById(R.id.editpost_description_show);
        saveBtn = view.findViewById(R.id.editpost_save_btn);
        cancelBtn = view.findViewById(R.id.editpost_cancel_btn);
        cameraBtn = view.findViewById(R.id.editpost_camera_btn);
        galleryBtn = view.findViewById(R.id.editpost_gallery_btn);
        mapView = view.findViewById(R.id.post_edit_map);
        titleTv = view.findViewById(R.id.editpost_title_tv);
        descriptionTv = view.findViewById(R.id.editpost_description_tv);
        picturesTv = view.findViewById(R.id.editpost_pictures_tv);
        image = view.findViewById(R.id.editpost_image_show);
        image2 = view.findViewById(R.id.editpost_image2_show);
        updateUI(View.INVISIBLE);

        postId = DetailsPostFragmentArgs.fromBundle(getArguments()).getPostId();
        //get post by ID
        Model.instance.getPostById(postId, new Model.getPostByIdListener() {
            @Override
            public void onComplete(JsonObject post) {

                String title = post.get("title").toString().replace("\"", "");
                String description = post.get("description").toString().replace("\"", "");
                displayPost(title, description); //TODO: add tags, image etc..
                progressBar.setVisibility(View.GONE);
                updateUI(View.VISIBLE);
                mapView.setVisibility(View.GONE);
                Log.d("TAG",post.get("role").toString());
                if (post.get("role").toString().replace("\"", "").equals("SOS")) {
                    mapView.setVisibility(View.VISIBLE);
                    Log.d("TAG","yesyesyes");
                    isSOS = 1;
                    address = post.get("address").toString().replace("\"", "");
                    locationSOS = post.get("location").toString().replace("\"", "");


                }
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Model.instance.getPostById(postId, new Model.getPostByIdListener() {
                    @Override
                    public void onComplete(JsonObject post) {

                        savePost(view, titleEt.getText().toString(), descriptionEt.getText().toString(), postId,isSOS);

                    }

                });
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });

        cameraBtn.setOnClickListener(v -> OpenCamera());
        galleryBtn.setOnClickListener(v -> OpenGallery());

        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState); //NEED?

        return view;
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        /*BEFORE 2 PICS*/
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IMAGE_CAPTURE) {
//            if (resultCode == RESULT_OK) {
//                Bundle extras = data.getExtras();
//                imageBitmap = (Bitmap) extras.get("data");
//                image.setImageBitmap(imageBitmap);
//
//            }
//        } else if (requestCode == REQUEST_IMAGE_GALLERY) {
//            if (resultCode == RESULT_OK) {
//                try {
//                    final Uri imageUri = data.getData();
//                    final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
//                    imageBitmap = BitmapFactory.decodeStream(imageStream);
//                    image.setImageBitmap(imageBitmap);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        super.onActivityResult(requestCode, resultCode, data);
        mArrayUri = new ArrayList<Uri>();
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                image2.setImageResource(R.drawable.image_place);
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                image.setImageBitmap(imageBitmap);

                Uri tempUri = getImageUri(getContext(), imageBitmap);
                Log.d("TAG", "IMAGE URI" + tempUri);
                mArrayUri.add(tempUri);


            }
        } else if (requestCode == REQUEST_IMAGE_GALLERY) {
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
                        saveBtn.setEnabled(false);
                        image.setImageResource(R.drawable.image_place);
                        image2.setImageResource(R.drawable.image_place);
                        mArrayUri = new ArrayList<Uri>();
                    } else {
                        for (int i = 0; i < count; i++) {
                            imageBitmap = null;
                            // adding imageuri in array
                            Uri imageurl = data.getClipData().getItemAt(i).getUri();
                            mArrayUri.add(imageurl);
                            Log.d("TAG", "IMAGE URI" + imageurl);
                            saveBtn.setEnabled(true);
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
                    Log.d("TAG", "IMAGE URI" + data.getData());
                    saveBtn.setEnabled(true);

                }
            }
        } else {
            Toast.makeText(getContext(), "Please try again!", Toast.LENGTH_LONG).show();

        }

    }

    private void savePost(View view, String title, String description, String postId, int isSOS) {
        HashMap<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("description", description);
        if(isSOS ==1)
        {
            map.put("location", locationOnMap.toString());
            map.put("address", fullAddress);
        }
//        if (imageBitmap != null) {
//            Log.d("TAG", imageBitmap.toString());
//            Model.instance.encodeBitMapImg(imageBitmap, new Model.encodeBitMapImgListener() {
//                @Override
//                public void onComplete(String url) {
//                    map.put("photo", url);
//                }
//            });
//
//        }
        ArrayList<String> photos = new ArrayList<>();

        if (imageBitmap != null) {
            Log.d("TAG", imageBitmap.toString());
            Model.instance.encodeBitMapImg(imageBitmap, new Model.encodeBitMapImgListener() {
                @Override
                public void onComplete(String url) {
                    map.put("photo", url);
                }
            });

        }
        if (mArrayUri != null) {
            Log.d("TAG", map.size() + "   jhhk ");

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
        }
        Model.instance.editPost(map, postId, new Model.editPostListener() {
            @Override
            public void onComplete(JsonObject res) {
                Model.instance.addPhotosToPost(photos, res.get("_id").toString().replace("\"", ""), new Model.addPhotosToPostListener() {
                    @Override
                    public void onComplete() {
                        Log.d("TAG", "stopppppppppp");
                    }
                });
                Navigation.findNavController(view).navigateUp();
            }
        });

    }

    private void displayPost(String title, String description) {

        titleEt.setText(title);
        titleEt.setTextSize(20);
        descriptionEt.setText(description);
        descriptionEt.setTextSize(20);
    }

    public void updateUI(int type) {
        titleEt.setVisibility(type);
        descriptionEt.setVisibility(type);
        saveBtn.setVisibility(type);
        cancelBtn.setVisibility(type);
        cameraBtn.setVisibility(type);
        galleryBtn.setVisibility(type);
        titleTv.setVisibility(type);
        descriptionTv.setVisibility(type);
        picturesTv.setVisibility(type);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        if(isSOS == 1) {

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                return;
            }
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onSuccess(Location location) {
                    // GPS location can be null if GPS is switched off
                    if (location != null) {
                        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        String str = locationSOS.substring(10, locationSOS.length() - 1);

                        String[] latLong = str.split(",");
                        LatLng sosLocation = new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1]));
                        googleMap.addMarker(new MarkerOptions().position(sosLocation).title("here!"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sosLocation));
                        float zoomLevel = 16.0f; //This goes up to 21
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sosLocation, zoomLevel));

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
                                Log.d("TAG", latLng.toString());
                                googleMap.clear();
                                googleMap.addMarker(new MarkerOptions().position(latLng));
                                geocoder = new Geocoder(getContext(), Locale.getDefault());
                                try {
                                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                                    address = addresses.get(0).getAddressLine(0);
                                    fullAddress = address;
                                    Log.d("TAG", fullAddress);
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
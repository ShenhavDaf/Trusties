package com.example.trusties.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
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
import android.widget.TextView;

import com.example.trusties.R;
import com.example.trusties.model.Model;
import com.example.trusties.model.User;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;


public class EditProfileFragment extends Fragment {

    TextView nameEt;
    TextView phoneEt;
    Button saveBtn;
    String userId;

    ImageButton cameraBtn;
    ImageButton galleryBtn;
    ImageView image;
    Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_edit_profile, container, false);
        userId = EditProfileFragmentArgs.fromBundle(getArguments()).getUserId();

        nameEt = view.findViewById(R.id.editProfile_name_tv);
        phoneEt = view.findViewById(R.id.editProfile_phone_tv);
        saveBtn = view.findViewById(R.id.editProfile_save_btn);
        cameraBtn = view.findViewById(R.id.editProfile_camera_btn);
        galleryBtn = view.findViewById(R.id.editProfile_gallery_btn);
        image = view.findViewById(R.id.editProfile_image);

        User user = Model.instance.getCurrentUserModel();
        nameEt.setText(user.getFullName());
        phoneEt.setText(user.getPhone());
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(v);
            }
        });

        cameraBtn.setOnClickListener(v -> OpenCamera());
        galleryBtn.setOnClickListener(v -> OpenGallery());

        return view;
    }


    private void OpenCamera() {
        //TODO
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void OpenGallery() {
        //TODO
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                image.setImageBitmap(imageBitmap);

            }
        } else if (requestCode == REQUEST_IMAGE_GALLERY) {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                    imageBitmap = BitmapFactory.decodeStream(imageStream);
                    image.setImageBitmap(imageBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void save(View view){
        HashMap<String,String> map = new HashMap<>();
        map.put("name",nameEt.getText().toString());
        map.put("phone",phoneEt.getText().toString());
        if (imageBitmap != null) {
            Log.d("TAG", imageBitmap.toString());
            Model.instance.encodeBitMapImg(imageBitmap, new Model.encodeBitMapImgListener() {
                @Override
                public void onComplete(String url) {
                    map.put("photo", url);
                }
            });

        }

        Model.instance.editUser(map, userId, new Model.editUserListener() {
            @Override
            public void onComplete() {
                Navigation.findNavController(view).navigateUp();
            }
        });

    }
}
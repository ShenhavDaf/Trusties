package com.example.trusties.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trusties.CommonFunctions;
import com.example.trusties.R;
import com.example.trusties.model.Model;
import com.example.trusties.model.User;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Pattern;


public class EditProfileFragment extends Fragment {

    TextView nameEt;
    TextView phoneEt;
    Button saveBtn, cancelBtn, changePasswordBtn;
    String userId;

    ImageButton cameraBtn;
    ImageButton galleryBtn;
    ImageView image;
    Bitmap imageBitmap;
    Bitmap decodedByte;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;
    User user;
    String photoBase64;

    TextView currPassword, newPassword, confirmPassword;
    int flagPassword = 0;
    TextView currTv, newTv, confirmTv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        userId = EditProfileFragmentArgs.fromBundle(getArguments()).getUserId();

        nameEt = view.findViewById(R.id.editProfile_name_tv);
        phoneEt = view.findViewById(R.id.editProfile_phone_tv);
        saveBtn = view.findViewById(R.id.editProfile_save_btn);
        cameraBtn = view.findViewById(R.id.editProfile_camera_btn);
        galleryBtn = view.findViewById(R.id.editProfile_gallery_btn);
        image = view.findViewById(R.id.editProfile_image);
        currPassword = view.findViewById(R.id.edit_profile_curr_password);
        newPassword = view.findViewById(R.id.edit_profile_new_password);
        confirmPassword = view.findViewById(R.id.edit_profile_confirm_password);
        changePasswordBtn = view.findViewById(R.id.edit_profile_change_password_btn);
        cancelBtn = view.findViewById(R.id.editProfile_cancel_btn);
        currTv = view.findViewById(R.id.edit_profile_curr_tv);
        newTv = view.findViewById(R.id.edit_profile_new_tv);
        confirmTv = view.findViewById(R.id.edit_profile_confirm_tv);
        setVisibility(View.GONE);

        Model.instance.findUserById(Model.instance.getCurrentUserModel().getId(), new Model.findUserByIdListener() {
            @Override
            public void onComplete(JsonObject user) {

                if (user.get("photo") != null) {
                    photoBase64 = user.get("photo").getAsString();
                    byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    image.setImageBitmap(decodedByte);
                }

            }
        });


        user = Model.instance.getCurrentUserModel();
        nameEt.setText(user.getFullName());
        phoneEt.setText(user.getPhone());
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(v);
            }
        });
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagPassword = 1;
                setVisibility(View.VISIBLE);

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagPassword = 0;
                setVisibility(View.GONE);

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

    public void save(View view) {
        int inputOk = 1;
        HashMap<String, String> map = new HashMap<>();
        if (flagPassword == 1) {

            if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                inputOk = 0;
                String msg = "New password and confirm password does not match!";
                new CommonFunctions().myPopup(getContext(), "Error", msg);
            } else {
                if (newPassword.getText().toString().length() < 6 || confirmPassword.getText().toString().length() < 6) {
                    inputOk = 0;
                    String msg = "Password must be at least 6 digits!";
                    new CommonFunctions().myPopup(getContext(), "Error", msg);
                } else {

                    map.put("currPassword", currPassword.getText().toString());
                    map.put("newPassword", newPassword.getText().toString());
                }
            }
        }
        int isNameAndPhoneOk = CheckNameAndPhone(nameEt.getText().toString(), phoneEt.getText().toString());
        if (isNameAndPhoneOk == 1) {

            map.put("name", nameEt.getText().toString());
            map.put("phone", phoneEt.getText().toString());
            map.put("flag", flagPassword + "");


            if (imageBitmap != null) {
                Log.d("TAG", imageBitmap.toString());
                Model.instance.encodeBitMapImg(imageBitmap, new Model.encodeBitMapImgListener() {
                    @Override
                    public void onComplete(String url) {
                        map.put("photo", url);
                    }
                });

            }
            else{
                map.put("photo",photoBase64);
            }
            Context context = getContext();
            if (inputOk == 1) {
                Model.instance.editUser(map, userId, context, new Model.editUserListener() {
                    @Override
                    public void onComplete() {

                        User newUsr = new User(userId, map.get("name"), user.getEmail(), map.get("phone"));
                        Model.instance.setCurrentUserModel(newUsr);
                        Navigation.findNavController(view).navigateUp();
                    }
                });
            }
        }

    }

    public void setVisibility(int isVisible) {
        cancelBtn.setVisibility(isVisible);
        currPassword.setVisibility(isVisible);
        newPassword.setVisibility(isVisible);
        confirmPassword.setVisibility(isVisible);
        confirmTv.setVisibility(isVisible);
        newTv.setVisibility(isVisible);
        currTv.setVisibility(isVisible);


    }

    public int CheckNameAndPhone(String name, String phone) {
        if (name.equals("") || phone.equals("")) {
            String msg = "You need to add name/phone";
            new CommonFunctions().myPopup(getContext(), "Error", msg);
            return 0;
        } else   if ((!Patterns.PHONE.matcher(phone).matches()) || (!Pattern.matches("(050|052|054|057)[0-9]{7}", phone)|| phone.length() != 10)) {
            String msg = "Phone is not valid";
            new CommonFunctions().myPopup(getContext(), "Error", msg);
            return 0;
        }else
            return 1;

    }

}
package com.example.trusties.login;

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
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.trusties.R;
import com.example.trusties.model.Model;
import com.example.trusties.model.User;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpFragment extends Fragment {

    EditText fullName, email, password, verify, phone;
    ImageView userImg;
    ImageButton camera, gallery;
    Button joinBtn;
    ProgressBar progressBar;

    Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        fullName = view.findViewById(R.id.signup_name_et);
        email = view.findViewById(R.id.signup_email_et);
        password = view.findViewById(R.id.signup_password_et);
        verify = view.findViewById(R.id.signup_verify_et);
        phone = view.findViewById(R.id.signup_phone_et);
        userImg = view.findViewById(R.id.signup_user_image);

        progressBar = view.findViewById(R.id.signup_progressBar);
        progressBar.setVisibility(View.GONE);

        camera = view.findViewById(R.id.signup_camera_btn);
        gallery = view.findViewById(R.id.signup_gallery_btn);
        joinBtn = view.findViewById(R.id.signup_join_btn);

        camera.setOnClickListener(v -> OpenCamera());
        gallery.setOnClickListener(v -> OpenGallery());
        joinBtn.setOnClickListener(v -> Join(v));


        return view;
    }

    private void Join(View v) {

        String localName = fullName.getText().toString();
        String localEmail = email.getText().toString();
        String localPassword = password.getText().toString();
        String localVerify = verify.getText().toString();
        String localPhone = phone.getText().toString();

        if (localName.isEmpty()) {
            fullName.setError("Please enter your full name");
            fullName.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(localEmail).matches()) {
            email.setError("Please provide valid email");
            email.requestFocus();
            return;
        }
        if (localEmail.isEmpty()) {
            email.setError("Please enter your Email");
            email.requestFocus();
            return;
        }

        if (localPassword.length() < 6) {
            password.setError("Password length should be at least 6 characters");
            password.requestFocus();
            return;
        }

        if (!localVerify.equals(localPassword)) {
            verify.setError("Wrong password");
            verify.requestFocus();
            return;
        }

        if (!Patterns.PHONE.matcher(localPhone).matches()) {
            phone.setError("Please provide valid phone number");
            phone.requestFocus();
            return;
        }
        if (localPhone.isEmpty()) {
            phone.setError("Please enter your phone number");
            phone.requestFocus();
            return;
        }

//        progressBar.setVisibility(View.VISIBLE);
//        joinBtn.setEnabled(false);

        HashMap<String, String> map = new HashMap<>();
        map.put("name", localName);
        map.put("email", localEmail);
        map.put("password", localPassword);
        map.put("phone", localPhone);
        map.put("fragment", "SignUpFragment");

        if (imageBitmap != null) {
            Log.d("TAG", imageBitmap.toString());
            Model.instance.encodeBitMapImg(imageBitmap, new Model.encodeBitMapImgListener() {
                @Override
                public void onComplete(String url) {
                    map.put("photo", url);
                }
            });

        }

        Model.instance.signup(map, randomCodeFromServer -> {
            setConnectedUser(localEmail);
            Navigation.findNavController(v).navigate(
                    SignUpFragmentDirections.actionSignUpFragmentToVerificationFragment(localName, localEmail, randomCodeFromServer));

        }, getContext());
    }

    private void OpenCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void OpenGallery() {
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
                userImg.setImageBitmap(imageBitmap);

            }
        } else if (requestCode == REQUEST_IMAGE_GALLERY) {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                    imageBitmap = BitmapFactory.decodeStream(imageStream);
                    userImg.setImageBitmap(imageBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    void setConnectedUser(String localEmail) {
        Model.instance.findUserByEmail(localEmail, new Model.findUserByEmailListener() {
            @Override
            public void onComplete(JsonObject user) {
                Model.instance.setCurrentUserModel(new User(user.get("_id").toString().replace("\"", ""),user.get("name").toString().replace("\"", ""), user.get("email").toString().replace("\"", ""), user.get("phone").toString().replace("\"", "")));
            }
        });

    }

}
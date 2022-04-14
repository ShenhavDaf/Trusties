package com.example.trusties.ui.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.trusties.R;
import com.example.trusties.model.Model;
import com.example.trusties.model.User;

import java.util.HashMap;


public class EditProfileFragment extends Fragment {

    TextView nameEt;
    TextView phoneEt;
    Button saveBtn;
    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_edit_profile, container, false);
        userId = EditProfileFragmentArgs.fromBundle(getArguments()).getUserId();

        nameEt = view.findViewById(R.id.editProfile_name_tv);
        phoneEt = view.findViewById(R.id.editProfile_phone_tv);
        saveBtn = view.findViewById(R.id.editProfile_save_btn);

        User user = Model.instance.getCurrentUserModel();
        nameEt.setText(user.getFullName());
        phoneEt.setText(user.getPhone());
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(v);
            }
        });
        return view;
    }

    public void save(View view){
        HashMap<String,String> map = new HashMap<>();
        map.put("name",nameEt.getText().toString());
        map.put("phone",phoneEt.getText().toString());

        Model.instance.editUser(map, userId, new Model.editUserListener() {
            @Override
            public void onComplete() {
                Navigation.findNavController(view).navigateUp();
            }
        });

    }
}
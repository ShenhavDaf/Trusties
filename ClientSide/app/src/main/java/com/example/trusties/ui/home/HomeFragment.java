package com.example.trusties.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.trusties.MainActivity;
import com.example.trusties.R;
import com.example.trusties.User;
import com.example.trusties.databinding.FragmentHomeBinding;
import com.example.trusties.model.Model;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    String usersEmail;
    public static User connectedUser;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /***********************************/
        usersEmail = MainActivity.usersEmail;
        TextView userName = root.findViewById(R.id.home_userName_tv);
        Model.instance.findUserByEmail(usersEmail, new Model.findUserByEmailListener() {
            @Override
            public void onComplete(JsonObject user) {
               connectedUser = new User(user.get("name").toString(),user.get("email").toString(),user.get("phone").toString());
               userName.setText(connectedUser.getFullName().replace("\"", ""));
            }
        });
        /************************************/


        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
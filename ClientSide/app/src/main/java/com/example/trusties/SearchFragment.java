package com.example.trusties;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.trusties.model.Model;

public class SearchFragment extends Fragment {

    TextView categoriesBtn, statusBtn, authorBtn, descriptionBtn;
    Button findFriendBtn, searchBtn;
    ConstraintLayout categoriesLayout, statusLayout;
    EditText authorET, descriptionET, newFriendET;
    CheckBox carCB, deliveryCB, toolsCB, houseCB, openCB, waitingCB, closeCB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        categoriesBtn = view.findViewById(R.id.search_categories_tv);
        categoriesLayout = view.findViewById(R.id.search_categories_layout);
        carCB = view.findViewById(R.id.search_car_cb);
        deliveryCB = view.findViewById(R.id.search_delivery_cb);
        toolsCB = view.findViewById(R.id.search_tools_cb);
        houseCB = view.findViewById(R.id.search_house_cb);


        statusBtn = view.findViewById(R.id.search_status_tv);
        statusLayout = view.findViewById(R.id.search_status_layout);
        openCB = view.findViewById(R.id.search_open_cb);
        waitingCB = view.findViewById(R.id.search_waiting_cb);
        closeCB = view.findViewById(R.id.search_close_cb);


        authorBtn = view.findViewById(R.id.search_author_tv);
        authorET = view.findViewById(R.id.search_author_et);


        descriptionBtn = view.findViewById(R.id.search_description_tv);
        descriptionET = view.findViewById(R.id.search_description_et);


        findFriendBtn = view.findViewById(R.id.search_friend_btn);
        newFriendET = view.findViewById(R.id.search_friend_et);

        searchBtn = view.findViewById(R.id.search_btn);

        /*------------------------------------------------------------------*/
        categoriesBtn.setOnClickListener(v -> {
            switchVisibility(categoriesLayout);
        });

        statusBtn.setOnClickListener(v -> {
            switchVisibility(statusLayout);
        });

        authorBtn.setOnClickListener(v -> {
            switchVisibility(authorET);
        });

        descriptionBtn.setOnClickListener(v -> {
            switchVisibility(descriptionET);
        });

        findFriendBtn.setOnClickListener(v -> {
            switchVisibility(newFriendET);
        });

        searchBtn.setOnClickListener(v -> {
            String userID = Model.instance.getCurrentUserModel().getId();
            Navigation.findNavController(v).navigate(SearchFragmentDirections.actionGlobalNavigationHome(userID));
        });

        return view;
    }

    private void switchVisibility(View object) {
        if (object.getVisibility() == View.GONE)
            object.setVisibility(View.VISIBLE);
        else object.setVisibility(View.GONE);
    }
}
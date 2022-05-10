package com.example.trusties.posts.sos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trusties.R;
import com.example.trusties.databinding.FragmentDetailsPostBinding;
import com.example.trusties.posts.DetailsPostFragment;
import com.example.trusties.posts.DetailsPostViewModel;


public class RequestsFragment extends Fragment {

    private DetailsPostViewModel postViewModel;
    private FragmentDetailsPostBinding binding;

    //TODO:CHANGE IT
    //DetailsPostFragment.MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;



    public RequestsFragment() {
        // Required empty public constructor
    }


    public static RequestsFragment newInstance(String param1, String param2) {
        RequestsFragment fragment = new RequestsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }
}
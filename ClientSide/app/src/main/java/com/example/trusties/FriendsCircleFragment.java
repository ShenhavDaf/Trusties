package com.example.trusties;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;


public class FriendsCircleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_circle, container, false);

        SlidingUpPanelLayout layout1 = view.findViewById(R.id.slidingUp1);
        SlidingUpPanelLayout layout2 = view.findViewById(R.id.slidingUp2);
        SlidingUpPanelLayout layout3 = view.findViewById(R.id.slidingUp3);

        layout1.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                view.findViewById(R.id.from1).setAlpha(1-slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {
                if(newState == SlidingUpPanelLayout.PanelState.EXPANDED){
                    new CommonFunctions().myPopup(getContext(), "test", "Panel expanded1");

                }
                else if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    new CommonFunctions().myPopup(getContext(), "test", "Panel collapsed1");
                }
            }
        });

        layout2.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                view.findViewById(R.id.from2).setAlpha(1-slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {
                if(newState == SlidingUpPanelLayout.PanelState.EXPANDED){
                    new CommonFunctions().myPopup(getContext(), "test", "Panel expanded1");

                }
                else if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    new CommonFunctions().myPopup(getContext(), "test", "Panel collapsed1");
                }
            }
        });

        layout3.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                view.findViewById(R.id.from3).setAlpha(1-slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {
                if(newState == SlidingUpPanelLayout.PanelState.EXPANDED){
                    new CommonFunctions().myPopup(getContext(), "test", "Panel expanded1");

                }
                else if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    new CommonFunctions().myPopup(getContext(), "test", "Panel collapsed1");
                }
            }
        });

        return view;
    }
}

package com.example.trusties;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.example.trusties.login.LoginActivity;
import com.example.trusties.model.Model;


public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Model.instance.executor.execute(() -> {
            try {
                Log.d("TAG", "INNN");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            Model.instance.isSignedIn(new Model.isSignedInListener() {
//                @Override
//                public void onComplete(int flag) {
//                    if (flag == 1) {
//                        Log.d("TAG", "logged in");
//                        Model.instance.mainThread.post(() -> {
//                            toFeedActivity();
//                        });
//
//                    } else {
//                        Model.instance.mainThread.post(() -> {
//                            toLoginActivity();
//                        });
//                    }
//                }
//            });
            Model.instance.mainThread.post(() -> {
                toLoginActivity();
            });

        });
    }

    public void toLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void toFeedActivity() {
        Log.d("TAG", "inside to feed");
//        Navigation.findNavController(getWindow().getDecorView().findViewById(android.R.id.content)).navigate(R.id.action_global_navigation_home);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}


package com.example.trusties;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.trusties.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    AppBarConfiguration appBarConfiguration;
    public static String usersEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*********************/
//        Intent myIntent = getIntent(); // gets the previously created intent
//        usersEmail = myIntent.getStringExtra("email");
//        Model.instance.findUserByEmail(usersEmail, new Model.findUserByEmailListener() {
//            @Override
//            public void onComplete(JsonObject user) {
//                Log.d("TAG",user.toString());
//                Model.instance.setCurrentUserModel(new User(user.get("name").toString(), user.get("email").toString(), user.get("phone").toString()));
//            }
//        });
        /*****************/

//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ActionBarColor)));
        /*HIDES THE TOP ACTION BAR*/
        getSupportActionBar().hide();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

//        binding.navView.setOnItemSelectedListener(item -> {
//            navController.navigate(item.getItemId());
//            return true;
//        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
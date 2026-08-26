package com.projectconnect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.projectconnect.R;
import com.projectconnect.util.SharedPreferencesHelper;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin, btnSignup;
    private SharedPreferencesHelper sharedPrefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sharedPrefsHelper = new SharedPreferencesHelper(this);

        // Check if user is already logged in
        if (sharedPrefsHelper.isLoggedIn()) {
            String userRole = sharedPrefsHelper.getUserRole();
            if ("CUSTOMER".equals(userRole)) {
                startActivity(new Intent(this, CustomerHomeActivity.class));
            } else if ("SELLER".equals(userRole)) {
                startActivity(new Intent(this, SellerDashboardActivity.class));
            }
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }
}
package com.projectconnect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.projectconnect.R;
import com.projectconnect.model.LoginRequest;
import com.projectconnect.model.LoginResponse;
import com.projectconnect.network.ApiClient;
import com.projectconnect.network.ApiService;
import com.projectconnect.util.SharedPreferencesHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnBackToMain;
    private ApiService apiService;
    private SharedPreferencesHelper sharedPrefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initServices();
        setupClickListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnBackToMain = findViewById(R.id.btnBackToMain);
    }

    private void initServices() {
        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPrefsHelper = new SharedPreferencesHelper(this);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        btnBackToMain.setOnClickListener(v -> finish());
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(username, password);
        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    
                    // Save user data
                    sharedPrefsHelper.saveUserData(
                        loginResponse.getAccessToken(),
                        loginResponse.getId(),
                        loginResponse.getUsername(),
                        loginResponse.getEmail(),
                        loginResponse.getRole()
                    );

                    // Navigate to appropriate activity based on role
                    Intent intent;
                    if ("CUSTOMER".equals(loginResponse.getRole())) {
                        intent = new Intent(LoginActivity.this, CustomerHomeActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, SellerDashboardActivity.class);
                    }
                    
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Login failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
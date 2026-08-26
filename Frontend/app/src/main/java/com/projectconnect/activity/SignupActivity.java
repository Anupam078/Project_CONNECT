package com.projectconnect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.projectconnect.R;
import com.projectconnect.model.MessageResponse;
import com.projectconnect.model.SignupRequest;
import com.projectconnect.network.ApiClient;
import com.projectconnect.network.ApiService;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etShopName, etAddress, etLatitude, etLongitude;
    private Button btnSignup, btnBackToMain;
    private RadioGroup rgRole;
    private LinearLayout llSellerFields;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        initServices();
        setupClickListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etShopName = findViewById(R.id.etShopName);
        etAddress = findViewById(R.id.etAddress);
        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        btnSignup = findViewById(R.id.btnSignup);
        btnBackToMain = findViewById(R.id.btnBackToMain);
        rgRole = findViewById(R.id.rgRole);
        llSellerFields = findViewById(R.id.llSellerFields);
    }

    private void initServices() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void setupClickListeners() {
        btnSignup.setOnClickListener(v -> performSignup());
        btnBackToMain.setOnClickListener(v -> finish());

        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbSeller) {
                llSellerFields.setVisibility(View.VISIBLE);
            } else {
                llSellerFields.setVisibility(View.GONE);
            }
        });
    }

    private void performSignup() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = rgRole.getCheckedRadioButtonId() == R.id.rbSeller ? "SELLER" : "CUSTOMER";
        
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(username);
        signupRequest.setEmail(email);
        signupRequest.setPassword(password);
        signupRequest.setRole(role);

        if ("SELLER".equals(role)) {
            String shopName = etShopName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String latStr = etLatitude.getText().toString().trim();
            String lngStr = etLongitude.getText().toString().trim();

            if (shopName.isEmpty() || address.isEmpty() || latStr.isEmpty() || lngStr.isEmpty()) {
                Toast.makeText(this, "Please fill all seller fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                signupRequest.setShopName(shopName);
                signupRequest.setAddress(address);
                signupRequest.setLatitude(new BigDecimal(latStr));
                signupRequest.setLongitude(new BigDecimal(lngStr));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Call<MessageResponse> call = apiService.signup(signupRequest);

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "Registration successful! Please login.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Registration failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
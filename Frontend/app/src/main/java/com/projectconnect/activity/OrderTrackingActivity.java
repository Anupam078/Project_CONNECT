package com.projectconnect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.projectconnect.R;
import com.projectconnect.adapter.OrderAdapter;
import com.projectconnect.model.Order;
import com.projectconnect.network.ApiClient;
import com.projectconnect.network.ApiService;
import com.projectconnect.util.SharedPreferencesHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderTrackingActivity extends AppCompatActivity implements OrderAdapter.OnOrderClickListener {

    private RecyclerView rvOrders;
    private OrderAdapter orderAdapter;
    private ApiService apiService;
    private SharedPreferencesHelper sharedPrefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Orders");

        initViews();
        initServices();
        setupRecyclerView();
        loadOrders();
    }

    private void initViews() {
        rvOrders = findViewById(R.id.rvOrders);
    }

    private void initServices() {
        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPrefsHelper = new SharedPreferencesHelper(this);
    }

    private void setupRecyclerView() {
        orderAdapter = new OrderAdapter(this, this);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(orderAdapter);
    }

    private void loadOrders() {
        String token = "Bearer " + sharedPrefsHelper.getToken();
        Call<List<Order>> call = apiService.getCustomerOrders(token);

        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderAdapter.setOrders(response.body());
                } else {
                    Toast.makeText(OrderTrackingActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(OrderTrackingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onOrderClick(Order order) {
        // Open chat for this order
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("order_id", order.getId());
        intent.putExtra("shop_name", order.getShopName());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders(); // Refresh orders when coming back to this activity
    }
}
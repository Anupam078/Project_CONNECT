package com.projectconnect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.projectconnect.R;
import com.projectconnect.adapter.ProductAdapter;
import com.projectconnect.model.MessageResponse;
import com.projectconnect.model.OrderRequest;
import com.projectconnect.model.Product;
import com.projectconnect.network.ApiClient;
import com.projectconnect.network.ApiService;
import com.projectconnect.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopDetailsActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private TextView tvShopName;
    private RecyclerView rvProducts;
    private Button btnPlaceOrder;
    private ProductAdapter productAdapter;
    private ApiService apiService;
    private SharedPreferencesHelper sharedPrefsHelper;
    private Long shopId;
    private String shopName;
    private List<OrderRequest.OrderItemRequest> orderItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
        initServices();
        getIntentData();
        setupRecyclerView();
        loadProducts();
        setupClickListeners();
    }

    private void initViews() {
        tvShopName = findViewById(R.id.tvShopName);
        rvProducts = findViewById(R.id.rvProducts);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
    }

    private void initServices() {
        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPrefsHelper = new SharedPreferencesHelper(this);
    }

    private void getIntentData() {
        shopId = getIntent().getLongExtra("shop_id", -1);
        shopName = getIntent().getStringExtra("shop_name");
        tvShopName.setText(shopName);
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, this);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(productAdapter);
    }

    private void loadProducts() {
        String token = "Bearer " + sharedPrefsHelper.getToken();
        Call<List<Product>> call = apiService.getShopProducts(token, shopId);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productAdapter.setProducts(response.body());
                } else {
                    Toast.makeText(ShopDetailsActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ShopDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    @Override
    public void onAddToCart(Product product, int quantity) {
        // Check if product is already in order items
        boolean found = false;
        for (OrderRequest.OrderItemRequest item : orderItems) {
            if (item.getProductId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            OrderRequest.OrderItemRequest orderItem = new OrderRequest.OrderItemRequest();
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(quantity);
            orderItems.add(orderItem);
        }

        Toast.makeText(this, quantity + " " + product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
        updateOrderButton();
    }

    private void updateOrderButton() {
        int totalItems = 0;
        for (OrderRequest.OrderItemRequest item : orderItems) {
            totalItems += item.getQuantity();
        }

        if (totalItems > 0) {
            btnPlaceOrder.setText("Place Order (" + totalItems + " items)");
            btnPlaceOrder.setEnabled(true);
        } else {
            btnPlaceOrder.setText("Place Order");
            btnPlaceOrder.setEnabled(false);
        }
    }

    private void placeOrder() {
        if (orderItems.isEmpty()) {
            Toast.makeText(this, "Please add items to cart", Toast.LENGTH_SHORT).show();
            return;
        }

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setShopId(shopId);
        orderRequest.setItems(orderItems);

        String token = "Bearer " + sharedPrefsHelper.getToken();
        Call<MessageResponse> call = apiService.placeOrder(token, orderRequest);

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ShopDetailsActivity.this, "Order placed successfully!", Toast.LENGTH_LONG).show();
                    orderItems.clear();
                    updateOrderButton();
                } else {
                    Toast.makeText(ShopDetailsActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(ShopDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
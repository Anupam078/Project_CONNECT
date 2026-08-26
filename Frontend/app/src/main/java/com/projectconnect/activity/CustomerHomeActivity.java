package com.projectconnect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.projectconnect.R;
import com.projectconnect.model.Shop;
import com.projectconnect.network.ApiClient;
import com.projectconnect.network.ApiService;
import com.projectconnect.util.SharedPreferencesHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerHomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ApiService apiService;
    private SharedPreferencesHelper sharedPrefsHelper;
    private List<Shop> shopList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initServices();
        initMap();
        loadShops();
    }

    private void initServices() {
        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPrefsHelper = new SharedPreferencesHelper(this);
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set default location (you can change this)
        LatLng defaultLocation = new LatLng(37.7749, -122.4194); // San Francisco
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        // Set marker click listener
        mMap.setOnMarkerClickListener(marker -> {
            Shop shop = (Shop) marker.getTag();
            if (shop != null) {
                Intent intent = new Intent(CustomerHomeActivity.this, ShopDetailsActivity.class);
                intent.putExtra("shop_id", shop.getId());
                intent.putExtra("shop_name", shop.getShopName());
                startActivity(intent);
            }
            return true;
        });
    }

    private void loadShops() {
        String token = "Bearer " + sharedPrefsHelper.getToken();
        Call<List<Shop>> call = apiService.getAllShops(token);

        call.enqueue(new Callback<List<Shop>>() {
            @Override
            public void onResponse(Call<List<Shop>> call, Response<List<Shop>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shopList = response.body();
                    displayShopsOnMap();
                } else {
                    Toast.makeText(CustomerHomeActivity.this, "Failed to load shops", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Shop>> call, Throwable t) {
                Toast.makeText(CustomerHomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayShopsOnMap() {
        if (mMap != null && shopList != null) {
            for (Shop shop : shopList) {
                if (shop.getLatitude() != null && shop.getLongitude() != null) {
                    LatLng shopLocation = new LatLng(
                            shop.getLatitude().doubleValue(),
                            shop.getLongitude().doubleValue()
                    );

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(shopLocation)
                            .title(shop.getShopName())
                            .snippet(shop.getAddress() + " - " + shop.getStatus());

                    Marker marker = mMap.addMarker(markerOptions);
                    if (marker != null) {
                        marker.setTag(shop);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_orders) {
            startActivity(new Intent(this, OrderTrackingActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        sharedPrefsHelper.clearUserData();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
package com.projectconnect.network;

import com.projectconnect.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    
    // Auth endpoints
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/auth/signup")
    Call<MessageResponse> signup(@Body SignupRequest signupRequest);

    // Customer endpoints
    @GET("api/customer/shops")
    Call<List<Shop>> getAllShops(@Header("Authorization") String token);

    @GET("api/customer/shops/{shopId}/products")
    Call<List<Product>> getShopProducts(@Header("Authorization") String token, @Path("shopId") Long shopId);

    @POST("api/customer/orders")
    Call<MessageResponse> placeOrder(@Header("Authorization") String token, @Body OrderRequest orderRequest);

    @GET("api/customer/orders")
    Call<List<Order>> getCustomerOrders(@Header("Authorization") String token);

    // Seller endpoints
    @POST("api/seller/products")
    Call<MessageResponse> addProduct(@Header("Authorization") String token, @Body ProductRequest productRequest);

    @GET("api/seller/products")
    Call<List<Product>> getSellerProducts(@Header("Authorization") String token);

    @PUT("api/seller/products/{productId}")
    Call<MessageResponse> updateProduct(@Header("Authorization") String token, @Path("productId") Long productId, @Body ProductRequest productRequest);

    @DELETE("api/seller/products/{productId}")
    Call<MessageResponse> deleteProduct(@Header("Authorization") String token, @Path("productId") Long productId);

    @GET("api/seller/orders")
    Call<List<Order>> getSellerOrders(@Header("Authorization") String token);

    @PUT("api/seller/orders/{orderId}/status")
    Call<MessageResponse> updateOrderStatus(@Header("Authorization") String token, @Path("orderId") Long orderId, @Body UpdateOrderStatusRequest request);
}
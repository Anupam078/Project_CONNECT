package com.projectconnect.project_connect_backend.controller;

import com.projectconnect.project_connect_backend.dto.ProductRequest;
import com.projectconnect.project_connect_backend.entity.Order;
import com.projectconnect.project_connect_backend.entity.Product;
import com.projectconnect.project_connect_backend.security.JwtUtil;
import com.projectconnect.project_connect_backend.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/seller")
@CrossOrigin(origins = "*")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private JwtUtil jwtUtil;

    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractUserId(token);
    }

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody ProductRequest request,
                                            HttpServletRequest httpRequest) {
        try {
            Long sellerId = getCurrentUserId(httpRequest);
            Product product = sellerService.addProduct(sellerId, request);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts(HttpServletRequest httpRequest) {
        try {
            Long sellerId = getCurrentUserId(httpRequest);
            List<Product> products = sellerService.getSellerProducts(sellerId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId,
                                               @RequestBody ProductRequest request,
                                               HttpServletRequest httpRequest) {
        try {
            Long sellerId = getCurrentUserId(httpRequest);
            Product product = sellerService.updateProduct(sellerId, productId, request);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId,
                                            HttpServletRequest httpRequest) {
        try {
            Long sellerId = getCurrentUserId(httpRequest);
            sellerService.deleteProduct(sellerId, productId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrders(HttpServletRequest httpRequest) {
        try {
            Long sellerId = getCurrentUserId(httpRequest);
            List<Order> orders = sellerService.getSellerOrders(sellerId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId,
                                                  @RequestParam Order.OrderStatus status,
                                                  HttpServletRequest httpRequest) {
        try {
            Long sellerId = getCurrentUserId(httpRequest);
            Order order = sellerService.updateOrderStatus(sellerId, orderId, status);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
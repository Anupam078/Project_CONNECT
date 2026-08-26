package com.projectconnect.project_connect_backend.controller;

import com.projectconnect.project_connect_backend.dto.OrderRequest;
import com.projectconnect.project_connect_backend.entity.Order;
import com.projectconnect.project_connect_backend.entity.Product;
import com.projectconnect.project_connect_backend.entity.Shop;
import com.projectconnect.project_connect_backend.security.JwtUtil;
import com.projectconnect.project_connect_backend.service.CustomerService;
import jakarta.persistence.PrePersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private JwtUtil jwtUtil;

    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractUserId(token);
    }

    @GetMapping("/shops")
    public ResponseEntity<List<Shop>> getAllShops() {
        try {
            List<Shop> shops = customerService.getAllShops();
            return ResponseEntity.ok(shops);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/shops/{shopId}/products")
    public ResponseEntity<List<Product>> getShopProducts(@PathVariable Long shopId) {
        try {
            List<Product> products = customerService.getShopProducts(shopId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest request,
                                          HttpServletRequest httpRequest) {
        try {
            Long customerId = getCurrentUserId(httpRequest);
            Order order = customerService.placeOrder(customerId, request);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrders(HttpServletRequest httpRequest) {
        try {
            Long customerId = getCurrentUserId(httpRequest);
            List<Order> orders = customerService.getCustomerOrders(customerId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

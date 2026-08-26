package com.projectconnect.project_connect_backend.service;


import com.projectconnect.project_connect_backend.dto.ProductRequest;
import com.projectconnect.project_connect_backend.entity.Order;
import com.projectconnect.project_connect_backend.entity.Product;
import com.projectconnect.project_connect_backend.entity.Shop;
import com.projectconnect.project_connect_backend.repository.OrderRepository;
import com.projectconnect.project_connect_backend.repository.ProductRepository;
import com.projectconnect.project_connect_backend.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Product addProduct(Long sellerId, ProductRequest request) {
        Shop shop = shopRepository.findByUserId(sellerId)
                .orElseThrow(() -> new RuntimeException("Shop not found for seller"));

        Product product = new Product();
        product.setShopId(shop.getId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        return productRepository.save(product);
    }

    public List<Product> getSellerProducts(Long sellerId) {
        Shop shop = shopRepository.findByUserId(sellerId)
                .orElseThrow(() -> new RuntimeException("Shop not found for seller"));
        return productRepository.findByShopId(shop.getId());
    }

    public Product updateProduct(Long sellerId, Long productId, ProductRequest request) {
        Shop shop = shopRepository.findByUserId(sellerId)
                .orElseThrow(() -> new RuntimeException("Shop not found for seller"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getShopId().equals(shop.getId())) {
            throw new RuntimeException("Product does not belong to this seller");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        return productRepository.save(product);
    }

    public void deleteProduct(Long sellerId, Long productId) {
        Shop shop = shopRepository.findByUserId(sellerId)
                .orElseThrow(() -> new RuntimeException("Shop not found for seller"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getShopId().equals(shop.getId())) {
            throw new RuntimeException("Product does not belong to this seller");
        }

        productRepository.delete(product);
    }

    public List<Order> getSellerOrders(Long sellerId) {
        Shop shop = shopRepository.findByUserId(sellerId)
                .orElseThrow(() -> new RuntimeException("Shop not found for seller"));
        return orderRepository.findByShopId(shop.getId());
    }

    public Order updateOrderStatus(Long sellerId, Long orderId, Order.OrderStatus status) {
        Shop shop = shopRepository.findByUserId(sellerId)
                .orElseThrow(() -> new RuntimeException("Shop not found for seller"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getShopId().equals(shop.getId())) {
            throw new RuntimeException("Order does not belong to this seller");
        }

        order.setStatus(status);
        return orderRepository.save(order);
    }
}

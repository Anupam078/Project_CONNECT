package com.projectconnect.project_connect_backend.service;

import com.projectconnect.project_connect_backend.dto.OrderRequest;
import com.projectconnect.project_connect_backend.entity.*;
import com.projectconnect.project_connect_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    public List<Product> getShopProducts(Long shopId) {
        return productRepository.findByShopId(shopId);
    }

    @Transactional
    public Order placeOrder(Long customerId, OrderRequest request) {
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setShopId(request.getShopId());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderRequest.OrderItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        for (OrderRequest.OrderItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId()).get();
            OrderItem orderItem = new OrderItem(order.getId(), item.getProductId(), 
                                               item.getQuantity(), product.getPrice());
            orderItemRepository.save(orderItem);
        }

        return order;
    }

    public List<Order> getCustomerOrders(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
}

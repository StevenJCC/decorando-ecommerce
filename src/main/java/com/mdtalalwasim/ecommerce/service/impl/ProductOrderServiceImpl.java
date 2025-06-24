package com.mdtalalwasim.ecommerce.service.impl;

import com.mdtalalwasim.ecommerce.dto.ProductSalesDto;
import com.mdtalalwasim.ecommerce.entity.Cart;
import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import com.mdtalalwasim.ecommerce.entity.ProductOrderRequest;
import com.mdtalalwasim.ecommerce.repository.CartRepository;
import com.mdtalalwasim.ecommerce.repository.ProductOrderRepository;
import com.mdtalalwasim.ecommerce.service.ProductOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public ProductOrder saveProductOrder(Long userId, ProductOrderRequest productOrderRequest) {
        List<Cart> listOfCarts = cartRepository.findByUserId(userId);
        ProductOrder lastSavedOrder = null;

        for (Cart cart : listOfCarts) {
            ProductOrder order = new ProductOrder();
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(new Date());
            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice());
            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());
            order.setStatus("PENDIENTE");
            order.setPaymentType(productOrderRequest.getPaymentType());

            lastSavedOrder = productOrderRepository.save(order);
        }
        return lastSavedOrder;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return productOrderRepository.findAll();
    }

    @Override
    public void updateOrderStatus(Long id, String status) {
        Optional<ProductOrder> optionalOrder = productOrderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            ProductOrder order = optionalOrder.get();
            order.setStatus(status);
            productOrderRepository.save(order);
        }
    }

    @Override
    public List<ProductSalesDto> getTopSellingProducts(int limit) {
        return productOrderRepository.findTopSelling(limit);
    }
}

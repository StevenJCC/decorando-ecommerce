package com.mdtalalwasim.ecommerce.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.mdtalalwasim.ecommerce.entity.Cart;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.repository.CartRepository;
import com.mdtalalwasim.ecommerce.repository.ProductOrderRepository;
import com.mdtalalwasim.ecommerce.repository.ProductRepository;
import com.mdtalalwasim.ecommerce.repository.UserRepository;
import com.mdtalalwasim.ecommerce.service.CartService;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductOrderRepository productOrderRepository;

    @Override
    public Cart saveCart(Long productId, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (user == null || product == null) return null;

        int stockDisponible = product.getProductStock();
        Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

        Cart cart;

        if (ObjectUtils.isEmpty(cartStatus)) {
            if (stockDisponible < 1) return null;

            cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(1);
            cart.setTotalPrice(product.getDiscountPrice());
        } else {
            int nuevaCantidad = cartStatus.getQuantity() + 1;
            if (nuevaCantidad > stockDisponible) return null;

            cart = cartStatus;
            cart.setQuantity(nuevaCantidad);
            cart.setTotalPrice(nuevaCantidad * product.getDiscountPrice());
        }

        return cartRepository.save(cart);
    }

    @Override
    public List<Cart> getCartsByUser(Long userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        Double totalOrderPrice = 0.0;
        List<Cart> updatedCartList = new ArrayList<>();

        for (Cart cart : carts) {
            Double totalPrice = cart.getProduct().getDiscountPrice() * cart.getQuantity();
            cart.setTotalPrice(totalPrice);
            totalOrderPrice += totalPrice;
            cart.setTotalOrderPrice(totalOrderPrice);
            updatedCartList.add(cart);
        }
        return updatedCartList;
    }

    @Override
    public Long getCounterCart(Long userId) {
        return cartRepository.countByUserId(userId);
    }

    @Override
    public Boolean updateCartQuantity(String symbol, Long cartId) {
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            int quantity = cart.getQuantity();

            if (symbol.equalsIgnoreCase("decrease")) {
                quantity--;
                if (quantity <= 0) {
                    cartRepository.deleteById(cartId);
                    return true;
                }
            } else {
                Product product = cart.getProduct();
                if (quantity + 1 > product.getProductStock()) return false;
                quantity++;
            }

            cart.setQuantity(quantity);
            cart.setTotalPrice(quantity * cart.getProduct().getDiscountPrice());
            cartRepository.save(cart);
            return true;
        }
        return false;
    }

    @Override
    public boolean realizarPedidoSimple(User user) {
        List<Cart> carts = cartRepository.findByUserId(user.getId());
        if (carts == null || carts.isEmpty()) return false;

        for (Cart cart : carts) {
            Product product = cart.getProduct();

            if (product.getProductStock() < cart.getQuantity()) {
                return false;
            }

            ProductOrder order = new ProductOrder();
            order.setUser(user);
            order.setProduct(product);
            order.setQuantity(cart.getQuantity());
            order.setPrice(cart.getTotalPrice());
            order.setOrderDate(new Date());
            order.setStatus("PENDING");
            order.setOrderId(UUID.randomUUID().toString());
            order.setPaymentType("CASH");

            productOrderRepository.save(order);

            product.setProductStock(product.getProductStock() - cart.getQuantity());
            productRepository.save(product);
        }

        cartRepository.deleteAll(carts);
        return true;
    }

    // ✅ Nuevo método implementado correctamente:
    @Override
    public Long getTotalCartQuantity(Long userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        return carts.stream().mapToLong(Cart::getQuantity).sum();
    }
}

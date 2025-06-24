package com.mdtalalwasim.ecommerce.service;

import java.util.List;

import com.mdtalalwasim.ecommerce.entity.Cart;
import com.mdtalalwasim.ecommerce.entity.User;

public interface CartService {

    Cart saveCart(Long productId, Long userId);
    
    List<Cart> getCartsByUser(Long userId);
    
    Long getCounterCart(Long userId);
    
    Boolean updateCartQuantity(String symbol, Long cartId);  // <-- aquí el Boolean (ojo la B mayúscula)
    
    boolean realizarPedidoSimple(User user);

    Long getTotalCartQuantity(Long userId);
}

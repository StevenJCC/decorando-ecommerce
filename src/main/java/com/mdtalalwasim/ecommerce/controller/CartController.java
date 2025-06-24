package com.mdtalalwasim.ecommerce.controller;

import com.mdtalalwasim.ecommerce.entity.Cart;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.service.CartService;
import com.mdtalalwasim.ecommerce.service.ProductService;
import com.mdtalalwasim.ecommerce.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

@PostMapping("/add")
public String addToCart(@RequestParam("productId") Long productId,
                         @RequestParam("quantity") Integer quantity,
                         HttpSession session) {

    // 🚩 Aquí consultamos el usuario temporal o anónimo de la sesión
    Long userId = (Long) session.getAttribute("customerId");

    if (userId == null) {
        // Si no existe un cliente aún, podemos simular que está "registrado en sesión"
        // Por ahora le damos un id de prueba (ejemplo 1)
        userId = 1L;
        session.setAttribute("customerId", userId);
    }

    Product product = productService.getProductById(productId);

    if (product == null) {
        session.setAttribute("errorMsg", "Producto no encontrado");
        return "redirect:/products";
    }

    if (quantity <= 0 || quantity > product.getProductStock()) {
        session.setAttribute("errorMsg", "Cantidad inválida");
        return "redirect:/products";
    }

    cartService.saveCart(productId, userId);
    session.setAttribute("successMsg", "Producto agregado al carrito.");
    return "redirect:/products";
}


}

package com.mdtalalwasim.ecommerce.controller;

import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.service.CartService;
import com.mdtalalwasim.ecommerce.service.CategoryService;
import com.mdtalalwasim.ecommerce.service.ProductService;
import com.mdtalalwasim.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @GetMapping
    public String listProducts(Model model, Principal principal, String category) {

        // Manejo del usuario logueado
        if (principal != null) {
            String email = principal.getName();
            User currentUser = userService.getUserByEmail(email);
            model.addAttribute("currentLoggedInUserDetails", currentUser);
            Long countCartForUser = cartService.getTotalCartQuantity(currentUser.getId());
            model.addAttribute("countCartForUser", countCartForUser);
        }

        // Cargar categorías
        List<Category> allActiveCategory = categoryService.findAllActiveCategory();
        model.addAttribute("allActiveCategory", allActiveCategory);

        // Cargar productos
        List<Product> productList;
        if (StringUtils.hasText(category)) {
            productList = productService.getProductByCategory(category);
            model.addAttribute("paramValue", category);
        } else {
            productList = productService.getAllActiveProducts();
            model.addAttribute("paramValue", "");
        }
        model.addAttribute("allActiveProducts", productList);

        return "product";
    }
}

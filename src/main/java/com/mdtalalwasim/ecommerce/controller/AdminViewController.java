package com.mdtalalwasim.ecommerce.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mdtalalwasim.ecommerce.entity.*;
import com.mdtalalwasim.ecommerce.service.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @Autowired private CategoryService categoryService;
    @Autowired private ProductService productService;
    @Autowired private UserService userService;
    @Autowired private CartService cartService;
    @Autowired private ProductOrderService productOrderService;

    // Cargar datos del usuario logueado
    @ModelAttribute
    public void getUserDetails(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            User user = userService.getUserByEmail(email);
            model.addAttribute("currentLoggedInUserDetails", user);
            Long cartCount = cartService.getCounterCart(user.getId());
            model.addAttribute("countCartForUser", cartCount);
        }
        model.addAttribute("allActiveCategory", categoryService.findAllActiveCategory());
    }

    // DASHBOARD PRINCIPAL
    @GetMapping("/")
    public String adminIndex() {
        return "admin/admin-dashboard";
    }

    // ---------------- CATEGORÍAS -----------------
    @GetMapping("/category")
    public String category(Model model) {
        model.addAttribute("allCategoryList", categoryService.getAllCategories());
        return "admin/category/category-home";
    }

    @GetMapping("/add-category")
    public String addCategory(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category/category-add-form";
    }

    @PostMapping("/save-category")
    public String saveCategory(@ModelAttribute Category category, @RequestParam MultipartFile file, HttpSession session) throws IOException {
        String imageName = (!file.isEmpty()) ? file.getOriginalFilename() : "default.jpg";
        category.setCategoryImage(imageName);
        category.setIsActive(true);
        categoryService.saveCategory(category);
        if (!file.isEmpty()) {
            File saveFile = new ClassPathResource("static/img/category").getFile();
            if (!saveFile.exists()) saveFile.mkdirs();
            Path path = Path.of(saveFile.getAbsolutePath(), file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }
        session.setAttribute("successMsg", "Categoría guardada correctamente");
        return "redirect:/admin/category";
    }

    @GetMapping("/edit-category/{id}")
    public String editCategory(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id).orElse(null);
        model.addAttribute("category", category);
        return "admin/category/category-edit-form";
    }

    @PostMapping("/update-category")
    public String updateCategory(@ModelAttribute Category category, @RequestParam MultipartFile file, HttpSession session) throws IOException {
        Category dbCategory = categoryService.findById(category.getId()).orElseThrow();
        dbCategory.setCategoryName(category.getCategoryName());
        dbCategory.setIsActive(category.getIsActive());
        if (!file.isEmpty()) {
            String imageName = file.getOriginalFilename();
            dbCategory.setCategoryImage(imageName);
            File saveFile = new ClassPathResource("static/img/category").getFile();
            if (!saveFile.exists()) saveFile.mkdirs();
            Path path = Path.of(saveFile.getAbsolutePath(), imageName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }
        categoryService.saveCategory(dbCategory);
        session.setAttribute("successMsg", "Categoría actualizada correctamente");
        return "redirect:/admin/category";
    }

    @GetMapping("/delete-category/{id}")
    public String deleteCategory(@PathVariable Long id, HttpSession session) {
        boolean deleted = categoryService.deleteCategory(id);
        session.setAttribute(deleted ? "successMsg" : "errorMsg", deleted ? "Categoría eliminada" : "Error eliminando");
        return "redirect:/admin/category";
    }

    // ---------------- PRODUCTOS -----------------
    @GetMapping("/product-list")
    public String productList(Model model) {
        model.addAttribute("productList", productService.getAllProducts());
        return "admin/product/product-list";
    }

    @GetMapping("/add-product")
    public String addProduct(Model model) {
        model.addAttribute("allCategoryList", categoryService.getAllCategories());
        model.addAttribute("product", new Product());
        return "admin/product/add-product";
    }

    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute Product product, @RequestParam MultipartFile file, HttpSession session) throws IOException {
        String imageName = (!file.isEmpty()) ? file.getOriginalFilename() : "default.png";
        product.setProductImage(imageName);
        product.setDiscountPrice(product.getProductPrice());
        productService.saveProduct(product);
        if (!file.isEmpty()) {
            File saveFile = new ClassPathResource("static/img/product_image").getFile();
            if (!saveFile.exists()) saveFile.mkdirs();
            Path path = Path.of(saveFile.getAbsolutePath(), imageName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }
        session.setAttribute("successMsg", "Producto guardado correctamente");
        return "redirect:/admin/product-list";
    }

    @GetMapping("/edit-product/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("allCategoryList", categoryService.getAllCategories());
        return "admin/product/edit-product";
    }

    @PostMapping("/update-product")
    public String updateProduct(@ModelAttribute Product product, @RequestParam MultipartFile file, HttpSession session) throws IOException {
        Product dbProduct = productService.getProductById(product.getId());
        dbProduct.setProductTitle(product.getProductTitle());
        dbProduct.setProductDescription(product.getProductDescription());
        dbProduct.setProductCategory(product.getProductCategory());
        dbProduct.setProductPrice(product.getProductPrice());
        dbProduct.setDiscount(product.getDiscount());
        dbProduct.setProductStock(product.getProductStock());
        dbProduct.setIsActive(product.getIsActive());

        double discountPrice = dbProduct.getProductPrice() - (dbProduct.getProductPrice() * dbProduct.getDiscount() / 100.0);
        dbProduct.setDiscountPrice(discountPrice);

        if (!file.isEmpty()) {
            String imageName = file.getOriginalFilename();
            dbProduct.setProductImage(imageName);
            File saveFile = new ClassPathResource("static/img/product_image").getFile();
            if (!saveFile.exists()) saveFile.mkdirs();
            Path path = Path.of(saveFile.getAbsolutePath(), imageName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }

        productService.saveProduct(dbProduct);
        session.setAttribute("successMsg", "Producto actualizado correctamente");
        return "redirect:/admin/product-list";
    }

    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable long id, HttpSession session) {
        Boolean deleted = productService.deleteProduct(id);
        session.setAttribute(deleted ? "successMsg" : "errorMsg", deleted ? "Producto eliminado." : "Error al eliminar");
        return "redirect:/admin/product-list";
    }

    // ---------------- USUARIOS -----------------
    @GetMapping("/get-all-users")
    public String getAllUsers(Model model) {
        List<User> allUsers = userService.getAllUsersByRole("ROLE_USER");
        model.addAttribute("userData", allUsers);
        return "admin/users/user-home";
    }

    @GetMapping("/add-user")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/users/add-user";
    }

    @PostMapping("/save-user")
    public String saveUser(@ModelAttribute User user, @RequestParam("profileImageFile") MultipartFile file, HttpSession session) throws IOException {
        String imageName = (!file.isEmpty()) ? file.getOriginalFilename() : "default.jpg";
        user.setProfileImage(imageName);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setIsEnable(true);
        user.setAccountStatusNonLocked(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userService.saveUser(user);
        if (!file.isEmpty()) {
            File saveFile = new ClassPathResource("static/img/profile").getFile();
            if (!saveFile.exists()) saveFile.mkdirs();
            Path path = Path.of(saveFile.getAbsolutePath(), file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }
        session.setAttribute("successMsg", "Usuario guardado correctamente.");
        return "redirect:/admin/get-all-users";
    }

    @GetMapping("/edit-user/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/users/edit-user";
    }

@PostMapping("/update-user")
public String updateUser(@ModelAttribute User user,
                         @RequestParam("profileImageFile") MultipartFile file,
                         HttpSession session) {  // <-- ESTE PARÁMETRO ES NECESARIO
    try {
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()
         || user.getLastName() == null || user.getLastName().trim().isEmpty()
         || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            session.setAttribute("errorMsg", "Nombre, apellido y correo no pueden estar vacíos.");
            return "redirect:/admin/get-all-users";
        }

        User dbUser = userService.getUserById(user.getId());
        dbUser.setFirstName(user.getFirstName());
        dbUser.setLastName(user.getLastName());
        dbUser.setEmail(user.getEmail());
        dbUser.setRole(user.getRole());
        dbUser.setIsEnable(user.getIsEnable());
        dbUser.setAccountStatusNonLocked(user.getAccountStatusNonLocked());

        if (file != null && !file.isEmpty()) {
            String imageName = file.getOriginalFilename();
            if (!imageName.toLowerCase().matches(".*\\.(png|jpg|jpeg|gif)$")) {
                session.setAttribute("errorMsg", "Formato de imagen no válido. Usa PNG, JPG o JPEG.");
                return "redirect:/admin/get-all-users";
            }

            dbUser.setProfileImage(imageName);
            File saveDir = new ClassPathResource("static/img/profile").getFile();
            if (!saveDir.exists()) saveDir.mkdirs();

            Path path = Path.of(saveDir.getAbsolutePath(), imageName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }

        userService.saveUser(dbUser);
        session.setAttribute("successMsg", "Usuario actualizado correctamente.");
        return "redirect:/admin/get-all-users";

    } catch (Exception e) {
        e.printStackTrace();
        session.setAttribute("errorMsg", "Error al actualizar el usuario.");
        return "redirect:/admin/get-all-users";
    }
}



    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        try {
            userService.deleteUserById(id);
            session.setAttribute("successMsg", "Usuario eliminado correctamente");
        } catch (Exception e) {
            session.setAttribute("errorMsg", "No se pudo eliminar el usuario");
        }
        return "redirect:/admin/get-all-users";
    }

    // ---------------- PEDIDOS -----------------



}
